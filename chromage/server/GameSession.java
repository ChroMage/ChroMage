package chromage.server;

import chromage.shared.Constants;
import chromage.shared.GameState;
import chromage.shared.Mage;
import chromage.shared.RateLimitedLoop;

import java.util.ArrayList;

public class GameSession extends Thread {

	private GameState state;
	private String name;
	private ArrayList<PlayerThread> players;

	public void setExpectedNumberOfPlayers(int expectedNumberOfPlayers) {
		this.expectedNumberOfPlayers = expectedNumberOfPlayers;
	}

	private int expectedNumberOfPlayers;
	private long currentTick;
	private boolean isGameRunning;

	public GameState getGameState() { return state; }

	public GameSession(String name) {
		this.name = name;
		players = new ArrayList<PlayerThread>();
		isGameRunning = true;
		state = new GameState();
	}

	public String getGameName() {
		return name;
	}

	public boolean isGameRunning() {
		return isGameRunning;
	}

	public void endGame() {
		state.isGameOver = true;
	}

	public boolean isFull() {
		return players.size() == expectedNumberOfPlayers;
	}

	public void connectPlayer(PlayerThread player) {
		players.add(player);
		state.awaitedPlayers = expectedNumberOfPlayers - players.size();
	}

	public boolean allPlayersReady() {
		for (PlayerThread player : players) {
			if (!player.isReady())
				return false;
		}
		return true;
	}

	public boolean waitForPlayers() {
		while (players.size() < expectedNumberOfPlayers && !allPlayersReady()) {
			for (PlayerThread p : (ArrayList<PlayerThread>)players.clone()) {
				if (p.wantsTermination())
					return false;
			}
			System.out.println("waiting");
			// wait until all the players have joined the game.
            sendUpdates();
		}
		return true;
	}

	public void sendUpdates() {
		for (PlayerThread p : (ArrayList<PlayerThread>)players.clone()) {
			p.sendUpdate(state);
		}
	}

	public void processInput() {
		int inputTimeoutTicks = 6;
		for (PlayerThread p : players) {
			if (p.wantsTermination()) {
				// terminate if any of the players wants to.
				System.out.println("Player " + p + " wants to leave.");
				return;
			}

			p.mage.setVelocityWithInput(p.getCurrentInputState());
			if (currentTick - p.getLastUpdateTick() > inputTimeoutTicks) {
				p.resetCurrentInputState();
			}
			
			//process spell casts
			p.mage.castSpell(p.getCurrentInputState(), state);

			if (currentTick - p.getLastUpdateTick() > inputTimeoutTicks) {
				p.resetCurrentInputState();
			}
		}
	}

	public void executeGameLoop() {
		new RateLimitedLoop(Constants.TICKS_PER_SECOND) {
			public boolean shouldContinue() {
				for (PlayerThread p : (ArrayList<PlayerThread>)players.clone()) {
					if (p.wantsTermination())
						return false;
				}
				return true;
			}
			public void body() {
				processInput();
				state.update();
				sendUpdates();
			}
		}.run();
	}

	public void terminateConnections() {
		state.x = -5;
		for (PlayerThread p : players) {
			p.sendUpdate(state);
			p.terminateConnection();
		}
	}

	public void prepareGame() {
		ArrayList<Mage> mages = new ArrayList<Mage>();
		for (PlayerThread p : players) {
			mages.add(p.mage);
		}
		state.initialize(mages);
	}

	public void run() {
		System.out.println("Waiting for players to connect...");
		if (waitForPlayers()) {
			System.out.println("Starting game loop...");
			prepareGame();
			executeGameLoop();
			System.out.println("Ending game...");
		}
		terminateConnections();
	}

	public int connectedPlayers() {
		return players.size();
	}
}

package chromage.server;

import chromage.shared.*;

import java.awt.*;
import java.awt.geom.Point2D;
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
	private int jumpTick;

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
	}

	public boolean allPlayersReady() {
		for (PlayerThread player : players) {
			if (!player.isReady())
				return false;
		}
		return true;
	}

	public void waitForPlayers() {
		while (players.size() < expectedNumberOfPlayers && !allPlayersReady()) {
			System.out.println("waiting");
			// wait until all the players have joined the game.
            sendUpdates();
		}
	}

	public void sendUpdates() {
		for (PlayerThread p : (ArrayList<PlayerThread>)players.clone()) {
			p.sendUpdate(state);
		}
	}

	public void processInput() {
		System.out.println("Processing input");
		int inputTimeoutTicks = 6;
		for (PlayerThread p : players) {
			if (p.wantsTermination()) {
				// terminate if any of the players wants to.
				System.out.println("Player " + p + " wants to leave.");
				return;
			}
				System.out.println("Player " + p + " current input: " + p.getCurrentInputState());
				System.out.println("Ticks since last client update: " + (state.getCurrentTick() - p.getLastUpdateTick()));

				Point2D.Double a = accelerationForInput(p.getCurrentInputState());
				p.mage.addUpRightVelocity((int)a.x, (int)a.y);

				if (currentTick - p.getLastUpdateTick() > inputTimeoutTicks) {
					p.resetCurrentInputState();
				}
		}
	}

	public Point2D.Double accelerationForInput(UserInput input) {
		int x = 0, y = 0;
		switch (input.horizontalDirection) {
			case LEFT: x = -1; break;
            case NONE: x = 0; break;
			case RIGHT: x = 1; break;
		}
		switch (input.verticalDirection) {

			case JUMP:
				if(jumpTick > 0) {
					jumpTick--;		
					y = 3;
				}
				else 
					y = 0;
				break;
			case NONE: y = 0; jumpTick = 8; break;

		}
		return new Point2D.Double(x,y);
	}

	public void executeGameLoop() {
		new RateLimitedLoop(Constants.TICKS_PER_SECOND) {
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
			p.mage = new Mage(Color.RED);
			mages.add(p.mage);
		}
		state.initialize(mages);
	}

	public void run() {
		System.out.println("Waiting for players to connect...");
		waitForPlayers();
		System.out.println("Starting game loop...");

		prepareGame();

		executeGameLoop();
		System.out.println("Ending game...");
		terminateConnections();
	}

	public int connectedPlayers() {
		return players.size();
	}
}

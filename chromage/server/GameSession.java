package chromage.server;

import chromage.shared.Constants;
import chromage.shared.GameState;
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
		for (PlayerThread p : players) {
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
			if (p.getCurrentInputState() != 0) {
				System.out.println("Player " + p + " current input: " + p.getCurrentInputState());
				System.out.println("Ticks since last client update: " + (state.getCurrentTick() - p.getLastUpdateTick()));
				modifyState(Integer.toString(p.getCurrentInputState()));
				if (currentTick - p.getLastUpdateTick() > inputTimeoutTicks) {
					p.resetCurrentInputState();
				}
			}
		}
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

	private void modifyState(String s) {
		try{
			int clientKeys = Integer.parseInt(s);
//			if((clientKeys & Actions.UP) != 0){
//				state.y--;
//			}
//			if((clientKeys & Actions.LEFT) != 0){
//				state.x--;
//			}
//			if((clientKeys & Actions.DOWN) != 0){
//				state.y++;
//			}
//			if((clientKeys & Actions.RIGHT) != 0){
//				state.x++;
//			}
//			if((clientKeys & Actions.JUMP) != 0){
//				state.x = 100;
//				state.y = 100;
//			}
//			if(state.x < 0){
//				state.x = 0;
//			}
//			if(state.y < 0){
//				state.y = 0;
//			}
//			if(state.x > Constants.MAX_WIDTH){
//				state.x = Constants.MAX_WIDTH;
//			}
//			if(state.y > Constants.MAX_HEIGHT){
//				state.y = Constants.MAX_HEIGHT;
//			}
		}
		catch(NumberFormatException e){}
	}

	public void run() {
		System.out.println("Waiting for players to connect...");
		waitForPlayers();
		System.out.println("Starting game loop...");

		executeGameLoop();
		System.out.println("Ending game...");
		terminateConnections();
	}
}

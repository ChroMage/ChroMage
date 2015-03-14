package chromage.server;

import chromage.server.PlayerThread;
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
				p.mage.setVelocityWithInput(p.getCurrentInputState());
				if (currentTick - p.getLastUpdateTick() > inputTimeoutTicks) {
					p.resetCurrentInputState();
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
}

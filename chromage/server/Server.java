package chromage.server;
import chromage.shared.Actions;
import chromage.shared.Constants;
import chromage.shared.GameState;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread {

	/**
	 * The port the server is listening on.
	 */
	int port;

	private GameState state;
	private ServerSocket socket;

	private ArrayList<PlayerThread> players;

	private boolean isGameRunning;

	public GameState getGameState() { return state; }

	public Server() {
		port = 9877;
		players = new ArrayList<PlayerThread>();
		isGameRunning = true;
		state = new GameState();
	}
	
	public static void main(String args[]) throws IOException {
		// TODO: make port configurable
		Server server = new Server();
		server.setPriority(10);
		server.start();
	}

	public boolean isGameRunning() {
		return isGameRunning;
	}

	public void endGame() {
		state.isGameOver = true;
	}

	public void waitForPlayers() {
		int expectedNumberOfPlayers = 2;
		int numberOfPlayers = 0;
		while (numberOfPlayers < expectedNumberOfPlayers) {
			try {
				// wait for a new player to connect
				Socket connectionSocket = socket.accept();

				// create new thread to listen for each new player's updates
				PlayerThread newPlayer = new PlayerThread(connectionSocket, numberOfPlayers);
				newPlayer.start();
				players.add(newPlayer);

				// track how many players have successfully connected
				numberOfPlayers++;

			} catch (IOException e) {
				// if someone starts to connect but has an issue, log it but keep listening
				e.printStackTrace();
				return;
			}
		}
	}

	public void executeGameLoop() {
		int desiredTickLengthMillis = 1000 / 120;
		int inputTimeoutTicks = 6;

		int moveFactor = 3;
		while (true) {
			long startTime = System.currentTimeMillis();

			for (PlayerThread p : players) {
				if (p.wantsTermination()) {
					// terminate if any of the players wants to.
					System.out.println("Player " + p + " wants to leave.");
					return;
				}
				// update positions, fire, etc
				if (p.getCurrentInputState() != 0) {
					System.out.println("Player " + p + " current input: " + p.getCurrentInputState());

//					for (int i = 0; i < moveFactor; ++i)
                        modifyState("" + p.getCurrentInputState());

					System.out.println("Time since last client update: " + (System.currentTimeMillis() - p.getLastUpdateTime()));
					if (System.currentTimeMillis() - p.getLastUpdateTime() > inputTimeoutTicks*desiredTickLengthMillis) {
						p.resetCurrentInputState();
					}
				}
			}
			for (PlayerThread p : players) {
                p.sendUpdate(state);
            }

			long endTime = System.currentTimeMillis();
			if (endTime - startTime < desiredTickLengthMillis) {
				try {
					Thread.sleep(desiredTickLengthMillis - (endTime - startTime));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Tick took: " + (endTime - startTime));
		}
	}

	public void terminateConnections() {
		state.x = -5;
		for (PlayerThread p : players) {
			p.sendUpdate(state);
			p.terminateConnection();
		}
	}

	public void run() {
		try {
			socket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Waiting for players to connect...");
        waitForPlayers();
		System.out.println("Starting game loop...");
        executeGameLoop();
		System.out.println("Ending game...");
        terminateConnections();

		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void modifyState(String s) {
		try{
			int clientKeys = Integer.parseInt(s);
			if((clientKeys & Actions.UP) != 0){
				state.y--;
			}
			if((clientKeys & Actions.LEFT) != 0){
				state.x--;
			}
			if((clientKeys & Actions.DOWN) != 0){
				state.y++;
			}
			if((clientKeys & Actions.RIGHT) != 0){
				state.x++;
			}
			if((clientKeys & Actions.JUMP) != 0){
				state.x = 100;
				state.y = 100;
			}
			if(state.x < 0){
				state.x = 0;
			}
			if(state.y < 0){
				state.y = 0;
			}
			if(state.x > Constants.MAX_WIDTH){
				state.x = Constants.MAX_WIDTH;
			}
			if(state.y > Constants.MAX_HEIGHT){
				state.y = Constants.MAX_HEIGHT;
			}
		}
		catch(NumberFormatException e){}
	}
}


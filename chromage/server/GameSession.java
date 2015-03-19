package chromage.server;

import chromage.shared.utils.Constants;
import chromage.shared.engine.GameState;
import chromage.shared.Mage;
import chromage.shared.utils.RateLimitedLoop;

import java.util.ArrayList;
import java.util.UUID;

public class GameSession extends Thread {

	private GameState state;
	private String name;
	private ArrayList<PlayerThread> players;
	private Server server;
	private UUID uuid;

	public void setExpectedNumberOfPlayers(int expectedNumberOfPlayers) {
		this.expectedNumberOfPlayers = expectedNumberOfPlayers;
	}

	private int expectedNumberOfPlayers;
	private long currentTick;
	private boolean isGameRunning;

	public GameState getGameState() { return state; }

	public GameSession(String name, UUID uuid, Server server) {
		this.name = name;
		this.uuid = uuid;
		this.server = server;
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
		state.setGameOver(true);
	}

	public boolean isFull() {
		return players.size() == expectedNumberOfPlayers;
	}

	public void connectPlayer(PlayerThread player) {
		players.add(player);
		state.awaitedPlayers = expectedNumberOfPlayers - players.size();
	}

	public boolean allPlayersReady() {
		return true;
	}

	public boolean waitForPlayers() {
        return (Boolean)(new RateLimitedLoop(Constants.TICKS_PER_SECOND) {
            public Object defaultResult() { return true; }
            public boolean shouldContinue() {
                return players.size() < expectedNumberOfPlayers && !allPlayersReady();
            }
            public void body() {
                setResult(true);
                for (PlayerThread p : (ArrayList<PlayerThread>) players.clone()) {
                    if (p.wantsTermination()) {
                        setResult(false);
                        setBreak();
                    }
                }
                // wait until all the players have joined the game.
                sendUpdates();
            }
        }.runAndGetResult());
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
				if (state.shouldTerminate() || state.isGameOver()) {
					return false;
				}
				for (PlayerThread p : (ArrayList<PlayerThread>)players.clone()) {
					if (p.wantsTermination())
						return false;
				}
				return true;
			}
			public void body() {
                synchronized (state) {
                    processInput();
                    state.update();
                    sendUpdates();
                }
			}
		}.run();
	}

	public void terminateConnections() {

        // tell all the child threads to close their connections
		for (PlayerThread p : players) {
			p.sendUpdate(state);
		}

        // keep this thread alive while we wait for the child threads to terminate gracefully
        for (PlayerThread p : players) {
            try {
                p.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
		} else {
			System.out.println("Something went wrong before we could start the game.");
		}
		System.out.println("Terminating connections...");
		terminateConnections();
		System.out.println("Game session ended.");
		server.gameEnded(this);
	}

	public int connectedPlayers() {
		return players.size();
	}

	public UUID getUuid() {
		return uuid;
	}

    public int getExpectedNumberOfPlayers() {
        return expectedNumberOfPlayers;
    }
}

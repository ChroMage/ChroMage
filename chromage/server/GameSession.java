package chromage.server;

import chromage.shared.Mage;
import chromage.shared.engine.GameState;
import chromage.shared.utils.Constants;
import chromage.shared.utils.RateLimitedLoop;
import chromage.shared.utils.Timer;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Manages a game session from creation to the end of the game
 */
public class GameSession extends Thread {

    /**
     * The current state of the game we're managing
     */
    private GameState state;

    /**
     * The name of this game
     */
    private String name;
    /**
     * The list of players in the game.
     */
    private ArrayList<PlayerThread> players;

    /**
     * The server we're a part of
     */
    private Server server;

    /**
     * The unique ID assigned to this game
     */
    private UUID uuid;
    /**
     * The number of players we want in game before we start.
     */
    private int expectedNumberOfPlayers;

    public GameSession(String name, UUID uuid, Server server) {
        this.name = name;
        this.uuid = uuid;
        this.server = server;
        players = new ArrayList<PlayerThread>();
        state = new GameState();
    }

    /**
     * @return the current state of the game
     */
    public GameState getGameState() {
        return state;
    }

    public String getGameName() {
        return name;
    }

    /**
     * @return true iff the game has all the players it needs to start
     */
    public boolean isFull() {
        return players.size() == expectedNumberOfPlayers;
    }

    /**
     * Add a player to the list of players in game
     *
     * @param player the player to add
     */
    public void connectPlayer(PlayerThread player) {
        synchronized (players) {
            players.add(player);
        }
        state.setAwaitedPlayers(expectedNumberOfPlayers - players.size());
    }

    /**
     * Sends updates to all the players every tick until there are enough players in game to start playing.
     *
     * @return true if we finished with enough players to start the game, false if we terminated before getting enough
     * players
     */
    public boolean waitForPlayers() {
        return (Boolean) (new RateLimitedLoop(Constants.TICKS_PER_SECOND) {
            public Object defaultResult() {
                return true;
            }

            public boolean shouldContinue() {
                return players.size() < expectedNumberOfPlayers;
            }

            public void body() {
                setResult(true);
                synchronized (players) {
                    for (PlayerThread p : players) {
                        if (p.wantsTermination()) {
                            setResult(false);
                            setBreak();
                        }
                    }
                }
                sendUpdates();
            }
        }.runAndGetResult());
    }

    /**
     * Send an update to every player in our list
     */
    public void sendUpdates() {
        synchronized (players) {
            for (PlayerThread p : players) {
                p.sendUpdate(state);
            }
        }
    }

    /**
     * Update each player's desired actions for this tick based on their current input state
     */
    public void processInput() {
        int inputTimeoutTicks = 6;
        for (PlayerThread p : players) {
            if (p.wantsTermination()) {
                // terminate if any of the players wants to.
                System.out.println("Player " + p + " wants to leave.");
                return;
            }
            p.mage.setVelocityWithInput(p.getCurrentInputState());
            p.mage.setDesiredSpell(p.getCurrentInputState().spell);
            p.mage.setTarget(p.getCurrentInputState().mouseLocation);
            if (state.getCurrentTick() - p.getLastUpdateTick() > inputTimeoutTicks) {
                p.resetCurrentInputState();
            }
        }
    }

    /**
     * Run the main game loop.
     */
    public void executeGameLoop() {
        new RateLimitedLoop(Constants.TICKS_PER_SECOND) {
            public boolean shouldContinue() {
                if (state.shouldTerminate() || state.isGameOver()) {
                    return false;
                }
                for (PlayerThread p : (ArrayList<PlayerThread>) players.clone()) {
                    if (p.wantsTermination())
                        return false;
                }
                return true;
            }

            public void body() {
                Timer.time("whole tick", new Runnable() {
                    @Override
                    public void run() {
                    synchronized (state) {
                        Timer.time("processInput",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        processInput();
                                    }
                                });
                        Timer.time("state.update",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        state.update();
                                    }
                                });
                        Timer.time("sendUpdates",
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        sendUpdates();
                                    }
                                });
                    }
                    }
                });
            }
        }.run();
    }

    /**
     * Send an update to each player telling them the game is over, then wait
     * for all of our background threads to terminate
     */
    public void terminateConnections() {
        state.setTerminate();

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

    /**
     * Do any required pre-game initialization
     */
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

    public void setExpectedNumberOfPlayers(int expectedNumberOfPlayers) {
        this.expectedNumberOfPlayers = expectedNumberOfPlayers;
    }
}

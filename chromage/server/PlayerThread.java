package chromage.server;

import chromage.shared.engine.GameState;
import chromage.shared.Mage;
import chromage.shared.MageType;
import chromage.shared.utils.Constants;
import chromage.shared.utils.RateLimitedLoop;
import chromage.shared.utils.UserInput;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.UUID;

/**
 * Listens to a connection from a player, sending updates back to
 * the main server
 */
public class PlayerThread extends Thread {

    /**
     * the socket through which we are connected to this player.
     */
    private Socket socket;

    /**
     * The most recent input state received from the client.
     */
    private UserInput currentInputState;

    /**
     * The game tick in which we most recently got a packet from this player
     */
    private long lastUpdateTick;

    /**
     * True if the client has ever sent input requesting termination
     */
    private boolean wantsTermination;

    /**
     * True if we should keep listening to the player for input during the game
     */
    private boolean shouldKeepProcessing;

    /**
     * True iff we should keep listening to command to the lobby
     */
    private boolean shouldListenInLobby;

    /**
     * the most recent game state of which the server has informed this client
     */
    private GameState state = new GameState();

    /**
     * The server this client is connected to
     */
    private Server server;

    /**
     * The mage associated with this player
     */
    public Mage mage;

    /**
     * The input stream from the client
     */
    BufferedReader fromClient;

    /**
     * The output stream to the client
     */
    DataOutputStream toClient;

    private String playerName;

    /**
     * Create a new player thread
     * @param socket the socket on which this player is connected
     * @param server the server to which the player connected
     */
    public PlayerThread(Socket socket, Server server) {
        this.socket = socket;
        this.wantsTermination = false;
        this.lastUpdateTick = 0;
        this.server = server;
        resetCurrentInputState();
    }

    public String getPlayerName() { return playerName; }

    /**
     * Performs the whole process for a player from start to finish:
     * First it does the handshake and gets the player's name; then
     * it puts the player in the lobby and waits for them to choose a
     * game, then actually puts them in the game
     */
    public void run() {
        try {
            fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            toClient = new DataOutputStream(socket.getOutputStream());
            initiateHandshake();
            enterLobby();
            startStateSending();
            listenForUpdates();
        } catch(IOException e){
            e.printStackTrace();
        } finally {
            // try to close the socket if we can.
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends the current game state to the client every tick.
     */
    private void startStateSending() {
        new RateLimitedLoop(Constants.TICKS_PER_SECOND) {
            public boolean shouldContinue() {
                return PlayerThread.this.shouldKeepProcessing;
            }
            public void body() {
                try {
                    synchronized (state) {
                        String serialization = state.serializeToString();
                        toClient.writeBytes(serialization + '\n');
                        if (state.shouldTerminate()) {
                            terminateConnection();
                        }
                    }
                } catch (SocketException ex) {
                    server.disconnectPlayer(PlayerThread.this);
                    wantsTermination = true;
                } catch (IOException e) {
                    server.disconnectPlayer(PlayerThread.this);
                    wantsTermination = true;
                    e.printStackTrace();
                }
            }
        }.runInBackground();
    }

    /**
     * Read the player's username from the socket, then if
     * it was given in the right format, tell them welcome;
     * otherwise, disconnect them.
     *
     * @throws IOException
     */
    public void initiateHandshake() throws IOException {
        toClient.writeBytes("Enter your username.\n");
        String line = fromClient.readLine();
        String[] parts = line.split(" ");
        if (parts.length != 2) {
            toClient.writeBytes("Bad handshake. Disconnecting.\n");
            server.disconnectPlayer(this);
        }
        else {
            playerName = parts[1];
            toClient.writeBytes("Welcome.\n");
        }
    }

    /**
     * Sets the flags which make all the background processes stop.
     */
    public void terminateConnection() {
        wantsTermination = true;
        shouldKeepProcessing = false;
        shouldListenInLobby = false;
    }

    /**
     * Sets the state to be sent next time the player gets an update from the server.
     * @param state
     */
    public void sendUpdate(GameState state) {
        this.state = state;
    }

    /**
     * Listens for lobby commands until the player joins a game, then returns
     * @throws IOException
     */
    public void enterLobby() throws IOException {
        if (wantsTermination) return;
        System.out.println("Entered lobby: " + getPlayerName());
        shouldListenInLobby = true;
        while (shouldListenInLobby) {
            String clientMessage = fromClient.readLine();
            if (clientMessage == null) {
                server.disconnectPlayer(this);
            }
            System.out.println("Lobby: got message " + clientMessage);
            String[] parts = clientMessage.split(" ");
            String action = parts[0];
            if ("list".equals(action) && parts.length == 1) {
                server.sendGameList(toClient);
            }
            else if ("new".equals(action) && parts.length == 4) {
                mage = new Mage(MageType.valueOf(parts[3]));
                mage.setName(playerName);
                server.createAndJoinGame(this, parts[1], Integer.parseInt(parts[2]));
                toClient.writeBytes("success\n");
                
                break;
            }
            else if ("join".equals(action) && parts.length == 3) {
                mage = new Mage(MageType.valueOf(parts[2]));
                mage.setName(playerName);
                if (server.joinGame(this, UUID.fromString(parts[1]))) {
                    toClient.writeBytes("success\n");
                } else toClient.writeBytes("failure\n");
                
                break;
            } else {
                toClient.writeBytes("didn't understand that command\n");
            }
        }
    }

    /**
     * Listens on the socket for updates from the player about their input
     * @throws IOException
     */
    public void listenForUpdates() throws IOException {
        if (wantsTermination || state.shouldTerminate()) return;
        // Send initial connection info
        shouldKeepProcessing = true;
        while (shouldKeepProcessing) {
            String clientMessage = fromClient.readLine();
            if (clientMessage == null) break;
            try {
                UserInput userInput = UserInput.deserializeFromString(clientMessage);

                if (userInput.wantsTermination()) {
                    wantsTermination = true;
                } else {
                    currentInputState = userInput;
                    lastUpdateTick = state.getCurrentTick();
                }
            } catch (Exception e) {
                System.out.println("Exception: " + clientMessage);
                e.printStackTrace();
                server.disconnectPlayer(this);
            }
        }
        System.out.println("Terminating connection.");
    }

    public long getLastUpdateTick() { return lastUpdateTick; }
    public boolean wantsTermination() { return wantsTermination; }
    public UserInput getCurrentInputState() { return currentInputState; }
    public void resetCurrentInputState() { currentInputState = new UserInput(); }
}

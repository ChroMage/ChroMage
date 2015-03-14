package chromage.server;

import chromage.shared.GameState;
import chromage.shared.Mage;
import chromage.shared.UserInput;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.UUID;

/**
 * Listens to a connection from a player, sending updates back to
 * the main server
 */
public class PlayerThread extends Thread {

    private Socket socket;

    private UserInput currentInputState;
    private boolean wantsTermination;
    private boolean shouldKeepProcessing;
    private int playerNumber;
    private long lastUpdateTick;
    private boolean isReady;
    private GameState state = new GameState();
    private Server server;
    public Mage mage;

    BufferedReader fromClient;
    DataOutputStream toClient;

    public PlayerThread(Socket socket, Server server) {
        this.socket = socket;
        this.wantsTermination = false;
        this.lastUpdateTick = 0;
        resetCurrentInputState();
        this.server = server;
    }

    public void run() {
        try {
            fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            toClient = new DataOutputStream(socket.getOutputStream());
            initiateHandshake();
            enterLobby();
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

    public void initiateHandshake() throws IOException {
        toClient.writeBytes("Hello!" + '\n');
    }

    public void terminateConnection() {
        shouldKeepProcessing = false;
    }


    public void sendUpdate(GameState state) {
        this.state = state;
        try {
            String serialization = state.serializeToString();
//            System.out.println("Sending to " + playerNumber +": " + state.x + ", " + state.y);
            toClient.writeBytes(serialization + '\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enterLobby() throws IOException {
        System.out.println("Entered lobby");
        while (true) {
            String clientMessage = fromClient.readLine();
            System.out.println("Lobby: got message " + clientMessage);
            // TODO: actually come up with protocol for joining game etc.
            String[] parts = clientMessage.split(" ");
            String action = parts[0];
            if ("list".equals(action) && parts.length == 1) {
                server.sendGameList(toClient);
            }
            else if ("new".equals(action) && parts.length == 4) {
                server.createAndJoinGame(this, parts[1], Integer.parseInt(parts[2]));
                toClient.writeBytes("success\n");
                // TODO: setClass(parts[3]);
                break;
            }
            else if ("join".equals(action) && parts.length == 3) {
                if (server.joinGame(this, UUID.fromString(parts[1]))) {
                    toClient.writeBytes("success\n");
                } else toClient.writeBytes("failure\n");
                // TODO: setClass(parts[2]);
                break;
            }
        }
    }

    public void listenForUpdates() throws IOException {
        // Send initial connection info
        shouldKeepProcessing = true;
        while (shouldKeepProcessing) {
            String clientMessage = fromClient.readLine();
            try {
                UserInput userInput = UserInput.deserializeFromString(clientMessage);
                System.out.println("Client " + playerNumber + " sent: " + userInput.toString());
                if (clientMessage == null) {
                    break;
                }
                if (userInput.wantsTermination()) {
                    wantsTermination = true;
                } else {
                    currentInputState = userInput;
                    lastUpdateTick = state.getCurrentTick();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Terminating connection.");
    }

    public boolean isReady() {
        return isReady;
    }

    public long getLastUpdateTick() {
        return lastUpdateTick;
    }

    public boolean wantsTermination() { return wantsTermination; }
    public UserInput getCurrentInputState() { return currentInputState; }
    public void resetCurrentInputState() { currentInputState = new UserInput(); }
}

package chromage.server;

import chromage.shared.GameState;
import chromage.shared.Mage;
import chromage.shared.MageType;
import chromage.shared.UserInput;

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

    private Socket socket;

    private UserInput currentInputState;
    private boolean wantsTermination;
    private boolean shouldKeepProcessing;
    private boolean shouldListenInLobby;
    private int playerNumber;
    private long lastUpdateTick;
    private boolean isReady;
    private GameState state = new GameState();
    private Server server;
    private String playerName;
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

    public String getPlayerName() {
        return playerName;
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

    public void terminateConnection() {
        wantsTermination = true;
        shouldKeepProcessing = false;
        shouldListenInLobby = false;
    }

    public void sendUpdate(GameState state) {
        this.state = state;
        try {
            String serialization = state.serializeToString();
//            System.out.println("Sending to " + playerNumber +": " + state.x + ", " + state.y);
            toClient.writeBytes(serialization + '\n');
        } catch (SocketException ex) {
            server.disconnectPlayer(this);
            wantsTermination = true;
        } catch (IOException e) {
            server.disconnectPlayer(this);
            wantsTermination = true;
            e.printStackTrace();
        }
    }

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
            // TODO: actually come up with protocol for joining game etc.
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

    public void listenForUpdates() throws IOException {
        if (wantsTermination || state.shouldTerminate()) return;
        // Send initial connection info
        shouldKeepProcessing = true;
        while (shouldKeepProcessing) {
            String clientMessage = fromClient.readLine();
            if (clientMessage == null) break;
            try {
                UserInput userInput = UserInput.deserializeFromString(clientMessage);
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
                System.out.println("Exception: " + clientMessage);
                e.printStackTrace();
                server.disconnectPlayer(this);
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

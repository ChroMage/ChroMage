package chromage.server;

import chromage.shared.GameState;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Listens to a connection from a player, sending updates back to
 * the main server
 */
public class PlayerThread extends Thread {

    private Socket socket;

    private int currentInputState;
    private boolean wantsTermination;
    private boolean shouldKeepProcessing;
    private int playerNumber;
    private long lastUpdateTime;

    BufferedReader fromClient;
    DataOutputStream toClient;

    public PlayerThread(Socket socket, int playerNumber) {
        this.socket = socket;
        this.wantsTermination = false;
        this.currentInputState = 0;
        this.playerNumber = playerNumber;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public void run() {
        try {
            fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            toClient = new DataOutputStream(socket.getOutputStream());
            initiateHandshake();
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

    public static boolean isTerminationCommand(String message) {
        return "14".equals(message);
    }

    public void initiateHandshake() throws IOException {
        toClient.writeBytes("Hello!" + '\n');
    }

    public void terminateConnection() {
        shouldKeepProcessing = false;
    }

    public void sendUpdate(GameState state) {
        try {
            String serialization = state.serializeToString();
            System.out.println("Sending to " + playerNumber +": " + state.x + ", " + state.y);
            toClient.writeBytes(serialization + '\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listenForUpdates() throws IOException {
        // Send initial connection info
        shouldKeepProcessing = true;
        while (shouldKeepProcessing) {
            String clientMessage = fromClient.readLine();
            if (!"0".equals(clientMessage)) {
//                System.out.println("Client " + playerNumber + " sent: " + clientMessage);
            }
            if (clientMessage == null) {
                break;
            }
            if (isTerminationCommand(clientMessage)) {
                wantsTermination = true;
            } else {
                currentInputState = Integer.parseInt(clientMessage);
                lastUpdateTime = System.currentTimeMillis();
            }
        }
        System.out.println("Terminating connection.");
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public boolean wantsTermination() { return wantsTermination; }
    public int getCurrentInputState() { return currentInputState; }
    public void resetCurrentInputState() { currentInputState = 0; }
}

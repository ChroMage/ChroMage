package chromage.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.UUID;

import chromage.server.GameSession;
import chromage.server.PlayerThread;

/**
 * Created by ahruss on 3/13/15.
 */
public class Server extends Thread {
    private ServerSocket socket;
    private int port = 9877;

    private Dictionary<UUID, GameSession> games;
    private ArrayList<PlayerThread> lobbyPlayers;

    public Server() {
        games = new Hashtable<UUID, GameSession>();
        lobbyPlayers = new ArrayList<PlayerThread>();
    }

    public static void main(String args[]) throws IOException {
        // TODO: make port configurable
        Server server = new Server();
        server.start();
    }

    public void createAndJoinGame(PlayerThread host, String name, int expectedNumberOfPlayers) {
        UUID gameUuid = UUID.randomUUID();
        GameSession game = new GameSession(name);
        game.setExpectedNumberOfPlayers(expectedNumberOfPlayers);
        System.out.println(expectedNumberOfPlayers);
        games.put(gameUuid, game);
        joinGame(host, gameUuid);
        game.start();
    }

    public boolean joinGame(PlayerThread player, UUID uuid) {
        GameSession game = games.get(uuid);
        if (game == null || game.isFull()) {
            System.out.println(game.isFull());
            System.out.println(game);
            System.out.println("Failed to join game.");
            return false;
        }
        lobbyPlayers.remove(player);
        game.connectPlayer(player);
        System.out.println("started game");
        return true;
    }

    public void sendGameList(DataOutputStream stream) {
        // TODO: empty method body
    }

    public void run() {
        System.out.println("Waiting for connections");
        while (true) {
            try {
                socket = new ServerSocket(port);
                Socket connectionSocket = socket.accept();
                PlayerThread player = new PlayerThread(connectionSocket, this);
                lobbyPlayers.add(player);
                System.out.println("Connected new player");
                player.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

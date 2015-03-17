package chromage.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Created by ahruss on 3/13/15.
 */
public class Server extends Thread {
    private ServerSocket socket;
    private int port = 9877;

    private Hashtable<UUID, GameSession> games;
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
        name = name.replace(" ", "").replace("\n", "").replace(",", "");
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

    public void sendGameList(DataOutputStream stream) throws IOException {
        Dictionary<UUID, GameSession> cloned = (Hashtable<UUID, GameSession>)games.clone();
        Enumeration<UUID> keyEnumeration = cloned.keys();

        StringBuffer list = new StringBuffer();
        while (keyEnumeration.hasMoreElements()) {
            UUID id = keyEnumeration.nextElement();
            GameSession session = cloned.get(id);
            if(session.getGameState() != null && session.getGameState().x != -5){
            	list.append(id + " " + session.getGameName() + " " + session.connectedPlayers() + ",");
            }
        }
        stream.writeBytes(list.toString() + "\n");
    }

    public void disconnectPlayer(PlayerThread p) {
        if (lobbyPlayers.contains(p)) {
            System.out.println("Disconnecting player " + p);
            lobbyPlayers.remove(p);
            p.terminateConnection();
        }
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

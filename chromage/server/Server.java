package chromage.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Main class for the backend server.
 */
public class Server extends Thread {
    /**
     * the socket we're listening for connections on
     */
    private ServerSocket socket;

    /**
     * the port we listen on
     */
    private int port = 9877;

    /**
     * The set of games that are currently either in-lobby or in-progress.
     */
    private Hashtable<UUID, GameSession> games;

    /**
     * the set of players currently in the lobby
     */
    private ArrayList<PlayerThread> lobbyPlayers;

    public Server() {
        games = new Hashtable<UUID, GameSession>();
        lobbyPlayers = new ArrayList<PlayerThread>();
    }

    public static void main(String args[]) throws IOException {
        Server server = new Server();
        server.start();
    }

    /**
     * create a new game session and put the host in it. starts a new background thread
     * @param host  the game session creator
     * @param name  the name of the game to create
     * @param expectedNumberOfPlayers   the size of the game
     */
    public void createAndJoinGame(PlayerThread host, String name, int expectedNumberOfPlayers) {
        UUID gameUuid = UUID.randomUUID();
        name = name.replace(" ", "").replace("\n", "").replace(",", "");
        GameSession game = new GameSession(name, gameUuid, this);
        game.setExpectedNumberOfPlayers(expectedNumberOfPlayers);
        System.out.println(expectedNumberOfPlayers);
        games.put(gameUuid, game);
        joinGame(host, gameUuid);
        game.start();
        System.out.println("started game");
    }

    /**
     * move the player into a game
     * @param player    the player to move
     * @param uuid      the uuid of the game to move them into
     * @return  true if we successfully move the player into the game, false if the game is full or there's no game with
     *              that UUID in our list.
     */
    public boolean joinGame(PlayerThread player, UUID uuid) {
        GameSession game = games.get(uuid);
        if (game == null || game.isFull()) {
            System.out.println("Failed to join game.");
            return false;
        }
        lobbyPlayers.remove(player);
        game.connectPlayer(player);
        return true;
    }

    /**
     * Send the list of games to the player. Sends game all in one line, comma separated, each in the following format:
     *
     *    [uuid] [name] [number of players in game] [number of players expected]
     *
     * @param stream    the stream on which to send the list of games
     * @throws IOException
     */
    public void sendGameList(DataOutputStream stream) throws IOException {
        Dictionary<UUID, GameSession> cloned = (Hashtable<UUID, GameSession>)games.clone();
        Enumeration<UUID> keyEnumeration = cloned.keys();

        StringBuffer list = new StringBuffer();
        while (keyEnumeration.hasMoreElements()) {
            UUID id = keyEnumeration.nextElement();
            GameSession session = cloned.get(id);
            list.append(id + " " + session.getGameName() + " " + session.connectedPlayers() + " " + session.getExpectedNumberOfPlayers() + ",");
        }
        stream.writeBytes(list.toString() + "\n");
    }

    /**
     * Removes a player from the lobby if they're in the lobby, then tells their thread to exit
     * and waits for it to do so
     * @param p the player to remove
     */
    public void disconnectPlayer(PlayerThread p) {
        if (lobbyPlayers.contains(p)) {
            System.out.println("Disconnecting player " + p);
            lobbyPlayers.remove(p);
            p.terminateConnection();
            try {
                p.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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

    /**
     * Removes the game from the list of current games.
     * @param gameSession
     */
    public void gameEnded(GameSession gameSession) {
        System.out.println("Game ended: " + gameSession.getGameName());
        games.remove(gameSession.getUuid());
    }
}

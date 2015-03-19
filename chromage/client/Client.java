package chromage.client;

import chromage.client.util.Keyboard;
import chromage.client.views.*;
import chromage.shared.Mage;
import chromage.shared.utils.GameInfo;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Game client main class. Creates the GUI.
 */
public class Client implements ConnectMenu.Delegate, LobbyMenu.Delegate, GamePanel.Delegate {

	JFrame mainWindow;
	Socket socket;
	DataOutputStream toServer;
	BufferedReader fromServer;

	public static void main(String args[]) {
		try {
			new Client().run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() throws Exception {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                mainWindow = new JFrame();
                mainWindow.setTitle("ChroMage");
                mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                mainWindow.setContentPane(new ConnectMenu(Client.this));
                mainWindow.setVisible(true);
                mainWindow.setFocusable(true);
                mainWindow.requestFocusInWindow();
                Keyboard.listenTo(mainWindow);
                mainWindow.pack();
                mainWindow.setLocationRelativeTo(null);
            }
        });
	}

    /**
     * Initiate a connection to the server. If we are successful, send the server our
     * username, then move to the lobby panel
     * @param port          the port entered by the user
     * @param ipAddress     the ip address entered by the user
     * @param playerName    the player name entered by the user
     */
	@Override
	public void initiateConnection(int port, String ipAddress, String playerName) {
		try {
            System.out.println("Initiating connection to " + ipAddress + ", " + port);
			socket = new Socket(ipAddress, port);
			toServer = new DataOutputStream(socket.getOutputStream());
			fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			// read handshake
			System.out.println(fromServer.readLine());
            toServer.writeBytes("setName " + playerName.replace(" ", "_") + '\n');
            String welcome = fromServer.readLine();
            System.out.println(welcome);
            if (!"Welcome.".equals(welcome)) {
                System.out.println("Connection failed.");
                returnToConnect();
            } else {
                System.out.println("Connection succeeded. Moving to lobby.");
                mainWindow.setContentPane(new LobbyMenu(this));
                mainWindow.pack();
                mainWindow.setLocationRelativeTo(null);
            }
		} catch (IOException e) {
			e.printStackTrace();
			returnToConnect();
		}
	}

    /**
     * Tell the server we want to join a game. If the server lets us do so, go to the game view
     * @param id
     * @param mageType
     */
	@Override
	public void joinGame(UUID id, Mage.Type mageType) {
		try {
			toServer.writeBytes("join " + id + " " + mageType + "\n");
			String line = fromServer.readLine();
			if ("success".equals(line)) {
				System.out.println("Setting content pane to game");
				mainWindow.setContentPane(new GamePanel(this, toServer, fromServer));
                mainWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
                mainWindow.setVisible(true);
			} else {
				System.out.println("Didn't get success from server, instead got: " + line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    /**
     * Get the list of games from the server
     * @return  the list of games from the server if the request is successful; if there's an exception, returns an
     *              empty list
     */
	@Override
	public ArrayList<GameInfo> getGameList() {
		try {
			toServer.writeBytes("list\n");
			String listLine = fromServer.readLine();
			ArrayList<GameInfo> games = new ArrayList<GameInfo>();
			if (listLine.indexOf(",") == -1) return new ArrayList<GameInfo>();
			for (String g : listLine.split(",")) {
				games.add(GameInfo.parse(g));
			}
			return games;
		} catch (IOException e) {
			returnToConnect();
			e.printStackTrace();
		}
		return new ArrayList<GameInfo>();
	}

    /**
     * Try to create a game. If we fail, go back to the connect menu
     * @param numberOfPlayers
     * @param name
     * @param mageType
     */
	@Override
	public void createGame(int numberOfPlayers, String name, Mage.Type mageType) {
		System.out.println("Trying to create game");
		try {
            name = name.replace(" ", "_");
			toServer.writeBytes("new " + name + " " + numberOfPlayers + " " + mageType + "\n");
			String line = fromServer.readLine();
            System.out.println("Receiving: " + line);
			if ("success".equals(line)) {
				System.out.println("Setting content pane to game");
				mainWindow.setContentPane(new GamePanel(this, toServer, fromServer));
                mainWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
                mainWindow.setVisible(true);
			} else {
				System.out.println("Didn't get success from server, instead got: " + line);
			}
		} catch (IOException e) {
			e.printStackTrace();
			returnToConnect();
		}
	}

    /**
     * Take the player back to the connect menu
     */
	@Override
	public void returnToConnect() {
		if (!socket.isClosed()) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Returning to connect menu");
		mainWindow.setContentPane(new ConnectMenu(this));
		mainWindow.getContentPane().getParent().revalidate();
		mainWindow.getContentPane().getParent().repaint();
        mainWindow.pack();
        mainWindow.setLocationRelativeTo(null);
	}
}

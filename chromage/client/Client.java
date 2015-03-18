package chromage.client;

import chromage.shared.MageType;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

public class Client implements IConnectMenuDelegate, ILobbyMenuDelegate, IGamePanelDelegate {

	public static final int SCREEN_WIDTH = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
	public static final int SCREEN_HEIGHT = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;

	public JFrame mainWindow;
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
                mainWindow.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
                mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                mainWindow.setContentPane(new ConnectMenu(Client.this));
                mainWindow.setVisible(true);
                mainWindow.setFocusable(true);
                mainWindow.requestFocusInWindow();
                mainWindow.addKeyListener(Keyboard.getInstance());
                mainWindow.pack();
                mainWindow.setLocationRelativeTo(null);
            }
        });
	}

	@Override
	public void initiateConnection(int port, String ipAddress, String playerName) {
		try {
            System.out.println("Initiating connection to " + ipAddress + ", " + port);
			socket = new Socket(ipAddress, port);
			toServer = new DataOutputStream(socket.getOutputStream());
			fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			// read handshake
			System.out.println(fromServer.readLine());
            toServer.writeBytes("setName " + playerName + '\n');
            String welcome = fromServer.readLine();
            System.out.println(welcome);
            if (!"Welcome.".equals(welcome)) {
                System.out.println("Connection failed.");
                returnToLobby();
            } else {
                System.out.println("Connection succeeded. Moving to lobby.");
                mainWindow.setContentPane(new LobbyMenu(this));
                mainWindow.pack();
                mainWindow.setLocationRelativeTo(null);
            }
		} catch (IOException e) {
			e.printStackTrace();
			returnToLobby();
		}
	}

	@Override
	public void joinGame(UUID id, MageType mageType) {
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
			returnToLobby();
			e.printStackTrace();
		}
		return new ArrayList<GameInfo>();
	}

	@Override
	public void createGame(int numberOfPlayers, String name, MageType mageType) {
		System.out.println("Trying to create game");
		try {
			toServer.writeBytes("new " + name + " " + numberOfPlayers + " " + mageType + "\n");
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
			returnToLobby();
		}
	}

	@Override
	public void returnToLobby() {
		if (!socket.isClosed()) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Returning to lobby");
		mainWindow.setContentPane(new ConnectMenu(this));
		mainWindow.getContentPane().getParent().revalidate();
		mainWindow.getContentPane().getParent().repaint();
        mainWindow.pack();
        mainWindow.setLocationRelativeTo(null);
	}
}

package chromage.client;

import javax.swing.*;

import chromage.client.Keyboard;
import chromage.client.MainMenu;

import java.awt.*;

public class Client {

	public static final int SCREEN_WIDTH = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
	public static final int SCREEN_HEIGHT = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;

	public static void main(String args[]) throws Exception {
		final JFrame frame = new JFrame();
	 	frame.setTitle("ChroMage");
	 	frame.setSize(SCREEN_WIDTH/2, SCREEN_HEIGHT/2);
	 	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	 	Container contentPane = frame.getContentPane();
		MainMenu menu = new MainMenu();
	 	contentPane.add(menu);
	 	frame.setVisible(true);
		frame.setFocusable(true);
		frame.requestFocusInWindow();
		frame.addKeyListener(Keyboard.getInstance());
	}
}

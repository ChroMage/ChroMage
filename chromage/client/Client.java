package chromage.client;

import javax.swing.*;
import java.awt.*;

public class Client {

	public static final int SCREEN_WIDTH = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
	public static final int SCREEN_HEIGHT = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;

	public static KeyboardInput keyboardInput;
	public static void main(String args[]) throws Exception {
		keyboardInput = new KeyboardInput();
		final JFrame frame = new JFrame();
	 	frame.setTitle("Key");
	 	frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
	 	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	 	Container contentPane = frame.getContentPane();
		MainMenu menu = new MainMenu();
	 	contentPane.add(menu);
	 	frame.setVisible(true);
		frame.setFocusable(true);
		frame.requestFocusInWindow();
		frame.addKeyListener(keyboardInput);
	}

}

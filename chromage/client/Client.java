package chromage.client;

import chromage.shared.Actions;
import chromage.shared.Constants;
import chromage.shared.GameState;
import chromage.shared.RateLimitedLoop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client extends JPanel implements KeyListener{

	public static final int SCREEN_WIDTH = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
	public static final int SCREEN_HEIGHT = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
	public static final double WIDTH_FACTOR =  (SCREEN_WIDTH + 0.0) / Constants.MAX_WIDTH;
	public static final double HEIGHT_FACTOR =  (SCREEN_HEIGHT + 0.0) / Constants.MAX_HEIGHT;
			
	public static int val = 0;
	static GameState state;
	public void keyTyped(KeyEvent e) {
 		switch(e.getKeyChar()) {
 			case 'w': val = val | Actions.UP; break;
 			case 'a': val = val | Actions.LEFT; break;
 			case 's': val = val | Actions.DOWN; break;
 			case 'd': val = val | Actions.RIGHT; break;
 			case ' ': val = val | Actions.JUMP; break;
 		}
 	}
 	public void keyReleased(KeyEvent e) {
 		switch(e.getKeyChar()) {
 			//keep all values except the released one
 			case 'w': val &= ~Actions.UP; break;
 			case 'a': val &= ~Actions.LEFT; break;
 			case 's': val &= ~Actions.DOWN; break;
 			case 'd': val &= ~Actions.RIGHT; break;
 			case ' ': val &= ~Actions.JUMP;  break;
 		}
 	}
 	public void keyPressed(KeyEvent e) {

 	}
 	public Client() {
 		addKeyListener(this);
 	}
 	public boolean isFocusable() {
 		return true;
 	}
	public static void main(String args[]) throws Exception {

		// TODO: Make these configurable
		String ipAddress = "127.0.0.1";
		int port = 9877;
		String input;
		String output = null;
		Socket clientSocket = new Socket(ipAddress, port); //set up connection

		DataOutputStream serverIn = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader serverOut = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

		final SenderThread sender = new SenderThread(serverIn);
		final ModelThread model = new ModelThread(serverOut);
		sender.start();
		model.start();

		final JFrame frame = new JFrame();
	 	frame.setTitle("Key");
	 	frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
	 	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	 	Container contentPane = frame.getContentPane();
	 	contentPane.add(new Client());
	 	frame.setVisible(true);

		new RateLimitedLoop(Constants.TICKS_PER_SECOND) {
			public boolean shouldContinue() {
				return model.state.x != -5;
			}
			public void body() {
				System.out.println("rendering");
				drawCircle(frame, model.state.x, model.state.y);
				sender.keyState = val;
				sender.isRunning = (model.state.x != -5);
			}
		}.run();

		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}

	public static void drawCircle(JFrame frame, int dx, int dy) {
		Graphics g = frame.getGraphics();
		int x = (int)(dx*WIDTH_FACTOR);
		int y = (int)(dy*HEIGHT_FACTOR);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, (int) (Constants.MAX_WIDTH * WIDTH_FACTOR), (int) (Constants.MAX_HEIGHT * HEIGHT_FACTOR));
		g.setColor(Color.BLACK);
		g.fillOval(x, y, (int) (100 * WIDTH_FACTOR), (int) (100 * HEIGHT_FACTOR));
		g.dispose();
	}
 	public static void drawLineTo(JFrame frame, int dx, int dy) {
 	    Graphics g = frame.getGraphics();
 	    int x = (int)(dx*WIDTH_FACTOR);
 	    int y = (int)(dy*HEIGHT_FACTOR);
 	    g.drawLine(0, 0, x, y);
 	    g.dispose();
 	}
}

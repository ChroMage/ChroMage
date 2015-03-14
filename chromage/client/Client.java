package chromage.client;

import chromage.shared.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client extends JPanel implements KeyListener, MouseMotionListener, MouseListener {

	public static final int SCREEN_WIDTH = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
	public static final int SCREEN_HEIGHT = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
	private static double WIDTH_FACTOR(int frameWidth){
		return (frameWidth + 0.0) / Constants.MAX_WIDTH;
	}
	private static double HEIGHT_FACTOR(int frameHeight){
		return (frameHeight + 0.0) / Constants.MAX_HEIGHT;
	}
	
	public static int val = 0;

	public static UserInput userInput = new UserInput();

	public void keyTyped(KeyEvent e) {
 		switch(e.getKeyChar()) {
 			case 'w': userInput.verticalDirection = VerticalDirection.JUMP; break;
 			case 'a': userInput.horizontalDirection = HorizontalDirection.LEFT; break;
 			case 'd': userInput.horizontalDirection = HorizontalDirection.RIGHT; break;
 			case ' ': userInput.verticalDirection = VerticalDirection.JUMP; break;
 		}
 	}
 	public void keyReleased(KeyEvent e) {
 		switch(e.getKeyChar()) {
 			//keep all values except the released one
 			case 'w': userInput.verticalDirection = VerticalDirection.NONE; break;
 			case 'a': userInput.horizontalDirection = HorizontalDirection.NONE; break;
 			case 'd': userInput.horizontalDirection = HorizontalDirection.NONE; break;
 			case ' ': userInput.verticalDirection = VerticalDirection.NONE; break;
 		}
 	}
 	public void keyPressed(KeyEvent e) {
 	}
 	public void mouseMoved(MouseEvent e) {
 		userInput.point2D.setLocation(e.getX(), e.getY());
 	}
 	public void mouseDragged(MouseEvent e) {
 		userInput.point2D.setLocation(e.getX(), e.getY());
 	}
 	 public void mousePressed(MouseEvent e) {
 	 	switch(e.getButton()) {
 	 		case MouseEvent.BUTTON1: userInput.spell = Spell.LEFT; break;
 	 		case MouseEvent.BUTTON2: userInput.spell = Spell.RIGHT; break;
 	 		case MouseEvent.BUTTON3: userInput.spell = Spell.MIDDLE; break;
 	 	}
    }
    public void mouseReleased(MouseEvent e) {
    	switch(e.getButton()) {
 	 		case MouseEvent.BUTTON1: userInput.spell = Spell.NONE; break;
 	 		case MouseEvent.BUTTON2: userInput.spell = Spell.NONE; break;
 	 		case MouseEvent.BUTTON3: userInput.spell = Spell.NONE; break;
 	 	}
    }
    public void mouseClicked(MouseEvent e) {

    }
    public void mouseEntered(MouseEvent e) {
    	//set flag to positive
    }
    public void mouseExited(MouseEvent e) {
    	//set flag to negative
    }
 	public Client() {
 		addMouseListener(this);
 		addMouseMotionListener(this);
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
	 	frame.setTitle("ChroMage");
	 	frame.setSize(SCREEN_WIDTH/2, SCREEN_HEIGHT/2);
	 	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	 	Container contentPane = frame.getContentPane();
	 	contentPane.add(new Client());
	 	frame.setVisible(true);
	 	frame.setAlwaysOnTop(true);

		new RateLimitedLoop(Constants.TICKS_PER_SECOND) {
			public boolean shouldContinue() {
				return model.state.x != -5;
			}
			public void body() {
				render(model, frame);
				sender.isRunning = (model.state.x != -5);
				sender.userInput = Client.userInput;
			}
		}.run();

		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}

	private static void render(ModelThread model, JFrame frame) {
		Graphics g = frame.getGraphics();
		drawCircle(frame, model.state.x, model.state.y, g);
		for(Entity e : model.state.entities){
			e.draw(g, HEIGHT_FACTOR(frame.getHeight()), WIDTH_FACTOR(frame.getWidth()));
		}
		g.dispose();
	}
	
	public static void drawCircle(JFrame frame, int dx, int dy, Graphics g) {
		int x = (int)(dx*WIDTH_FACTOR(frame.getWidth()));
		int y = (int)(dy*HEIGHT_FACTOR(frame.getHeight()));
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, (int) (Constants.MAX_WIDTH * WIDTH_FACTOR(frame.getWidth())), (int) (Constants.MAX_HEIGHT * HEIGHT_FACTOR(frame.getHeight())));
		//g.setColor(Color.BLACK);
		//g.fillOval(x, y, (int) (100 * WIDTH_FACTOR(frame.getWidth())), (int) (100 * HEIGHT_FACTOR(frame.getHeight())));
	}
	
 	public static void drawLineTo(JFrame frame, int dx, int dy) {
 	    Graphics g = frame.getGraphics();
 	    int x = (int)(dx*WIDTH_FACTOR(frame.getWidth()));
 	    int y = (int)(dy*HEIGHT_FACTOR(frame.getHeight()));
 	    g.drawLine(0, 0, x, y);
 	    g.dispose();
 	}
}

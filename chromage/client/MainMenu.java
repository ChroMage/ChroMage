package chromage.client;

import chromage.shared.*;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class MainMenu extends JPanel implements AncestorListener, KeyListener, MouseListener, MouseMotionListener {
	private static double WIDTH_FACTOR(int frameWidth){
		return (frameWidth + 0.0) / Constants.MAX_WIDTH;
	}
	private static double HEIGHT_FACTOR(int frameHeight){
		return (frameHeight + 0.0) / Constants.MAX_HEIGHT;
	}
	public static int val = 0;

	public UserInput userInput = new UserInput();

	public void keyTyped(KeyEvent e) {
		System.out.println("called");
 		switch(e.getKeyChar()) {
 			case KeyEvent.VK_BACK_SPACE: //showMenu(); 
 			break;
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
 	public MainMenu() {
 		addMouseListener(this);
 		addMouseMotionListener(this);
 		addKeyListener(this);
 		addAncestorListener(this);
 	}
 	public boolean isFocusable() {
 		return true;
 	}
 	private SenderThread sender; 
	private ModelThread model; 
	public void ancestorMoved(AncestorEvent e) {}
	public void ancestorRemoved(AncestorEvent e) {}
	public void ancestorAdded(AncestorEvent e) {
		System.out.println("hello");
		try {
			String ipAddress = "127.0.0.1";
			int port = 9877;
			String input;
			String output = null;
			Socket clientSocket = new Socket(ipAddress, port); //set up connection

			DataOutputStream serverIn = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader serverOut = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			sender = new SenderThread(serverIn);
			model = new ModelThread(serverOut);
			sender.start();
			model.start();
			new RateLimitedLoop(Constants.TICKS_PER_SECOND) {
				public boolean shouldContinue() {
					return model.state.x != -5;
				}
				public void body() {
					System.out.println("rendering");
					MainMenu.this.render(MainMenu.this.getGraphics());
					sender.isRunning = (model.state.x != -5);
				}
			}.run();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public void render(Graphics g) {
		g.setColor(Color.WHITE);
		// g.fillRect(0, 0, (int) (Constants.MAX_WIDTH * WIDTH_FACTOR(this.getWidth())), 
		// 	             (int) (Constants.MAX_HEIGHT * HEIGHT_FACTOR(this.getHeight())));
		drawCircle(this, model.state.x, model.state.y, g);
		for(Entity e : model.state.entities){
			e.draw(g, HEIGHT_FACTOR(this.getHeight()), WIDTH_FACTOR(this.getWidth()));
		}
		g.dispose();
	}
	
	public static void drawCircle(JPanel panel, int dx, int dy, Graphics g) {
		int x = (int)(dx*WIDTH_FACTOR(panel.getWidth()));
		int y = (int)(dy*HEIGHT_FACTOR(panel.getHeight()));
		g.setColor(Color.BLACK);
		g.fillOval(x, y, (int) (100 * WIDTH_FACTOR(panel.getWidth())), (int) (100 * HEIGHT_FACTOR(panel.getHeight())));
	}
	
 	public static void drawLineTo(JFrame frame, int dx, int dy) {
 	    Graphics g = frame.getGraphics();
 	    int x = (int)(dx*WIDTH_FACTOR(frame.getWidth()));
 	    int y = (int)(dy*HEIGHT_FACTOR(frame.getHeight()));
 	    g.drawLine(0, 0, x, y);
 	    g.dispose();
 	}
}
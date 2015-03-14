package chromage.client;

import chromage.shared.*;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;

public class GamePanel extends JPanel implements AncestorListener, MouseMotionListener, MouseListener {
	private static double WIDTH_FACTOR(int frameWidth){
		return (frameWidth + 0.0) / Constants.MAX_WIDTH;
	}
	private static double HEIGHT_FACTOR(int frameHeight){
		return (frameHeight + 0.0) / Constants.MAX_HEIGHT;
	}

	public UserInput userInput = new UserInput();
	public DataOutputStream toServer;
	public BufferedReader fromServer;

	IGamePanelDelegate delegate;

 	public void mouseMoved(MouseEvent e) {
 		userInput.mouseLocation.setLocation(e.getX()/WIDTH_FACTOR(getWidth()), e.getY() / HEIGHT_FACTOR(getHeight()));
 	}
 	public void mouseDragged(MouseEvent e) {
 		userInput.mouseLocation.setLocation(e.getX()/WIDTH_FACTOR(getWidth()), e.getY()/ HEIGHT_FACTOR(getHeight()));
 	}
 	public void mousePressed(MouseEvent e) {
 	 	switch(e.getButton()) {
 	 		case MouseEvent.BUTTON1: userInput.spell = SpellInput.LEFT; break;
 	 		case MouseEvent.BUTTON2: userInput.spell = SpellInput.MIDDLE; break;
 	 		case MouseEvent.BUTTON3: userInput.spell = SpellInput.RIGHT; break;
 	 	}
    }
    public void mouseReleased(MouseEvent e) {
    	switch(e.getButton()) {
	 		case MouseEvent.BUTTON1: userInput.spell = SpellInput.NONE; break;
	 		case MouseEvent.BUTTON2: userInput.spell = SpellInput.NONE; break;
	 		case MouseEvent.BUTTON3: userInput.spell = SpellInput.NONE; break;
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
 	public GamePanel(IGamePanelDelegate delegate, DataOutputStream toServer, BufferedReader fromServer) {
		this.delegate = delegate;
		System.out.println("creating game panel");
		this.toServer = toServer;
		this.fromServer = fromServer;
		addMouseListener(this);
		addMouseMotionListener(this);
 		addAncestorListener(this);
 	}
 	private SenderThread sender;
	private ModelThread model; 
	public void ancestorMoved(AncestorEvent e) {}
	public void ancestorRemoved(AncestorEvent e) {}
	public void ancestorAdded(AncestorEvent e) {
		System.out.println("Starting game panel");

		System.out.println("Opened game panel");
		try {
			String input;
			String output = null;


			sender = new SenderThread(toServer);
			model = new ModelThread(fromServer);
			sender.start();
			model.start();
			new RateLimitedLoop(Constants.TICKS_PER_SECOND) {
				@Override
				public void body() {
					Keyboard.getInstance().poll();
					System.out.println("A is down: " + Keyboard.isKeyDown(KeyEvent.VK_A));
					if (Keyboard.isKeyDown(KeyEvent.VK_A) && Keyboard.isKeyDown(KeyEvent.VK_D)) {
						userInput.horizontalDirection = HorizontalDirection.NONE;
					} else if (Keyboard.isKeyDown(KeyEvent.VK_A)) {
						userInput.horizontalDirection = HorizontalDirection.LEFT;
					} else if (Keyboard.isKeyDown(KeyEvent.VK_D)) {
						userInput.horizontalDirection = HorizontalDirection.RIGHT;
					} else {
						userInput.horizontalDirection = HorizontalDirection.NONE;
					}
					if (Keyboard.isKeyDown(KeyEvent.VK_W) || Keyboard.isKeyDown(KeyEvent.VK_SPACE)) {
						userInput.verticalDirection = VerticalDirection.JUMP;
					}
					else {
						userInput.verticalDirection = VerticalDirection.NONE;
					}

					if (Keyboard.isKeyDown(KeyEvent.VK_SHIFT)) {
						userInput.spell = SpellInput.MIDDLE;
					}
					sender.userInput = userInput;
					repaint();
					if (model.state.livingPlayers == 1) {
						delegate.returnToLobby();
						setBreak();
					}
				}
			}.runInBackground();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		getParent().revalidate();
		getParent().repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		System.out.println("Called");
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, (int) (Constants.MAX_WIDTH * WIDTH_FACTOR(this.getWidth())),
			             (int) (Constants.MAX_HEIGHT * HEIGHT_FACTOR(this.getHeight())));
		//drawCircle(this, model.state.x, model.state.y, g);
		for(Entity e : model.state.entities){
			e.draw(g, HEIGHT_FACTOR(this.getHeight()), WIDTH_FACTOR(this.getWidth()));
		}
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

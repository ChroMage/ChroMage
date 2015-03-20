package chromage.client.views;

import chromage.client.util.Keyboard;
import chromage.shared.Mage;
import chromage.shared.engine.Entity;
import chromage.shared.engine.GameState;
import chromage.shared.utils.Constants;
import chromage.shared.utils.RateLimitedLoop;
import chromage.shared.utils.Serializer;
import chromage.shared.utils.UserInput;

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

/**
 * A panel to display the game. Upon creation starts two background threads
 * to send and receive updates to and from the server
 */
public class GamePanel extends JPanel implements AncestorListener, MouseMotionListener, MouseListener {

    /**
     * The user input state to be sent to the server each tick
     */
	public UserInput userInput = new UserInput();
    /**
     * The output stream for data bound for the server to be written to
     */
	public DataOutputStream toServer;
    /**
     * The input stream from which to read server data
     */
	public BufferedReader fromServer;
	Delegate delegate;
 	private SenderThread sender;
	private ReceiverThread receiver;

    /**
     * Add listeners and sets properties; don't actually create elements until ancestorAdded is called.
     * @param delegate
     * @param toServer
     * @param fromServer
     */
 	public GamePanel(Delegate delegate, DataOutputStream toServer, BufferedReader fromServer) {
		this.delegate = delegate;
		System.out.println("creating game panel");
		this.toServer = toServer;
		this.fromServer = fromServer;
		addMouseListener(this);
		addMouseMotionListener(this);
 		addAncestorListener(this);
        setVisible(true);
 	}

    /**
     * gets the x scale factor at which the game is being drawn
     * @return
     */
	private double getWidthFactor(){
		return (double)getWidth() / Constants.BATTLEFIELD_WIDTH;
	}

    /**
     * gets the y scale factor at which the game is being drawn.
     * @return
     */
	private double getHeightFactor() {
		return (double)getHeight() / Constants.BATTLEFIELD_HEIGHT;
	}

 	public void mouseMoved(MouseEvent e) {
 		userInput.mouseLocation.setLocation(e.getX()/ getWidthFactor(), e.getY() / getHeightFactor());
 	}

 	public void mouseDragged(MouseEvent e) {
 		userInput.mouseLocation.setLocation(e.getX()/ getWidthFactor(), e.getY()/ getHeightFactor());
 	}

 	public void mousePressed(MouseEvent e) {
 	 	switch(e.getButton()) {
 	 		case MouseEvent.BUTTON1: userInput.spell = UserInput.SpellInput.LEFT; break;
 	 		case MouseEvent.BUTTON2: userInput.spell = UserInput.SpellInput.MIDDLE; break;
 	 		case MouseEvent.BUTTON3: userInput.spell = UserInput.SpellInput.RIGHT; break;
 	 	}
    }

    public void mouseReleased(MouseEvent e) {
    	switch(e.getButton()) {
	 		case MouseEvent.BUTTON1: userInput.spell = UserInput.SpellInput.NONE; break;
	 		case MouseEvent.BUTTON2: userInput.spell = UserInput.SpellInput.NONE; break;
	 		case MouseEvent.BUTTON3: userInput.spell = UserInput.SpellInput.NONE; break;
	 	}
    }

    public void mouseClicked(MouseEvent e) {  }

    public void mouseEntered(MouseEvent e) { }

    public void mouseExited(MouseEvent e) { }

	public void ancestorMoved(AncestorEvent e) {}
	public void ancestorRemoved(AncestorEvent e) {}
	public void ancestorAdded(AncestorEvent e) {
		System.out.println("Opened game panel");
		try {
			String input;
			String output = null;

			sender = new SenderThread(toServer, this);
			receiver = new ReceiverThread(fromServer, this);
			sender.start();
			receiver.start();
            new RateLimitedLoop(Constants.TICKS_PER_SECOND) {
				@Override
				public void body() {
					Keyboard.poll();
					if (Keyboard.isKeyDown(KeyEvent.VK_A) && Keyboard.isKeyDown(KeyEvent.VK_D)) {
						userInput.horizontalDirection = UserInput.HorizontalDirection.NONE;
					} else if (Keyboard.isKeyDown(KeyEvent.VK_A)) {
						userInput.horizontalDirection = UserInput.HorizontalDirection.LEFT;
					} else if (Keyboard.isKeyDown(KeyEvent.VK_D)) {
						userInput.horizontalDirection = UserInput.HorizontalDirection.RIGHT;
					} else {
						userInput.horizontalDirection = UserInput.HorizontalDirection.NONE;
					}
					if (Keyboard.isKeyDown(KeyEvent.VK_W) || Keyboard.isKeyDown(KeyEvent.VK_SPACE)) {
						userInput.verticalDirection = UserInput.VerticalDirection.JUMP;
					}
					else {
						userInput.verticalDirection = UserInput.VerticalDirection.NONE;
					}

					if (Keyboard.isKeyDown(KeyEvent.VK_SHIFT)) {
						userInput.spell = UserInput.SpellInput.MIDDLE;
					} else if (Keyboard.isKeyDown(KeyEvent.VK_Z)) {
						userInput.spell = UserInput.SpellInput.LEFT;
					} else if (Keyboard.isKeyDown(KeyEvent.VK_X)) {
						userInput.spell = UserInput.SpellInput.RIGHT;
					}
					if (Keyboard.isKeyDown(KeyEvent.VK_ESCAPE)) {
						userInput.wantsTermination = true;
					}
					sender.userInput = userInput;
					sender.isRunning = !receiver.state.shouldTerminate();
                    repaint();
					if (!receiver.isRunning || !sender.isRunning) {
                        System.out.println("Waiting for background processes to terminate...");
                        receiver.isRunning = sender.isRunning = false;
                        try {
                            sender.join();
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        try {
                            receiver.join();
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        delegate.returnToConnect();
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

    /**
     * called when the game ends normally: that is, when all but one player is dead
     */
    public void gameEnded() {
        sender.isRunning = false;
        for (Entity e : receiver.state.getEntities()) {
            if (e instanceof Mage) {
                Mage m = (Mage)e;
                if (!m.isDead()) {
                    System.out.println(m.getName() + " is living at the end of the round.");
                }
            }
        }
    }

    /**
     * called when the game ends abnormally, when a player presses escape
     */
    public void gameDismissed() {
        sender.isRunning = false;
        receiver.isRunning = false;
    }

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, (int) (Constants.BATTLEFIELD_WIDTH * getWidthFactor()),
                (int) (Constants.BATTLEFIELD_HEIGHT * getHeightFactor()));
		for(Entity e : receiver.state.getEntities()){
			e.draw(g, getHeightFactor(), getWidthFactor());
		}
		if (receiver.state.getAwaitedPlayers() != 0) {
			g.setColor(Color.BLACK);
			g.drawString("Awaiting players, need " + receiver.state.getAwaitedPlayers() + " more.",
					(int)(this.getWidth()/2* getWidthFactor()), (int)(this.getHeight()/2* getHeightFactor()));
		}
	}

    public static interface Delegate {
        public void returnToConnect();
    }

    /**
     * A thread which listens toe the server for updates and
     * stores those updates when it gets them.
     */
    public static class ReceiverThread extends Thread {
        public GameState state;
        public boolean isRunning;
        BufferedReader input;
        GamePanel panel;

        public ReceiverThread(BufferedReader input, GamePanel panel) {
            state = new GameState();
            this.input = input;
            this.panel = panel;
        }

        public void run() {
            System.out.println("Starting reciever");
            isRunning = true;
            try {
                while (isRunning) {
                    try {
                        String output = input.readLine();
                        if (output != null) {
                            state = Serializer.deserializeFromString(output);
                        }
                        if (output == null || state.shouldTerminate()) {
                            panel.gameEnded();
                            System.out.println("receive: exit.");
                            isRunning = false;
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * A thread which sends an update to the server every tick with the current user input.
     */
    public static class SenderThread extends Thread {

        public UserInput userInput = new UserInput();
        public boolean isRunning;
        DataOutputStream output;
        GamePanel panel;

        public SenderThread(DataOutputStream output,  GamePanel panel) {
            isRunning = true;
            this.output = output;
            this.panel = panel;
        }

        public void run() {
           new RateLimitedLoop(Constants.TICKS_PER_SECOND) {
                public boolean shouldContinue() {
                    return isRunning;
                }
                public void body() {
                    try {
                        UserInput u = userInput;
                        output.writeBytes(Serializer.serializeToString(u) + "\n");
                        if (userInput.wantsTermination()) {
                            panel.gameDismissed();
                            setBreak();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        setBreak();
                    }
                }
            }.run();
        }
    }
}

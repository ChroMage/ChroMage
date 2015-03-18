package chromage.client.views;

import chromage.client.util.Keyboard;
import chromage.shared.Mage;
import chromage.shared.engine.Entity;
import chromage.shared.engine.GameState;
import chromage.shared.engine.HorizontalDirection;
import chromage.shared.engine.VerticalDirection;
import chromage.shared.spells.SpellInput;
import chromage.shared.utils.Constants;
import chromage.shared.utils.RateLimitedLoop;
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
    public void mouseClicked(MouseEvent e) {  }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }

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
	private ReceiverThread receiver;
	public void ancestorMoved(AncestorEvent e) {}
	public void ancestorRemoved(AncestorEvent e) {}
	public void ancestorAdded(AncestorEvent e) {
		System.out.println("Starting game panel");

		System.out.println("Opened game panel");
		try {
			String input;
			String output = null;


			sender = new SenderThread(toServer);
			receiver = new ReceiverThread(fromServer, this);
			sender.start();
			receiver.start();
			new RateLimitedLoop(Constants.TICKS_PER_SECOND) {
				@Override
				public void body() {
					Keyboard.getInstance().poll();
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
					if (Keyboard.isKeyDown(KeyEvent.VK_ESCAPE)) {
						userInput.wantsTermination = true;
					}
					sender.userInput = userInput;
					sender.isRunning = !receiver.state.shouldTerminate();
					repaint();
					if (!receiver.isAlive() || !sender.isAlive()) {
						delegate.returnToLobby();
						setBreak();
					}
				}
			}.runInBackground();
		} catch(Exception ex) {
			ex.printStackTrace();
			delegate.returnToLobby();
		}
		getParent().revalidate();
		getParent().repaint();
	}

    public void gameEnded() {
        sender.isRunning = false;
        for (Entity e : receiver.state.entities) {
            if (e instanceof Mage) {
                Mage m = (Mage)e;
                if (!m.isDead()) {
                    System.out.println(m.getName() + " is living at the end of the round.");
                }
            }
        }
    }

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, (int) (Constants.MAX_WIDTH * WIDTH_FACTOR(this.getWidth())),
			             (int) (Constants.MAX_HEIGHT * HEIGHT_FACTOR(this.getHeight())));
		for(Entity e : receiver.state.entities){
			e.draw(g, HEIGHT_FACTOR(this.getHeight()), WIDTH_FACTOR(this.getWidth()));
		}
		if (receiver.state.awaitedPlayers != 0) {
			g.setColor(Color.BLACK);
			g.drawString("Awaiting players, need " + receiver.state.awaitedPlayers + " more.",
					(int)(this.getWidth()/2*WIDTH_FACTOR(this.getWidth())), (int)(this.getHeight()/2*HEIGHT_FACTOR(this.getHeight())));
		}
	}

    public static class ReceiverThread extends Thread {
        public GameState state;
        BufferedReader input;
        GamePanel panel;

        public ReceiverThread(BufferedReader input, GamePanel panel) {
            state = new GameState();
            this.input = input;
            this.panel = panel;
        }

        public void run() {
            try {
                while (true) {
                    try {
                        String output = input.readLine();
                        state = GameState.deserializeFromString(output);
                        if (state.shouldTerminate()) {
                            panel.gameEnded();
                            System.out.println("receive: exit.");
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

    public static class SenderThread extends Thread {

        DataOutputStream output;
        public UserInput userInput = new UserInput();
        public boolean isRunning;

        public SenderThread(DataOutputStream output) {
            isRunning = true;
            this.output = output;
        }

        public void run() {
           new RateLimitedLoop(Constants.TICKS_PER_SECOND) {
                public boolean shouldContinue() {
                    return isRunning;
                }
                public void body() {
                    try {
                        UserInput u = userInput;
                        output.writeBytes(u.serializeToString() + "\n");
                        if (userInput.wantsTermination()) setBreak();
                    } catch (Exception e) {
                        e.printStackTrace();
                        setBreak();
                    }
                }
            }.run();
        }
    }
}

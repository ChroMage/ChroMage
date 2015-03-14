package chromage.server;

import chromage.server.PlayerThread;
import chromage.shared.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class GameSession extends Thread {

	private GameState state;
	private String name;
	private ArrayList<PlayerThread> players;

	public void setExpectedNumberOfPlayers(int expectedNumberOfPlayers) {
		this.expectedNumberOfPlayers = expectedNumberOfPlayers;
	}

	private int expectedNumberOfPlayers;
	private long currentTick;
	private boolean isGameRunning;

	public GameState getGameState() { return state; }

	public GameSession(String name) {
		this.name = name;
		players = new ArrayList<PlayerThread>();
		isGameRunning = true;
		state = new GameState();
	}

	public String getGameName() {
		return name;
	}

	public boolean isGameRunning() {
		return isGameRunning;
	}

	public void endGame() {
		state.isGameOver = true;
	}

	public boolean isFull() {
		return players.size() == expectedNumberOfPlayers;
	}

	public void connectPlayer(PlayerThread player) {
		players.add(player);
	}

	public boolean allPlayersReady() {
		for (PlayerThread player : players) {
			if (!player.isReady())
				return false;
		}
		return true;
	}

	public void waitForPlayers() {
		while (players.size() < expectedNumberOfPlayers && !allPlayersReady()) {
			System.out.println("waiting");
			// wait until all the players have joined the game.
            sendUpdates();
		}
	}

	public void sendUpdates() {
		for (PlayerThread p : (ArrayList<PlayerThread>)players.clone()) {
			p.sendUpdate(state);
		}
	}

	public void processInput() {
		System.out.println("Processing input");
		int inputTimeoutTicks = 6;
		for (PlayerThread p : players) {
			if (p.wantsTermination()) {
				// terminate if any of the players wants to.
				System.out.println("Player " + p + " wants to leave.");
				return;
			}

				System.out.println("Player " + p + " current input: " + p.getCurrentInputState());
				System.out.println("Ticks since last client update: " + (state.getCurrentTick() - p.getLastUpdateTick()));
				p.mage.setVelocityWithInput(p.getCurrentInputState());
				if (currentTick - p.getLastUpdateTick() > inputTimeoutTicks) {
					p.resetCurrentInputState();
				}
			
			//process spell casts
			Spell spell = p.getCurrentInputState().spell;
			processSpellForPlayer(spell, p);

			if (currentTick - p.getLastUpdateTick() > inputTimeoutTicks) {
				p.resetCurrentInputState();
			}
		}
	}

	private void processSpellForPlayer(Spell spell, PlayerThread p) {
		Point2D mouseLocation = p.getCurrentInputState().mouseLocation;
		if(p.mage.getCoolDown() <= 0){
			double mouseXMinusMageX = p.getCurrentInputState().mouseLocation.x-p.mage.getPosition().x; 
			double mouseYMinusMageY = p.getCurrentInputState().mouseLocation.y-p.mage.getPosition().y;
			boolean facingRight = mouseXMinusMageX > 0;
			int launchX = 0;
			int launchY = p.mage.getPosition().y - 72;
			if(facingRight){
				launchX = p.mage.getPosition().x + p.mage.getWidth() + 2;
			}
			else{
				launchX = p.mage.getPosition().x - 72;
			}
			if (spell.equals(Spell.LEFT)){
				castBlink(p, p.getCurrentInputState().mouseLocation.x, p.getCurrentInputState().mouseLocation.y);
			}
			else if (spell.equals(Spell.RIGHT)){
				castIceball(p, launchX, launchY, mouseXMinusMageX, mouseYMinusMageY);
			}
			else if (spell.equals(Spell.MIDDLE)){
				castLightning(p, launchX, launchY, mouseXMinusMageX, mouseYMinusMageY);
			}
		}
		else{
			p.mage.decrementCooldown();
		}
	}

	private void castFireball(PlayerThread p, int launchX, int launchY,
			double mouseXMinusMageX, double mouseYMinusMageY) {
		state.addProjectile(launchX, launchY, mouseXMinusMageX, mouseYMinusMageY, Color.ORANGE);
		p.mage.setCoolDown(5);
	}
	private void castIceball(PlayerThread p, int launchX, int launchY,
			double mouseXMinusMageX, double mouseYMinusMageY) {
		state.addProjectile(launchX, launchY, mouseXMinusMageX, mouseYMinusMageY, Color.BLUE);
		p.mage.setCoolDown(30);
	}
	private void castLightning(PlayerThread p, int launchX, int launchY,
			double mouseXMinusMageX, double mouseYMinusMageY) {
		state.addProjectile(launchX, launchY, mouseXMinusMageX, mouseYMinusMageY, Color.YELLOW);
		p.mage.setCoolDown(90);
	}
	private void castLifesteal(PlayerThread p, int launchX, int launchY,
			double mouseXMinusMageX, double mouseYMinusMageY) {
		state.addProjectile(launchX, launchY, mouseXMinusMageX, mouseYMinusMageY, Color.GREEN);
		p.mage.setCoolDown(0);
	}
	private void castBlink(PlayerThread p, double mouseX, double mouseY) {
		Rectangle2D.Double newHitBox = new Rectangle2D.Double(mouseX, mouseY, p.mage.getWidth(), p.mage.getHeight());
		boolean canBlink = true;
		for(Entity e: state.entities){
			if(((e.getType() & Constants.BLOCK_TYPE) != 0) && newHitBox.intersects(e.getHitbox())){
				canBlink = false;
			}
		}
		if(canBlink){
			p.mage.setPosition(new Point((int)mouseX, (int)mouseY));
			p.mage.setCoolDown(60);
		}
	}

	public void executeGameLoop() {
		new RateLimitedLoop(Constants.TICKS_PER_SECOND) {
			public void body() {
				processInput();
				state.update();
				sendUpdates();
			}
		}.run();
	}

	public void terminateConnections() {
		state.x = -5;
		for (PlayerThread p : players) {
			p.sendUpdate(state);
			p.terminateConnection();
		}
	}

	public void prepareGame() {
		ArrayList<Mage> mages = new ArrayList<Mage>();
		for (PlayerThread p : players) {
			p.mage = new Mage(Color.RED);
			mages.add(p.mage);
		}
		state.initialize(mages);
	}

	public void run() {
		System.out.println("Waiting for players to connect...");
		waitForPlayers();
		System.out.println("Starting game loop...");

		prepareGame();

		executeGameLoop();
		System.out.println("Ending game...");
		terminateConnections();
	}

	public int connectedPlayers() {
		return players.size();
	}
}

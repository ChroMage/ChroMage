package chromage.shared;

import javax.xml.bind.DatatypeConverter;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class GameState implements Serializable {
    static final long serialVersionUID = -50077493051991117L;

    private boolean isGameOver;
    private long currentTick;
    public int livingPlayers;
    public int awaitedPlayers;
	public ArrayList<Entity> entities;
    private boolean shouldTerminate;

    public boolean isGameOver() {
        return isGameOver;
    }

    /** Default constructor. */
    public GameState() {
    	entities = new ArrayList<Entity>();
        isGameOver = false;
        shouldTerminate = false;
    }

    public void initialize(ArrayList<Mage> players) {
        generateMap();
        spreadPlayers(players);
        for (Mage m : players) {
            entities.add(m);
        }
        if(players.size() < 2){
        	//Add a dummy mage to fight
            entities.add(new Mage(2000, 2000, 100, 200, Color.BLUE));
        }
    }

    /**
     * Called exactly once per tick.
     */
    public void update(){
        int count = 0;
    	for(Entity e : entities) {
            if (e instanceof Mage) {
                count++;
            }
    		e.update(entities);
    	}
        livingPlayers = count;
        isGameOver = livingPlayers == 1;

        ArrayList<Entity> toRemove = new ArrayList<Entity>();
        for (Entity e : entities) {
            if (e.shouldBeRemoved()) toRemove.add(e);
        }
        entities.removeAll(toRemove);
        currentTick++;
    }

    public long getCurrentTick() {
        return currentTick;
    }

    public void spreadPlayers(ArrayList<Mage> players){
    	for (Mage m : players) {
    		Random r = new Random();
            m.setPosition(new Point2D.Double(500 + (int)r.nextInt(3000), 1000));
        }
    }
    
    private void generateMap() {
    	// Add border blocks
        entities.add(new Block(-10, 3700, 4010, 310));
        entities.add(new Block(-10, -10, 4010, 310));
        entities.add(new Block(-10, -10, 310, 4010));
        entities.add(new Block(3700, -10, 310, 4010));
        
	}

	public String serializeToString() throws IOException{
	     ByteArrayOutputStream bo = new ByteArrayOutputStream();
	     ObjectOutputStream so = new ObjectOutputStream(bo);
	     so.writeObject(this);
	     so.flush();
	     return DatatypeConverter.printBase64Binary(bo.toByteArray());
    }
    
    public static GameState deserializeFromString(String s) throws ClassNotFoundException, IOException {
	     byte b[] = DatatypeConverter.parseBase64Binary(s);
	     ByteArrayInputStream bi = new ByteArrayInputStream(b);
	     ObjectInputStream si = new ObjectInputStream(bi);
	     return (GameState)si.readObject();
    }

    public boolean shouldTerminate() {
        return shouldTerminate;
    }
    
    public void setTerminate() {
        this.shouldTerminate = true;
    }

    public void setGameOver(boolean gameOver) {
        this.isGameOver = gameOver;
    }
}
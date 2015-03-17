package chromage.shared;

import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class GameState implements Serializable {
    static final long serialVersionUID = -50077493051991117L;

    public boolean isGameOver;
	public int x, y;
    private long currentTick;
    public int livingPlayers;
    public int awaitedPlayers;
	public ArrayList<Entity> entities;

    /** Default constructor. */
    public GameState() {
    	entities = new ArrayList<Entity>();
        this.x = Constants.MAX_WIDTH / 2;
        this.y = Constants.MAX_HEIGHT / 2;
        this.isGameOver = false;
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
            m.setPosition(new Point(500 + (int)Math.random()*3000, 1000));
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
}
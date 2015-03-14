package chromage.shared;

import javax.xml.bind.DatatypeConverter;

import java.io.*;
import java.util.ArrayList;

public class GameState implements Serializable {
    static final long serialVersionUID = -50077493051991117L;

    public boolean isGameOver;
	public int x, y;
	public ArrayList<Entity> blocks;

    /** Default constructor. */
    public GameState() {
    	blocks = new ArrayList<Entity>();
        this.x = Constants.MAX_WIDTH / 2;
        this.y = Constants.MAX_HEIGHT / 2;
        this.isGameOver = false;
        blocks.add(new Block(2000, 2000, 200, 200));
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
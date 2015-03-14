package chromage.shared;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;

public class Mage extends Entity implements Serializable {
    static final long serialVersionUID = -50077493051991117L;

	public static final int DEFAULT_HEIGHT = 300;
	public static final int DEFAULT_WIDTH = 100;
	
	private int coolDown = 0;
	public int hp = 500;
	public int mana = 300;

	public Mage(Color color){
		this(2000,2000, DEFAULT_WIDTH, DEFAULT_HEIGHT, color);
	}

	public Mage(int x, int y, int width, int height, Color color){
		this.setPosition(new Point(x, y));
		this.setVelocity(new Point2D.Double(0, 0));
		this.width = width;
		this.height = height;
		this.color = color;
		type = Constants.MAGE_TYPE;
	}
	
	public boolean isAffectedByGravity(){
		return true;
	}

	public void decrementCooldown() {
		if(coolDown > 0){
			coolDown--;
		}
	}
	
	public int getCoolDown(){
		return coolDown;
	}

	public void setCoolDown(int i) {
		coolDown = i;
	}
}

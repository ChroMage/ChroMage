package chromage.shared;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.Serializable;

public class Projectile extends Entity implements Serializable {
    static final long serialVersionUID = -50077493051991117L;
	boolean isGravitated = false;
	
	public Projectile(int x, int y, double vx, double vy, Color color){
		this.setPosition(new Point(x, y));
		this.width = 70;
		this.height = 70;
		this.color = color;
		type = Constants.PROJECTILE_TYPE;
		
		double magnitude = Math.sqrt(vx*vx + vy*vy);
		if(color == Color.ORANGE){
			isGravitated = true;
			int speed = 45;
			this.setVelocity(new Point2D.Double(vx/magnitude*speed, vy/magnitude*speed));
		}
		else if(color == color.BLUE){
			int speed = 19;
			this.setVelocity(new Point2D.Double(vx/magnitude*speed, vy/magnitude*speed));
		}
		else if(color == color.YELLOW){
			int speed = 100;
			this.setVelocity(new Point2D.Double(vx/magnitude*speed, vy/magnitude*speed));
		}
	}
	
	public boolean isAffectedByGravity(){
		return isGravitated;
	}
}

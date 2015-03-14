package chromage.shared;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.Serializable;

public class Projectile extends Entity implements Serializable {
    static final long serialVersionUID = -50077493051991117L;
	boolean isGravitated = false;
	int damage = 1;
	
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
			damage = 3;
		}
		else if(color == color.BLUE){
			int speed = 19;
			this.setVelocity(new Point2D.Double(vx/magnitude*speed, vy/magnitude*speed));
			damage = 15;
		}
		else if(color == color.YELLOW){
			int speed = 100;
			this.setVelocity(new Point2D.Double(vx/magnitude*speed, vy/magnitude*speed));
			damage = 25;
		}
		else if(color == color.GREEN){
			int speed = 50;
			this.setVelocity(new Point2D.Double(vx/magnitude*speed, vy/magnitude*speed));
			damage = 1;
		}
	}
	
	public boolean isAffectedByGravity(){
		return isGravitated;
	}
}

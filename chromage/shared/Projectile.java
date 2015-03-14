package chromage.shared;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

public class Projectile extends Entity implements Serializable {
    static final long serialVersionUID = -50077493051991117L;
	boolean isGravitated = false;
	int damage = 1;
	int slowAmount = 100;
	public Projectile(int x, int y, double vx, double vy, int w, int h, int damage, int slowAmount, Color color){
		this.setPosition(new Point(x,y));
		this.width = w;
		this.height = h;
		this.damage = damage;
		this.slowAmount = slowAmount;
		this.setVelocity(new Point2D.Double(vx, vy));
		this.type = Constants.PROJECTILE_TYPE;
		this.color = color;
	}
	
	public boolean isAffectedByGravity(){
		return isGravitated;
	}

	@Override
	public void applyHits(ArrayList<Entity> entities) {
		System.out.println("Applying hits");
		//for each projectile, check if it should activate
			for(Entity target : entities){
				if(canCollideWith(target) && getHitbox().intersects(target.getHitbox())){
					target.takeDamage(damage, slowAmount);
					System.out.println("COLLISION! DELETING PROJECTILE!");
					setShouldBeRemoved(true);
				}
			}
	}
}

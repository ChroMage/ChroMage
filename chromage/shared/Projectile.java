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
	int knockup = 0;
	Mage owner = null;
	public Projectile(int x, int y, double vx, double vy, int w, int h, int damage, int slowAmount, int knockup, Color color, Mage owner){
		this.setPosition(new Point(x,y));
		this.width = w;
		this.height = h;
		this.damage = damage;
		this.slowAmount = slowAmount;
		this.setVelocity(new Point2D.Double(vx, vy));
		this.type = Constants.PROJECTILE_TYPE;
		this.color = color;
		this.owner = owner;
		this.knockup = knockup;
	}
	
	public boolean isAffectedByGravity(){
		return isGravitated;
	}

	@Override
	public void applyHits(ArrayList<Entity> entities) {
		//for each projectile, check if it should activate
		for(Entity target : entities){
			if(canCollideWith(target) && getHitbox().intersects(target.getHitbox())){
				hitTarget(target);
				setShouldBeRemoved(true);
			}
		}
	}
	
	public void hitTarget(Entity target){
		target.takeDamage(damage, slowAmount);
		target.applyKnockup(knockup);
	}

}

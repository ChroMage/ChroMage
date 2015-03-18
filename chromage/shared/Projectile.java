package chromage.shared;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;

public class Projectile extends Entity implements Serializable {
    static final long serialVersionUID = -50077493051991117L;
	boolean isGravitated = false;
	int damage = 1;
	int slowAmount = 100;
	int knockup = 0;
	int comboValue = 1;
	Mage owner = null;
	public Projectile(Rectangle2D.Double startRect, Point2D.Double velocity, int damage, int slowAmount, int knockup, Color color, Mage owner){
		this.setBounds(startRect);
		this.damage = damage;
		this.slowAmount = slowAmount;
		this.setVelocity(velocity);
		this.type = Constants.PROJECTILE_TYPE;
		this.color = color;
		this.owner = owner;
		this.knockup = knockup;
	}

    public Projectile(Point2D.Double initialPosition, double width, double height, Point2D.Double velocity, int damage, int slowAmount, int knockup, Color color, Mage owner){
        this(new Rectangle2D.Double(initialPosition.x, initialPosition.y, width, height), velocity, damage, slowAmount, knockup, color, owner);
    }

	public boolean isAffectedByGravity(){
		return isGravitated;
	}
	
	protected Entity getOwner() {
		return owner;
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
		target.takeDamage(damage, slowAmount, comboValue);
        target.setVelocity(
            Utilities.add(
                Utilities.scaleTo(
                        Utilities.subtract(target.getCenter(), this.getCenter()),
                        knockup
                ),
                target.getVelocity()
            )
        );
	}

}

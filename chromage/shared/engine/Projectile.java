package chromage.shared.engine;

import chromage.shared.Mage;
import chromage.shared.engine.Entity;
import chromage.shared.utils.Constants;
import chromage.shared.utils.Utilities;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;

public class Projectile extends Entity implements Serializable {
    static final long serialVersionUID = -50077493051991117L;
	protected boolean isGravitated = false;
	protected int damage = 1;
	protected int slowAmount = 100;
	protected int knockup = 0;
	protected int comboValue = 1;
	Mage owner = null;

	public Projectile(Rectangle2D.Double startRect, Point2D.Double velocity, int damage, int slowAmount, int knockup, Color color, Mage owner, boolean isAfftectedByGravity){
		this.setBounds(startRect);
		this.damage = damage;
		this.slowAmount = slowAmount;
		this.setVelocity(velocity);
        this.collisionBitMask = Constants.MAGE_TYPE | Constants.BLOCK_TYPE;
        this.categoryBitMask  = Constants.PROJECTILE_TYPE;
		this.color = color;
		this.owner = owner;
		this.knockup = knockup;
        this.isGravitated = isAfftectedByGravity;
	}

    public Projectile(Point2D.Double initialPosition, double width, double height, Point2D.Double velocity, int damage, int slowAmount, int knockup, Color color, Mage owner, boolean isAffectedByGravity){
        this(new Rectangle2D.Double(initialPosition.x, initialPosition.y, width, height), velocity, damage, slowAmount, knockup, color, owner, isAffectedByGravity);
    }

    @Override
    public boolean canCollideWith(Entity e) {
        return super.canCollideWith(e) && owner != e;
    }

	public boolean isAffectedByGravity(){
		return isGravitated;
	}
	
	protected Entity getOwner() {
		return owner;
	}

	@Override
	public void didCollideWith(Entity e) {
        hitTarget(e);
        setShouldBeRemoved(true);
	}
	
	public void hitTarget(Entity target){
		target.takeDamage(damage, slowAmount, comboValue);
        double elasticity = 0.5;
        double pureVertical = 0.7;
        target.setVelocity(
                Utilities.addAll(
                        Utilities.scaleTo(
                                Utilities.subtract(target.getCenter(), this.getCenter()),
                                knockup * elasticity * (1 - pureVertical)
                        ),
                        Utilities.scaleTo(
                                getVelocity(),
                                knockup * (1 - elasticity) * (1 - pureVertical)
                        ),
                        new Point2D.Double(0, -knockup * pureVertical),
                        target.getVelocity()
                )
        );
	}

}

package chromage.shared;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

public abstract class Spell implements Serializable{
	public abstract int getKnockup();
	public abstract double getWidth();
	public abstract double getHeight();
	public abstract int getStun();
	public abstract int getInvuln();
	public abstract int getCast();
	public abstract int getSlow();
	public abstract int getCoolDown();
	public abstract int getManaCost();
	public abstract int getDamage();
	public abstract int getSpeed();
	public abstract Color getColor();
	public abstract boolean isAffectedByGravity();

    public Point2D.Double getProjectileStartPosition(Mage mage, Point2D.Double direction) {
        // fire from a circle around your character
        return Utilities.add(
                mage.getCenter(),
                Utilities.scaleTo(direction, Math.sqrt(mage.getWidth()*mage.getWidth() + mage.getHeight()*mage.getHeight()))
        );
    }

	public Projectile createProjectile(Mage mage, Point2D.Double target, GameState state) {
        Point2D.Double direction = new Point2D.Double(target.getX() - mage.getPosition().getX(), target.getY() - mage.getPosition().getY());
        Point2D.Double startPosition =  getProjectileStartPosition(mage, direction);
		Projectile p = new Projectile(startPosition, getWidth(), getHeight(), Utilities.scaleTo(direction, getSpeed()),
                getDamage(), getSlow(), getKnockup(), getColor(), mage);
		p.isGravitated = isAffectedByGravity();
		return p;
	}
}

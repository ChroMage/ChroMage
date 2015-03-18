package chromage.shared;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.Serializable;

public abstract class Spell implements Serializable{
	public abstract int getKnockup();
	public abstract int getWidth();
	public abstract int getHeight();
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
	public Projectile createProjectile(Mage mage, Point2D.Double target, GameState state) {
		Point2D.Double direction = new Point2D.Double(target.getX() - mage.getPosition().getX(), target.getY() - mage.getPosition().getY());
		int x = (int) (mage.getPosition().getX() + direction.getX()/direction.distance(0, 0)*mage.getHeight());
		int y = (int) (mage.getPosition().getY() + direction.getY()/direction.distance(0, 0)*mage.getHeight());
		Projectile p = new Projectile(x, y, 
						(int)(direction.x/direction.distance(0, 0)*getSpeed()), (int)(direction.y/direction.distance(0,0)*getSpeed()),
				        getWidth(), getHeight(), 
				        getDamage(), getSlow(), getKnockup(), getColor(), mage);
		p.isGravitated = isAffectedByGravity();
		return p;
	}
}

package chromage.shared.spells;

import chromage.shared.Mage;
import chromage.shared.engine.Entity;
import chromage.shared.engine.Projectile;
import chromage.shared.engine.GameState;
import chromage.shared.utils.Utilities;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

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
        // fire from the center your character
        return new Point2D.Double(mage.getCenter().x - getWidth()/2, mage.getCenter().y - getHeight()/2);
    }

	public ArrayList<Projectile> createProjectiles(Mage mage, Point2D.Double target, ArrayList<Entity> entities) {
        Point2D.Double direction = new Point2D.Double(target.getX() - mage.getCenter().getX(), target.getY() - mage.getCenter().getY());
        Point2D.Double startPosition =  getProjectileStartPosition(mage, direction);
		Projectile p = new Projectile(startPosition, getWidth(), getHeight(), Utilities.scaleTo(direction, getSpeed()),
                getDamage(), getSlow(), getKnockup(), getColor(), mage, isAffectedByGravity());
        ArrayList<Projectile> a = new ArrayList<Projectile>();
        a.add(p);
		return a;
	}
}

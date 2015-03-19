package chromage.shared.spells;

import chromage.shared.Mage;
import chromage.shared.engine.Entity;
import chromage.shared.engine.Projectile;
import chromage.shared.utils.Utilities;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A generic spell
 */
public abstract class Spell implements Serializable {
    /**
     * Get the speed at which this spell launches its victim upon hit
     * @return
     */
    public abstract int getKnockup();

    /**
     * Get the width of the spell's projectile
     * @return
     */
    public abstract double getWidth();

    /**
     * Get the height of the spell's projectile
     * @return
     */
    public abstract double getHeight();

    /**
     * Get the amount of time the victim is stunned after hit
     * @return
     */
    public abstract int getStun();

    /**
     * Get the amount of time the victim is invulnerable after hit
     * @return
     */
    public abstract int getInvulnerability();

    /**
     * Get the amount of time the spell takes to cast
     * @return
     */
    public abstract int getCast();

    /**
     * Get the amount of slow this spell causes to the victim
     * @return
     */
    public abstract int getSlow();

    /**
     * Get the number of ticks of cooldown this spell causes
     * @return
     */
    public abstract int getCoolDown();

    /**
     * Get the mana cost of this spell
     * @return
     */
    public abstract int getManaCost();

    /**
     * Get the damage this spell inflicts
     * @return
     */
    public abstract int getDamage();

    /**
     * Get the speed of this spell's projectile
     * @return
     */
    public abstract int getSpeed();

    /**
     * Get the color of this spell's projectile
     * @return
     */
    public abstract Color getColor();

    /**
     * Get whether or not this spell's projectile is affected by gravity
     * @return
     */
    public abstract boolean isAffectedByGravity();

    /**
     * Get the position at which to spawn this spell's projectile
     * @param mage      the mage casting the spell
     * @param direction the direction in which the mage is casting the spell
     * @return  the position at which the projectile should spawn
     */
    public Point2D.Double getProjectileStartPosition(Mage mage, Point2D.Double direction) {
        return new Point2D.Double(mage.getCenter().x - getWidth() / 2, mage.getCenter().y - getHeight() / 2);
    }

    /**
     * Perform the action caused by casting this spell.
     * @param mage      the caster
     * @param target    the caster's mouse location
     * @param entities  the entities in the battlefield at the time of casting
     * @return          a collection of any projectiles created by casting this spell
     */
    public ArrayList<Projectile> cast(Mage mage, Point2D.Double target, Collection<Entity> entities) {
        Point2D.Double direction = new Point2D.Double(target.getX() - mage.getCenter().getX(), target.getY() - mage.getCenter().getY());
        Point2D.Double startPosition = getProjectileStartPosition(mage, direction);
        Projectile p = new Projectile.Builder()
                            .position(startPosition)
                            .width(getWidth())
                            .height(getHeight())
                            .velocity(Utilities.scaleTo(direction, getSpeed()))
                            .damage(getDamage())
                            .slowAmount(getSlow())
                            .knockup(getKnockup())
                            .color(getColor())
                            .owner(mage)
                            .isAffectedByGravity(isAffectedByGravity())
                            .build();
        ArrayList<Projectile> a = new ArrayList<Projectile>();
        a.add(p);
        return a;
    }
}

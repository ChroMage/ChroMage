package chromage.shared.spells;

import chromage.shared.Mage;
import chromage.shared.engine.Entity;
import chromage.shared.engine.Projectile;
import chromage.shared.utils.Utilities;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;

public class Blink extends Spell {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public int getStun() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getInvulnerability() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getCast() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getSlow() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getCoolDown() {
        // TODO Auto-generated method stub
        return 10;
    }

    @Override
    public int getManaCost() {
        // TODO Auto-generated method stub
        return 100;
    }

    @Override
    public int getDamage() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getWidth() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getHeight() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getSpeed() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Color getColor() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isAffectedByGravity() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public ArrayList<Projectile> cast(Mage mage, Point2D.Double target, Collection<Entity> entities) {
        Rectangle2D.Double newHitBox = new Rectangle2D.Double(target.x, target.y, 1, 1);
        boolean canBlink = true;
        for (Entity e : entities) {
            if (mage.canCollideWith(e) && Utilities.areasIntersect(new Area(newHitBox), e.getHitbox())) {
                canBlink = false;
            }
        }
        if (canBlink) {
            mage.setPosition(target);
        }
        return new ArrayList<Projectile>();
    }

    @Override
    public int getKnockup() {
        // TODO Auto-generated method stub
        return 0;
    }

}

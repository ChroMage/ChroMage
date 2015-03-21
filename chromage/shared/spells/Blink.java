package chromage.shared.spells;

import chromage.shared.Mage;
import chromage.shared.engine.Entity;
import chromage.shared.engine.Projectile;
import chromage.shared.utils.Utilities;

import java.awt.*;
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
        Rectangle2D.Double newHitBox = new Rectangle2D.Double(target.x - mage.getWidth()/2, target.y - getHeight()/2, mage.getWidth(), mage.getHeight());
        boolean canBlink = true;
        for (Entity e : entities) {
            if (mage.canCollideWith(e) && newHitBox.intersects(e.getHitbox())) {
                Rectangle2D intersect = newHitBox.createIntersection(e.getHitbox());
                if (intersect.equals(newHitBox)) {
                	canBlink = false;
                }
                double differenceX = newHitBox.getCenterX() - intersect.getCenterX();
                double differenceY = newHitBox.getCenterY() - intersect.getCenterY();
                Point2D.Double dir = Utilities.normalize(new Point2D.Double(differenceX, differenceY));
                while(newHitBox.intersects(e.getHitbox())){
                	newHitBox = Utilities.moveRectangle(newHitBox, dir);
                }
                
            }
        }
        if (canBlink) {
            mage.setPosition(newHitBox.x, newHitBox.y);
        }
        return new ArrayList<Projectile>();
    }

    @Override
    public int getKnockup() {
        // TODO Auto-generated method stub
        return 0;
    }

}

package chromage.shared.spells;

import chromage.shared.Mage;
import chromage.shared.engine.Entity;
import chromage.shared.engine.GameState;
import chromage.shared.engine.Projectile;
import chromage.shared.utils.Utilities;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;

public class Lightning extends Spell {

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
        return 100;
    }

    @Override
    public int getCoolDown() {
        // TODO Auto-generated method stub
        return 30;
    }

    @Override
    public int getManaCost() {
        // TODO Auto-generated method stub
        return 240;
    }

    @Override
    public int getDamage() {
        // TODO Auto-generated method stub
        return 120;
    }

    @Override
    public double getWidth() {
        // TODO Auto-generated method stub
        return 50;
    }

    @Override
    public double getHeight() {
        // TODO Auto-generated method stub
        return 50;
    }

    @Override
    public int getSpeed() {
        return 100;
    }

    @Override
    public Color getColor() {
        // TODO Auto-generated method stub
        return Color.YELLOW;
    }

    @Override
    public boolean isAffectedByGravity() {
        // TODO Auto-generated method stub
        return false;
    }

    public ArrayList<Projectile> createProjectile(Mage mage, Point2D.Double target, GameState state) {
        Point2D.Double direction = new Point2D.Double(target.getX() - mage.getCenter().getX(), target.getY() - mage.getCenter().getY());
        Point2D.Double startPosition = getProjectileStartPosition(mage, direction);
        Projectile p = new LightingProjectile.Builder()
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

    @Override
    public int getKnockup() {
        // TODO Auto-generated method stub
        return 80;
    }

    public static class LightingProjectile extends Projectile {
        public static class Builder extends Init<LightingProjectile, Builder> {
            @Override
            protected Builder self() {
                return this;
            }

            @Override
            public LightingProjectile build() {
                return new LightingProjectile(this);
            }
        }
        public Collection<? extends Entity> update(Collection<Entity> e) {
            super.update(e);
            setWidth(getWidth() + 3);
            setHeight(getHeight() + 3);
            return new ArrayList<Entity>();
        }

        protected LightingProjectile(Init<?, ?> init) {
            super(init);
        }
    }

}

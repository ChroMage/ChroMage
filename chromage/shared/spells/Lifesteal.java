package chromage.shared.spells;

import chromage.shared.Mage;
import chromage.shared.engine.Damagable;
import chromage.shared.engine.Entity;
import chromage.shared.engine.Projectile;
import chromage.shared.utils.Utilities;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;

public class Lifesteal extends Spell {

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
        return 90;
    }

    @Override
    public int getCoolDown() {
        // TODO Auto-generated method stub
        return 2;
    }

    @Override
    public int getManaCost() {
        // TODO Auto-generated method stub
        return 10;
    }

    @Override
    public int getDamage() {
        // TODO Auto-generated method stub
        return 2;
    }

    public int getHeal() {
        return 1;
    }

    @Override
    public double getWidth() {
        // TODO Auto-generated method stub
        return 70;
    }

    @Override
    public double getHeight() {
        // TODO Auto-generated method stub
        return 70;
    }

    @Override
    public int getSpeed() {
        // TODO Auto-generated method stub
        return 50;
    }

    @Override
    public Color getColor() {
        // TODO Auto-generated method stub
        return Color.GREEN;
    }

    @Override
    public int getKnockup() {
        // TODO Auto-generated method stub
        return 10;
    }

    @Override
    public boolean isAffectedByGravity() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public ArrayList<Projectile> cast(Mage mage, Point2D.Double target, Collection<Entity> entities) {
        Point2D.Double direction = new Point2D.Double(target.getX() - mage.getCenter().getX(), target.getY() - mage.getCenter().getY());
        Point2D.Double startPosition = getProjectileStartPosition(mage, direction);
        Projectile p = new LifestealProjectile.Builder()
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
                .healAmount(getHeal())
                .build();
        ArrayList<Projectile> a = new ArrayList<Projectile>();
        a.add(p);
        return a;
    }

    public static class LifestealProjectile extends chromage.shared.engine.Projectile {
        private int healAmount = 0;

        public static class Builder extends Init<LifestealProjectile, Builder> {
            private int healAmount;
            public Builder healAmount(int amount) {
                this.healAmount = amount;
                return self();
            }
            @Override
            protected Builder self() {
                return this;
            }

            @Override
            public LifestealProjectile build() {
                return new LifestealProjectile(this);
            }

        }

        protected LifestealProjectile(Builder init) {
            super(init);
            this.healAmount = init.healAmount;
        }

        private static final long serialVersionUID = 188689086533652783L;

        protected Mage getOwner() {
            return (Mage) super.getOwner();
        }

        public void processCollision(Damagable target) {
            super.processCollision(target);
            getOwner().healDamage(healAmount);
        }
    }
}

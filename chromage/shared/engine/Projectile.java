package chromage.shared.engine;

import chromage.shared.Mage;
import chromage.shared.utils.Constants;
import chromage.shared.utils.Utilities;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

/**
 * A generic projectile
 */
public class Projectile extends MobileEntity implements Serializable, CollisionProcessor {
    static final long serialVersionUID = -50077493051991117L;

    /**
     * The amount of damage this projectile does when it hits something.
     */
    protected int damage = 1;

    /**
     * The percentage by which this projectile slows its target
     */
    protected int slowAmount = 100;

    /**
     * The velocity at which this projectile launches its target assuming optimal hit angle
     */
    protected int knockup = 0;
    /**
     * The amount by which this projectile increases the combo counter
     */
    protected int comboValue = 1;

    /**
     * The owner of this projectile
     */
    Entity owner = null;

    protected Projectile(Init<?, ?> init) {
        this.setBounds(new Rectangle2D.Double(init.x, init.y, init.width, init.height));
        this.damage = init.damage;
        this.slowAmount = init.slowAmount;
        this.setVelocity(init.velocityX, init.velocityY);
        this.color = init.color;
        this.owner = init.owner;
        this.knockup = init.knockup;
        this.comboValue = init.comboValue;
        super.setIgnoresGravity(!init.isAffectedByGravity);
        this.collisionBitMask = Constants.MAGE_TYPE | Constants.BLOCK_TYPE;
        this.categoryBitMask = Constants.PROJECTILE_TYPE;
    }

    @Override
    public boolean canCollideWith(Entity e) {
        return super.canCollideWith(e) && owner != e;
    }

    protected Entity getOwner() {
        return owner;
    }

    @Override
    public void didCollideWith(Entity e) {
        e.acceptCollisionFrom(this);
    }

    public void processCollision(Entity e) {
    }

    public void processCollision(MobileEntity target) {
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
                        target.velocity
                )
        );
        setShouldBeRemoved(true);
    }

    public void processCollision(Slowable target) {
        target.slowBy(slowAmount);
        setShouldBeRemoved(true);
    }

    public void processCollision(Damagable target) {
        target.takeDamage(damage);
        setShouldBeRemoved(true);
    }

    public void processCollision(Comboable target) {
        target.addCombo(comboValue);
        setShouldBeRemoved(true);
    }

    public void processCollision(Block b) {
        setShouldBeRemoved(true);
    }

    /**
     * Subclass-able builder class
     *
     * @param <T> the subclass
     */
    public static abstract class Init<P, T extends Init<P, T>> {
        private int damage = 1;
        private int slowAmount = 100;
        private int knockup = 0;
        private int comboValue = 1;
        private double x = 0;
        private double y = 0;
        private double width = 100;
        private double height = 50;
        private double velocityX = 0;
        private double velocityY = 0;
        private Mage owner = null;
        private Color color = Color.MAGENTA;
        private boolean isAffectedByGravity = false;

        public T damage(int damage) {
            this.damage = damage;
            return self();
        }

        public T slowAmount(int slowAmount) {
            this.slowAmount = slowAmount;
            return self();
        }

        public T knockup(int knockup) {
            this.knockup = knockup;
            return self();
        }

        public T comboValue(int comboValue) {
            this.comboValue = comboValue;
            return self();
        }

        public T x(double x) {
            this.x = x;
            return self();
        }

        public T y(double y) {
            this.y = y;
            return self();
        }

        public T width(double width) {
            this.width = width;
            return self();
        }

        public T height(double height) {
            this.height = height;
            return self();
        }

        public T velocityX(double velocityX) {
            this.velocityX = velocityX;
            return self();
        }

        public T velocityY(double velocityY) {
            this.velocityY = velocityY;
            return self();
        }

        public T velocity(Point2D.Double velocity) {
            this.velocityX = velocity.x;
            this.velocityY = velocity.y;
            return self();
        }

        public T owner(Mage owner) {
            this.owner = owner;
            return self();
        }

        public T color(Color color) {
            this.color = color;
            return self();
        }

        public T isAffectedByGravity(boolean isAffectedByGravity) {
            this.isAffectedByGravity = isAffectedByGravity;
            return self();
        }

        public T position(Point2D.Double p) {
            this.x = p.x;
            this.y = p.y;
            return self();
        }

        public T bounds(Rectangle2D.Double rect) {
            this.x = rect.getX();
            this.y = rect.getY();
            this.width = rect.getWidth();
            this.height = rect.getHeight();
            return self();
        }

        protected abstract T self();

        public abstract P build();
    }

    public static class Builder extends Init<Projectile, Builder> {
        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public Projectile build() {
            return new Projectile(this);
        }
    }
}

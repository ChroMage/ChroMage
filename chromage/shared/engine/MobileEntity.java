package chromage.shared.engine;

import chromage.shared.utils.Utilities;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;

/**
 * An entity that can has a velocity and thus moves each tick.
 */
public class MobileEntity extends Entity {
    /**
     * This entity's current velocity. Positive Y is down, Positive X is right.
     */
    protected Point2D.Double velocity = new Point2D.Double(0, 0);

    /**
     * True if this entity should ignore gravity, false if it shouldn't.
     */
    private boolean ignoresGravity = false;

    /**
     * Reset the vertical velocity to 0
     */
    public void zeroVerticalVelocity() {
        velocity.setLocation(velocity.getX(), 0.0);
    }

    public boolean isAffectedByGravity() {
        return !ignoresGravity;
    }

    /**
     * @param entities the entities in the battlefield
     * @return  the collection of entities created this tick
     */
    public Collection<? extends Entity> update(Collection<Entity> entities) {
        updatePosition(entities);
        applyGravity();
        super.update(entities);
        return new ArrayList<Entity>();
    }

    public void applyGravity() {
        if (isAffectedByGravity()) {
            this.velocity.y += 2;
        }
    }

    /**
     * Handle collisions. Move this object back in the direction it was moving until it no longer intersects the
     * object it collided with. This may be overkill given the new movement scheme, but it works.
     *
     * @param e the entity this entity collided with
     */
    @Override
    public void didCollideWith(Entity e) {
        Rectangle2D.Double intersectionBounds = new Rectangle2D.Double();
        Rectangle2D.intersect(getHitbox(), e.getHitbox(), intersectionBounds);
        Point2D.Double d = Utilities.invert(Utilities.normalize(getVelocity()));

        while (getHitbox().intersects(e.getHitbox())) {
            moveBy(d);
        }

        if (intersectionBounds.getMinY() == e.getHitbox().getBounds().getMinY() && canCollideWith(e)) {
            // hit the top of the object
            hitGround();
        } else if (intersectionBounds.getMaxY() == e.getHitbox().getBounds().getMaxY()) {
            // hit the bottom of the object
            velocity.y = 0;
        } else if (intersectionBounds.getMinX() == e.getHitbox().getBounds().getMinX()) {
            // hit the right of the object
            velocity.x = 0;
        } else if (intersectionBounds.getMaxX() == e.getHitbox().getBounds().getMaxX()) {
            // hit the left  of the object
            velocity.x = 0;
        }
    }

    /**
     * Called whenever this entity touches the ground.
     */
    public void hitGround() {
        isGrounded = true;
        zeroVerticalVelocity();
    }

    /**
     * Move this entity. Move one unit at a time until we just barely overlap an entity we can collide with, then stop.
     * @param entities
     */
    public void updatePosition(Collection<Entity> entities) {
        if (velocity.y < 0) isGrounded = false;
        if (Utilities.length(getVelocity()) == 0) return;
        Point2D.Double normalized = Utilities.scaleTo(getVelocity(), 2);
        int desiredDistance = (int)(Utilities.length(getVelocity()));
        for (int i = 0; i < desiredDistance; i += 2) {
            moveBy(normalized);

            for (Entity e : entities) {
                if (canCollideWith(e) && getHitbox().intersects(e.getHitbox())) {
                    Rectangle2D.Double intersectionBounds = new Rectangle2D.Double();
                    Rectangle2D.intersect(getHitbox(), e.getHitbox(), intersectionBounds);

                    if (intersectionBounds.getMinY() == e.getHitbox().getBounds().getMinY()) {
                        // hit the top of the object
                        normalized.y = 0;
                    } else if (intersectionBounds.getMaxY() == e.getHitbox().getBounds().getMaxY()) {
                        // hit the bottom of the object
                        normalized.y = 0;
                    } else if (intersectionBounds.getMinX() == e.getHitbox().getBounds().getMinX()) {
                        // hit the right of the object
                        normalized.x = 0;
                    } else if (intersectionBounds.getMaxX() == e.getHitbox().getBounds().getMaxX()) {
                        // hit the left  of the object
                        normalized.x = 0;
                    }
                }
            }

            if (Utilities.length(normalized) == 0) break;
        }
    }

    public Point2D.Double getVelocity() {
        return velocity;
    }

    public void setVelocity(Point2D.Double velocity) {
        this.velocity = velocity;
    }

    public void setIgnoresGravity(boolean ignoresGravity) {
        this.ignoresGravity = ignoresGravity;
    }

    public void setVelocity(double velocityX, double velocityY) {
        setVelocity(new Point2D.Double(velocityX, velocityY));
    }
}

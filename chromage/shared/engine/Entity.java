package chromage.shared.engine;

import chromage.shared.utils.Utilities;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class Entity implements Serializable {
    public static final double DEFAULT_WIDTH = 100;
    public static final double DEFAULT_HEIGHT = 100;
    static final long serialVersionUID = -50077493051991117L;
    protected Color color = Color.MAGENTA;
    /**
     * Tells what categories of Entities this Entity can collide with.
     *
     * @see Entity::canCollideWith
     */
    protected int collisionBitMask;
    /**
     * Tells what category of entity this is.
     *
     * @see Entity::canCollideWith
     */
    protected int categoryBitMask;
    /**
     * True if this entity is currently on the ground, false otherwise
     */
    protected boolean isGrounded = false;
    /**
     * The hitbox of this entity. For now we just use rectangles
     */
    private Rectangle2D.Double bounds = new Rectangle2D.Double();
    /**
     * True if this entity should be removed from the game next tick; false otherwise
     */
    private boolean shouldBeRemoved = false;

    /**
     * Draw this entity in the given graphics context. Scale all dimensions by the given width and  height factors
     *
     * @param g            the graphics context in which to draw this entity
     * @param heightFactor the amount by which to scale y dimensions
     * @param widthFactor  the amount by which to scale x dimensions
     */
    public void draw(Graphics g, double heightFactor, double widthFactor) {
        int x = (int) (getPosition().x * widthFactor);
        int y = (int) (getPosition().y * heightFactor);
        g.setColor(color);
        g.fillRect(x, y, (int) (getWidth() * widthFactor), (int) (getHeight() * heightFactor));
    }

    /**
     * Check if two entities can collide.
     * Entity A can collide with Entity B if A's collisionBitMask matches at least one bit of Entity B's category:
     * that is, if
     * <p/>
     * collisionBitMask & e.categoryBitMask != 0
     * <p/>
     * Note that subclasses can override this method, though, so more complex criteria can be expressed through
     * subclass overrides if necessary.
     * <p/>
     * Also note that this means not all collisions are bi-directional.
     *
     * @param e the entity to check if collisions are allowed with
     * @return true iff this entity can collide with the other entity.
     */
    public boolean canCollideWith(Entity e) {
        return (collisionBitMask & e.categoryBitMask) != 0 && e != this;
    }

    /**
     * Update this entity for this tick.
     *
     * @param entities the entities in the battlefield
     * @return any entities created by this one during this tick
     */
    public Collection<? extends Entity> update(Collection<Entity> entities) {
        return new ArrayList<Entity>();
    }

    /**
     * @return the area of this entity which should be counted as colliding with other entities
     */
    public Area getHitbox() {
        return new Area(getBounds());
    }

    /**
     * Moves this entity along a vector
     *
     * @param p the vector by which to move this entity
     */
    public void moveBy(Point2D.Double p) {
        setPosition(Utilities.add(getPosition(), p));
    }

    /**
     * Accept a CollisionProcessor (visitor)
     *
     * @param p the visitor
     */
    public void acceptCollisionFrom(CollisionProcessor p) {
        p.processCollision(this);
    }

    /**
     * Called by the engine whenever any collision occurs
     *
     * @param e the entity this entity collided with
     */
    public void didCollideWith(Entity e) {
    }

    public Rectangle2D.Double getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle2D.Double bounds) {
        this.bounds = bounds;
    }

    public Point2D.Double getCenter() {
        return new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
    }

    public Point2D.Double getPosition() {
        return new Point2D.Double(bounds.getX(), bounds.getY());
    }

    public void setPosition(Point2D.Double position) {
        bounds.setFrame(position.x, position.y, getWidth(), getHeight());
    }

    public void setPosition(double x, double y) {
        setPosition(new Point2D.Double(x, y));
    }

    public double getWidth() {
        return bounds.getWidth();
    }

    public void setWidth(double width) {
        bounds.width = width;
    }

    public double getHeight() {
        return bounds.getHeight();
    }

    public void setHeight(double height) {
        bounds.height = height;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setShouldBeRemoved(boolean v) {
        shouldBeRemoved = v;
    }

    public boolean shouldBeRemoved() {
        return shouldBeRemoved;
    }

}

package chromage.shared.engine;

import chromage.shared.utils.Constants;
import chromage.shared.utils.Utilities;

import javax.rmi.CORBA.Util;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;

public class Entity implements Serializable {
    static final long serialVersionUID = -50077493051991117L;

    public static final double DEFAULT_WIDTH = 100;
    public static final double DEFAULT_HEIGHT = 100;

    public Rectangle2D.Double bounds = new Rectangle2D.Double();
	public Point2D.Double velocity = new Point2D.Double(0,0);
	protected Color color = Color.MAGENTA;
	protected boolean isMobile = true;

    /**
     * Tells what categories of Entities this Entity can collide with.
     */
    protected int collisionBitMask;

    /**
     * Tells what category of entity this is.
     */
    protected int categoryBitMask;

	protected boolean isGrounded = false;

	private boolean shouldBeRemoved = false;

	public void draw(Graphics g, double heightFactor, double widthFactor) {
		int x = (int)(getPosition().x*widthFactor);
		int y = (int)(getPosition().y*heightFactor);
		g.setColor(color);
		g.fillRect(x, y, (int) (getWidth() * widthFactor), (int) (getHeight() * heightFactor));
	}
	public void takeDamage(int dmg, int slowAmount, int comboValue) { }
    public void healDamage(int damage) { }
	public void zeroVerticalVelocity() {
		velocity.setLocation(velocity.getX(), 0.0);
	}
	public boolean isAffectedByGravity(){ return false; }
	
	public boolean canCollideWith(Entity e) {
        return (collisionBitMask & e.categoryBitMask) != 0 && e != this;
	}
	
	public void applyFriction() { }
	
	public ArrayList<? extends Entity> update(ArrayList<Entity> entities){
		applyGravity();
		applyFriction();
		updatePosition(entities);
        return new ArrayList<Entity>();
	}

	public void applyGravity() {
		if (isAffectedByGravity()){
			this.velocity.y += 2;
		}
	}
	
	public Rectangle2D.Double getHitbox() {
		return getBounds();
	}

    public void moveBy(Point2D.Double p) {
        setPosition(Utilities.add(getPosition(), p));
    }

    public void didCollideWith(Entity e) {
        Rectangle2D.Double intersection = new Rectangle2D.Double();
        Rectangle2D.intersect(getHitbox(), e.getHitbox(), intersection);

        Point2D.Double d = Utilities.subtract(new Point2D.Double(), Utilities.normalize(getVelocity()));

        if (intersection.getMinX() == getHitbox().getMinX() && getHitbox().getMaxX() == intersection.getMaxX()) {
            // if the object extends past us in either direction in X, moving in X won't get us away from it.
            if (d.y != 0) d.x = 0;
        }
        if (intersection.getMinY() == getHitbox().getMinY() && getHitbox().getMaxY() == intersection.getMaxY()) {
            // if the object extends past us in either direction in Y, moving in Y won't get us away from it.
            if (d.x != 0) d.y = 0;
        }
        while (getHitbox().intersects(e.getHitbox())) {
            moveBy(d);
        }
        if (intersection.getMinY() == e.getHitbox().getMinY()) {
            // hit the top of the object
            hitGround();
        } else if (intersection.getMaxY() == e.getHitbox().getMaxY()) {
            // hit the bottom of the object
            zeroVerticalVelocity();
        } else if (intersection.getMinX() == e.getHitbox().getMinX()) {
            // hit the right of the object
            velocity.x = 0;
        } else if (intersection.getMaxX() == e.getHitbox().getMaxX()) {
            // hit the left  of the object
            velocity.x = 0;
        }
    }

    public void hitGround() {
        isGrounded = true;
        zeroVerticalVelocity();
    }

	public void updatePosition(ArrayList<Entity> entities) {
		if (isMobile) {
            setPosition(Utilities.add(getPosition(), getVelocity()));
			if (velocity.y < 0) isGrounded = false;
			for (Entity e : entities) {
                if (canCollideWith(e) && getHitbox().intersects(e.getHitbox())) {
                    didCollideWith(e);
                }
			}
		}
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
        setPosition(new Point2D.Double(x,y));
    }

	public Point2D.Double getVelocity() {
		return velocity;
	}

	public void setVelocity(Point2D.Double velocity) {
		this.velocity = velocity;
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
    public boolean shouldBeRemoved() { return shouldBeRemoved; }
}

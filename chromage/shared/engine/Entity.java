package chromage.shared.engine;

import chromage.shared.utils.Constants;
import chromage.shared.utils.Utilities;

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
//    private Point2D.Double position = new Point2D.Double(2000,2000);
//	protected double width = DEFAULT_WIDTH;
//	protected double height = DEFAULT_HEIGHT;
	protected Color color = Color.MAGENTA;
	protected boolean isMobile = true;
	protected int type = 0;
	public int getType() {
		return type;
	}

	protected boolean isGrounded = false;
	private boolean shouldBeRemoved = false;
	public void setShouldBeRemoved(boolean v) {
		shouldBeRemoved = v;
	}
	public boolean shouldBeRemoved() { return shouldBeRemoved; }
	
	public void draw(Graphics g, double heightFactor, double widthFactor) {
		int x = (int)(getPosition().x*widthFactor);
		int y = (int)(getPosition().y*heightFactor);
		g.setColor(color);
//        ((Graphics2D)g).fill(bounds);
		g.fillRect(x, y, (int) (getWidth() * widthFactor), (int) (getHeight() * heightFactor));
	}
	public void takeDamage(int dmg, int slowAmount, int comboValue) { }
    public void healDamage(int damage) { }
	public void zeroVerticalVelocity() {
		velocity.setLocation(velocity.getX(), 0.0);
	}
	public boolean isAffectedByGravity(){ return false; }
	
	public boolean canCollideWith(Entity e){
		//collide if I am a projectile and they aren't and they aren't my owner
		return ((e.type & Constants.PROJECTILE_TYPE) == 0)
			&& ((type & Constants.PROJECTILE_TYPE) != 0)
			&& getOwner() != e;
	}
	
	protected Entity getOwner() {
		return null;
	}
	
	public void addUpRightVelocity(int x, int y){
		if(isMobile){
			this.velocity.x += x;
			this.velocity.y -= y;
		}
	}
	public void applyFriction() {
		if((type & Constants.MAGE_TYPE) != 0){
			if(Math.abs(this.velocity.x) > .4){
				this.velocity.x -= .5*Math.signum(this.velocity.x);
			}
			else{
				this.velocity.x = 0;
			}
			int maxXVelocity = 15;
			if(this.velocity.x > maxXVelocity){
				this.velocity.x = maxXVelocity;
			}
			if(this.velocity.x < -1*maxXVelocity){
				this.velocity.x = -1*maxXVelocity;
			}
		}
	}
	
	public void update(ArrayList<Entity> entities){
		applyGravity();
		applyFriction();
		updatePosition(entities);
		applyHits(entities);
	}
	
	public void applyGravity(){
		if(isAffectedByGravity()){
			this.velocity.y += 2;
		}
	}
	
	public Rectangle2D.Double getHitbox() {
		return getBounds();
	}

	public void updatePosition(ArrayList<Entity> entities) {
		if(isMobile) {
            setPosition(Utilities.add(getPosition(), getVelocity()));
			if (velocity.y < 0) isGrounded = false;
			for(Entity e : entities){
				if(((type & Constants.MAGE_TYPE) != 0)){
					//Stop the object on top of immobile objects
					if(((e.type & Constants.BLOCK_TYPE) != 0) && overlapsTheTopOf(e)){
						this.velocity.y = 0;
                        setPosition(getPosition().getX(), e.getBounds().getMinY() - getHeight() + 1);
						isGrounded = true;
						this.clearCombo();
					}
					
					//Stop the object on hitting ceiling
					if(((e.type & Constants.BLOCK_TYPE) != 0) && overlapsTheBottomOf(e)){
						this.velocity.y = 0;
                        setPosition(getPosition().getX(), e.getBounds().getMaxY() + 1);
					}
					
					//stop when hitting a wall going right
					if(((e.type & Constants.BLOCK_TYPE) != 0) && overlapsLeftOf(e)){
						this.velocity.x = 0;
                        setPosition(e.getBounds().getMinX() - getWidth() - 1, getPosition().getY());
					}
					
					//stop when hitting a wall going left
					if(((e.type & Constants.BLOCK_TYPE) != 0) && overlapsRightOf(e)){
						this.velocity.x = 0;
                        setPosition(e.getBounds().getMaxX() + 1, getPosition().getY());
					}
				}
				else if(((e.type & Constants.BLOCK_TYPE) != 0) && getHitbox().intersects(e.getHitbox())){
					/*
					 * insert code here for projectile hits wall
					 */
					this.setShouldBeRemoved(true);
				}
			}
		}
	}

	//override these if needed
	public void clearCombo() {
	}
	public void applyHits(ArrayList<Entity> entities) {
	}
	protected void applyKnockup(int knockup) {
	}
	
	private boolean overlapsTheBottomOf(Entity wall) {
        Rectangle2D.Double r = new Rectangle2D.Double();
        Rectangle2D.intersect(bounds, wall.getBounds(), r);
        return !r.isEmpty() && r.getMaxY() == wall.getBounds().getMaxY();
	}

	private boolean overlapsRightOf(Entity wall) {
        Rectangle2D.Double r = new Rectangle2D.Double();
        Rectangle2D.intersect(bounds, wall.getBounds(), r);
        return !r.isEmpty() && r.getMaxX() == wall.getBounds().getMaxX();
	}

	private boolean overlapsLeftOf(Entity wall) {
        Rectangle2D.Double r = new Rectangle2D.Double();
        Rectangle2D.intersect(bounds, wall.getBounds(), r);
        return !r.isEmpty() && r.getMinX() == wall.getBounds().getMinX();
	}

	private boolean overlapsTheTopOf(Entity wall) {
        Rectangle2D.Double r = new Rectangle2D.Double();
        Rectangle2D.intersect(bounds, wall.getBounds(), r);
        return !r.isEmpty() && r.getMinY() == wall.getBounds().getMinY();
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
    
	public void addCombo(int comboValue) {
		
	}
}

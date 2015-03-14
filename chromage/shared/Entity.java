package chromage.shared;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;

public class Entity implements Serializable {
    static final long serialVersionUID = -50077493051991117L;

    private Point position = new Point(2000,2000);
	public Point2D.Double velocity = new Point2D.Double(0,0);

	protected int width = 100;
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	protected int height = 100;
	protected Color color = Color.MAGENTA;
	protected boolean isMobile = true;
	protected int type = 0;
	public int getType() {
		return type;
	}

	protected boolean isGrounded = false;
	
	public void draw(Graphics g, double heightFactor, double widthFactor) {
		int x = (int)(getPosition().x*widthFactor);
		int y = (int)(getPosition().y*heightFactor);
		g.setColor(color);
		g.fillRect(x, y, (int) (width * widthFactor), (int) (height * heightFactor));
	}
	
	public void zeroVerticalVelocity() {
		velocity.setLocation(velocity.getX(), 0.0);
	}
	public boolean isAffectedByGravity(){
		return false;
	}
	
	public boolean canCollideWith(Entity e){
		//collide if I am a projectile and they aren't
		return ((e.type & Constants.PROJECTILE_TYPE) == 0)
			&& ((type & Constants.PROJECTILE_TYPE) != 0);
	}
	
	public void addUpRightVelocity(int x, int y){
		if(isMobile){
			this.getVelocity().x += x;
			this.getVelocity().y -= y;
		}
	}
	public void applyFriction() {
		if((type & Constants.MAGE_TYPE) != 0){
			if(Math.abs(this.getVelocity().x) > .4){
				this.getVelocity().x -= .5*Math.signum(this.getVelocity().x);
			}
			else{
				this.getVelocity().x = 0;
			}
			int maxXVelocity = 15;
			if(this.getVelocity().x > maxXVelocity){
				this.getVelocity().x = maxXVelocity;
			}
			if(this.getVelocity().x < -1*maxXVelocity){
				this.getVelocity().x = -1*maxXVelocity;
			}
		}
	}
	
	public void applyGravity(){
		if(isAffectedByGravity()){
			this.getVelocity().y += 2;
		}
	}
	
	public Rectangle2D.Double getHitbox(){
		return new Rectangle2D.Double(getPosition().x, getPosition().y, width, height);
	}

	public void updatePosition(ArrayList<Entity> entities, ArrayList<Entity> toBeRemoved) {
		if(isMobile){
			this.position.x += this.velocity.x;
			this.position.y += this.velocity.y;
			if (velocity.y < 0) isGrounded = false;
			for(Entity e : entities){
				if(((type & Constants.MAGE_TYPE) != 0)){
					//Stop the object on top of immobile objects
					if(((e.type & Constants.BLOCK_TYPE) != 0) && overlapsTheTopOf(e)){
						this.getVelocity().y = 0;
						this.getPosition().y = e.getPosition().y - this.height + 1;
						isGrounded = true;
					}
					
					//Stop the object on hitting ceiling
					if(((e.type & Constants.BLOCK_TYPE) != 0) && overlapsTheBottomOf(e)){
						this.getVelocity().y = 0;
						this.getPosition().y = e.getPosition().y + e.height + 1;
					}
					
					//stop when hitting a wall going right
					if(((e.type & Constants.BLOCK_TYPE) != 0) && overlapsLeftOf(e)){
						this.getVelocity().x = 0;
						this.getPosition().x = e.getPosition().x - this.width - 1;
					}
					
					//stop when hitting a wall going left
					if(((e.type & Constants.BLOCK_TYPE) != 0) && overlapsRightOf(e)){
						this.getVelocity().x = 0;
						this.getPosition().x = e.getPosition().x + e.width + 1;
					}
				}
				else if(((e.type & Constants.BLOCK_TYPE) != 0) && getHitbox().intersects(e.getHitbox())){
					/*
					 * insert code here for projectile hits wall
					 */
					toBeRemoved.add(this);
				}
			}
		}
	}



	public void applyHits(ArrayList<Entity> entities, ArrayList<Entity> toBeRemoved) {
		//for each projectile, check if it should activate
		if(((type & Constants.PROJECTILE_TYPE) != 0)){
			for(Entity target : entities){
				if(canCollideWith(target) && getHitbox().intersects(target.getHitbox())){
					/*
					 * insert collision damage and effect code here
					 */
					System.out.println("COLLISION! DELETING PROJECTILE!");
					toBeRemoved.add(this);
				}
			}
		}
	}
	
	//Ignore how ugly my overlap code is!
	private boolean overlapsTheBottomOf(Entity wall) {
		//and object bottom is above my bottom
		//if object bottom is under my top
		//and its left is left of my right
		//and its right is right of my left
		int myTop = getPosition().y;
		int myBottom = getPosition().y + height;
		int myRight = getPosition().x + width;
		int myLeft = getPosition().x;
		int wallTop = wall.getPosition().y;
		int wallBottom = wall.getPosition().y + wall.height;
		int wallRight = wall.getPosition().x + wall.width;
		int wallLeft = wall.getPosition().x;
		if(		wallBottom < myBottom				
				&& wallBottom > myTop	
				&& wallLeft < myRight	
				&& wallRight > myLeft){
			return true;
		}
		
		return false;
	}

	private boolean overlapsRightOf(Entity wall) {
		//if object top is above my bottom
		//and object bottom is below my top
		//and its right is right of my left
		//and its right is left of my right
		int myTop = getPosition().y;
		int myBottom = getPosition().y + height;
		int myRight = getPosition().x + width;
		int myLeft = getPosition().x;
		int wallTop = wall.getPosition().y;
		int wallBottom = wall.getPosition().y + wall.height;
		int wallRight = wall.getPosition().x + wall.width;
		int wallLeft = wall.getPosition().x;
		if(		wallTop < myBottom				
				&& wallBottom > myTop	
				&& wallRight < myRight	
				&& wallRight > myLeft){
			return true;
		}
		return false;
	}

	private boolean overlapsLeftOf(Entity wall) {
		//if object top is above my bottom
		//and object bottom is below my top
		//and its left is left of my right
		//and its left is right of my left
		int myTop = getPosition().y;
		int myBottom = getPosition().y + height;
		int myRight = getPosition().x + width;
		int myLeft = getPosition().x;
		int wallTop = wall.getPosition().y;
		int wallBottom = wall.getPosition().y + wall.height;
		int wallRight = wall.getPosition().x + wall.width;
		int wallLeft = wall.getPosition().x;
		if(		wallTop < myBottom				
				&& wallBottom > myTop	
				&& wallLeft < myRight	
				&& wallLeft > myLeft){
			return true;
		}
		return false;
	}

	private boolean overlapsTheTopOf(Entity wall) {
		//if object top is under my top
		//and object top is above my bottom
		//and its left is left of my right
		//and its right is right of my left
		int myTop = getPosition().y;
		int myBottom = getPosition().y + height;
		int myRight = getPosition().x + width;
		int myLeft = getPosition().x;
		int wallTop = wall.getPosition().y;
		int wallBottom = wall.getPosition().y + wall.height;
		int wallRight = wall.getPosition().x + wall.width;
		int wallLeft = wall.getPosition().x;
		if(		wallTop > myTop				
				&& wallTop < myBottom	
				&& wallLeft < myRight	
				&& wallRight > myLeft){
			return true;
		}
		
		return false;
	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public Point2D.Double getVelocity() {
		return velocity;
	}

	public void setVelocity(Point2D.Double velocity) {
		this.velocity = velocity;
	}
}

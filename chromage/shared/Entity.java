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
    protected Point position = new Point(2000,2000);
	protected Point2D.Double velocity = new Point2D.Double(0,0);

	protected int width = 100;
	protected int height = 100;
	protected Color color = Color.MAGENTA;
	protected boolean isMobile = true;
	protected int type = 0;
	
	public void draw(Graphics g, double heightFactor, double widthFactor) {
		int x = (int)(position.x*widthFactor);
		int y = (int)(position.y*heightFactor);
		g.setColor(color);
		g.fillRect(x, y, (int) (width * widthFactor), (int) (height * heightFactor));
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
			this.velocity.x += x;
			this.velocity.y -= y;
		}
		if((type & Constants.MAGE_TYPE) != 0){
			if(Math.abs(this.velocity.x) > .4){
				this.velocity.x -= .5*Math.signum(this.velocity.x);
			}
			else{
				this.velocity.x = 0;
			}
		}
	}
	
	public void applyGravity(){
		if(isAffectedByGravity()){
			this.velocity.y += .03;
		}
	}
	
	protected Rectangle2D.Double getHitbox(){
		return new Rectangle2D.Double(position.x, position.y, width, height);
	}

	public void updatePosition(ArrayList<Entity> entities) {
		if(isMobile){
			System.out.println("Dropping from " + this.position.y + " at rate " + this.velocity.y);
			this.position.x += this.velocity.x;
			this.position.y += this.velocity.y;
			for(Entity e : entities){
				//Stop the object on top of immobile objects
				if(((e.type & Constants.BLOCK_TYPE) != 0) && overlapsTheTopOf(e)){
					this.velocity.y = 0;
					this.position.y = e.position.y - this.height;
				}
				
				//Stop the object on hitting ceiling
				if(((e.type & Constants.BLOCK_TYPE) != 0) && overlapsTheBottomOf(e)){
					this.velocity.y = 0;
					this.position.y = e.position.y + e.height + 1;
				}
				
				//stop when hitting a wall going right
				if(((e.type & Constants.BLOCK_TYPE) != 0) && overlapsLeftOf(e)){
					this.velocity.x = 0;
					this.position.x = e.position.x - this.width - 1;
				}
				
				//stop when hitting a wall going left
				if(((e.type & Constants.BLOCK_TYPE) != 0) && overlapsRightOf(e)){
					this.velocity.x = 0;
					this.position.x = e.position.x + e.width + 1;
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
		int myTop = position.y;
		int myBottom = position.y + height;
		int myRight = position.x + width;
		int myLeft = position.x;
		int wallTop = wall.position.y;
		int wallBottom = wall.position.y + wall.height;
		int wallRight = wall.position.x + wall.width;
		int wallLeft = wall.position.x;
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
		int myTop = position.y;
		int myBottom = position.y + height;
		int myRight = position.x + width;
		int myLeft = position.x;
		int wallTop = wall.position.y;
		int wallBottom = wall.position.y + wall.height;
		int wallRight = wall.position.x + wall.width;
		int wallLeft = wall.position.x;
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
		int myTop = position.y;
		int myBottom = position.y + height;
		int myRight = position.x + width;
		int myLeft = position.x;
		int wallTop = wall.position.y;
		int wallBottom = wall.position.y + wall.height;
		int wallRight = wall.position.x + wall.width;
		int wallLeft = wall.position.x;
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
		int myTop = position.y;
		int myBottom = position.y + height;
		int myRight = position.x + width;
		int myLeft = position.x;
		int wallTop = wall.position.y;
		int wallBottom = wall.position.y + wall.height;
		int wallRight = wall.position.x + wall.width;
		int wallLeft = wall.position.x;
		if(		wallTop > myTop				
				&& wallTop < myBottom	
				&& wallLeft < myRight	
				&& wallRight > myLeft){
			return true;
		}
		
		return false;
	}
}

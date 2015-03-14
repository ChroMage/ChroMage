package chromage.shared;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.Serializable;

public class Entity implements Serializable {
    static final long serialVersionUID = -50077493051991117L;
    protected Point position = new Point(2000,2000);
	protected Point2D.Double velocity = new Point2D.Double(0,0);

	protected int width = 100;
	protected int height = 100;
	protected Color color = Color.MAGENTA;
	protected boolean canBeMoved = true;
	
	public void draw(Graphics g, double heightFactor, double widthFactor) {
		int x = (int)(position.x*widthFactor);
		int y = (int)(position.y*heightFactor);
		g.setColor(color);
		g.fillRect(x, y, (int) (width * widthFactor), (int) (height * heightFactor));
	}
	
	public boolean isAffectedByGravity(){
		return false;
	}
	
	public void addUpRightVelocity(int x, int y){
		if(canBeMoved){
			this.velocity.x += x;
			this.velocity.y -= y;
		}
	}
	
	public void applyGravity(){
		if(isAffectedByGravity()){
			this.velocity.y += .01;
		}
	}

	public void updatePosition() {
		if(canBeMoved){
			System.out.println("Dropping " + this.position.y + " at rate " + this.velocity.y);
			this.position.x += this.velocity.x;
			this.position.y += this.velocity.y;
		}
	}
}

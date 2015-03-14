package chromage.shared;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;

public class Mage extends Entity implements Serializable {
    static final long serialVersionUID = -50077493051991117L;

	public static final int DEFAULT_HEIGHT = 100;
	public static final int DEFAULT_WIDTH = 200;
	
	//added
	private boolean secondJump = false;
	private boolean firstJump = false;
	
	public Mage(Color color){
		this(2000,2000, DEFAULT_WIDTH, DEFAULT_HEIGHT, color);
	}

	public Mage(int x, int y, int width, int height, Color color){
		this.position = new Point(x, y);
		this.velocity = new Point2D.Double(0, 0);
		this.width = width;
		this.height = height;
		this.color = color;
		type = Constants.MAGE_TYPE;
	}
	
	public void setVelocityWithInput(UserInput input) {
		int x = 0, y = 0;
		if(isGrounded) {
			firstJump = true;
			secondJump = false;
		}
		switch (input.horizontalDirection) {
			case LEFT: x = -1; break;
            case NONE: x = 0; break;
			case RIGHT: x = 1; break;
		}
		switch (input.verticalDirection) {
			case JUMP:
				if(isGrounded) {
					y = 40;
				}
				else if(!isGrounded && !firstJump) {
					zeroVerticalVelocity();
					y = 40;
					secondJump = true;
					firstJump = true;
				}
			break;
			case NONE: 
			y = 0;
			if(!isGrounded && !secondJump) {
				firstJump = false;
			}
			break;

		}
		velocity.setLocation(velocity.getX() + x, velocity.getY() - y);
	}
	
	public boolean isAffectedByGravity(){
		return true;
	}
}

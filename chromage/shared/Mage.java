package chromage.shared;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;

import chromage.server.PlayerThread;

public class Mage extends Entity implements Serializable {
    static final long serialVersionUID = -50077493051991117L;

	//added
	private boolean secondJump = false;
	private boolean firstJump = false;

	public static final int DEFAULT_HEIGHT = 300;
	public static final int DEFAULT_WIDTH = 100;
	public static final int MAX_HP = 500;

	private int coolDown = 0;
	public int hp = MAX_HP;
	public int mana = 300;
	public Spell leftSpell;
	public Spell middleSpell;
	public Spell rightSpell;
	public boolean isDead() {
		return hp == 0;
	}

	public void takeDamage(int dmg) {
		hp -= dmg;
		if(hp <= 0) {
			hp = 0;
		}
	}
	public Mage(Color color){
		this(2000,2000, DEFAULT_WIDTH, DEFAULT_HEIGHT, color);
	}

	public Mage(int x, int y, int width, int height, Color color){
		this.setPosition(new Point(x, y));
		this.setVelocity(new Point2D.Double(0, 0));
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

	public void decrementCooldown() {
		if(coolDown > 0){
			coolDown--;
		}
	}
	
	public int getCoolDown(){
		return coolDown;
	}

	public void setCoolDown(int i) {
		coolDown = i;
	}

	public void castSpell(UserInput input, GameState state) {
		if(getCoolDown() <= 0){
			if (input.spell.equals(SpellInput.LEFT)){ 
				state.entities.add(leftSpell.createProjectile(this, input.mouseLocation));
			}
			else if (input.spell.equals(SpellInput.RIGHT)){ 
				state.entities.add(rightSpell.createProjectile(this, input.mouseLocation));
			}
			else if (input.spell.equals(SpellInput.MIDDLE)){
				Projectile middle = middleSpell.createProjectile(this, input.mouseLocation);
				if(middle == null) {
					Rectangle2D.Double newHitBox = new Rectangle2D.Double(input.mouseLocation.getX(), input.mouseLocation.getY(), getWidth(), getHeight());
					boolean canBlink = true;
					for(Entity e: state.entities){
						if(((e.getType() & Constants.BLOCK_TYPE) != 0) && newHitBox.intersects(e.getHitbox())){
							canBlink = false;
						}
					}
					if(canBlink){
						setPosition(new Point((int)input.mouseLocation.getX(), (int)input.mouseLocation.getY()));
						setCoolDown(60);
					}
				}
				else {
					state.entities.add(middle);
				}
			}
		}
		else{
			decrementCooldown();
		}
	}


	public void draw(Graphics g, double heightFactor, double widthFactor) {
		int x = (int)(getPosition().x*widthFactor);
		int y = (int)(getPosition().y*heightFactor);
		int scaledWidth = (int) (width * widthFactor);
		int scaledHeight = (int) (height * heightFactor);
		g.setColor(color);
		g.fillRect(x, y, scaledWidth, scaledHeight);
		g.setColor(Color.white);
		g.fillRect(x, y - 5, scaledWidth, 5);
		g.setColor(Color.green);
		g.fillRect(x, y - 25, scaledWidth * hp / MAX_HP, 10);

	}
}

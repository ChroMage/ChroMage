package chromage.shared;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Blink extends Spell {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int getStun() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getInvuln() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCast() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSlow() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCoolDown() {
		// TODO Auto-generated method stub
		return 60;
	}

	@Override
	public int getManaCost() {
		// TODO Auto-generated method stub
		return 50;
	}

	@Override
	public int getDamage() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Color getColor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAffectedByGravity() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public Projectile createProjectile(Mage mage, Point2D.Double target, GameState state) {
		Rectangle2D.Double newHitBox = new Rectangle2D.Double(target.x, target.y, mage.getWidth(), mage.getHeight());
		boolean canBlink = true;
		for(Entity e: state.entities){
			if(((e.getType() & Constants.BLOCK_TYPE) != 0) && newHitBox.intersects(e.getHitbox())){
				canBlink = false;
			}
		}
		if(canBlink){
			mage.setPosition(new Point((int)target.x, (int)target.y));
		}
		return null;
	}

}

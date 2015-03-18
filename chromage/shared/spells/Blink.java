package chromage.shared.spells;

import chromage.shared.Mage;
import chromage.shared.engine.Projectile;
import chromage.shared.engine.Entity;
import chromage.shared.engine.GameState;
import chromage.shared.utils.Constants;

import java.awt.Color;
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
		return 30;
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
	public double getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getHeight() {
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
		Rectangle2D.Double newHitBox = new Rectangle2D.Double(target.x, target.y, 1, 1);
		boolean canBlink = true;
		for(Entity e: state.entities){
			if(((e.getType() & Constants.BLOCK_TYPE) != 0) && newHitBox.intersects(e.getHitbox())){
				canBlink = false;
			}
		}
		if (canBlink) {
			mage.setPosition(target);
		}
		return null;
	}

	@Override
	public int getKnockup() {
		// TODO Auto-generated method stub
		return 0;
	}

}

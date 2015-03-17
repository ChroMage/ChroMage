package chromage.shared;

import java.awt.Color;
import java.awt.geom.Point2D;

public class Fireball extends Spell {

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
		return 100;
	}

	@Override
	public int getCoolDown() {
		// TODO Auto-generated method stub
		return 15;
	}

	@Override
	public int getManaCost() {
		// TODO Auto-generated method stub
		return 30;
	}

	@Override
	public int getDamage() {
		// TODO Auto-generated method stub
		return 30;
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return 70;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 70;
	}

	@Override
	public int getSpeed() {
		return 45;
	}

	@Override
	public Color getColor() {
		// TODO Auto-generated method stub
		return Color.ORANGE;
	}

	@Override
	public boolean isAffectedByGravity() {
		// TODO Auto-generated method stub
		return true;
	}

}

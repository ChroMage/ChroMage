package chromage.shared;

import java.awt.Color;

public class Iceball extends Spell {

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
		return 40;
	}

	@Override
	public int getDamage() {
		// TODO Auto-generated method stub
		return 6;
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
		return 19;
	}

	@Override
	public Color getColor() {
		// TODO Auto-generated method stub
		return Color.BLUE;
	}

	@Override
	public boolean isAffectedByGravity() {
		// TODO Auto-generated method stub
		return false;
	}

}

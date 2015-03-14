package chromage.shared;

import java.awt.Color;

public class Lightning extends Spell {

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
		return 90;
	}

	@Override
	public int getManaCost() {
		// TODO Auto-generated method stub
		return 80;
	}

	@Override
	public int getDamage() {
		// TODO Auto-generated method stub
		return 70;
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return 150;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 150;
	}

	@Override
	public int getSpeed() {
		return 100;
	}

	@Override
	public Color getColor() {
		// TODO Auto-generated method stub
		return Color.YELLOW;
	}

	@Override
	public boolean isAffectedByGravity() {
		// TODO Auto-generated method stub
		return false;
	}

}

package chromage.shared.spells;
import java.awt.Color;

public class Fireball extends Spell {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	public int getStun() {
		return 0;
	}
	@Override
	public int getInvuln() {
		return 0;
	}
	@Override
	public int getCast() {
		return 0;
	}
	@Override
	public int getSlow() {
		return 100;
	}
	@Override
	public int getCoolDown() {
		return 15;
	}
	@Override
	public int getManaCost() {
		return 30;
	}
	@Override
	public int getDamage() {
		return 40;
	}
	@Override
	public double getWidth() {
		return 70;
	}
	@Override
	public double getHeight() {
		return 70;
	}
	@Override
	public int getSpeed() {
		return 45;
	}
	@Override
	public Color getColor() {
		return Color.ORANGE;
	}
	@Override
	public boolean isAffectedByGravity() {
		return true;
	}
	@Override
	public int getKnockup() {
		return 40;
	}
}

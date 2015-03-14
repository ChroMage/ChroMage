package chromage.shared;

import java.awt.Color;
import java.awt.geom.Point2D;

public class FruitPunch extends Spell {

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
		return 15;
	}

	@Override
	public int getSlow() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCoolDown() {
		// TODO Auto-generated method stub
		return 240;
	}

	@Override
	public int getManaCost() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDamage() {
		// TODO Auto-generated method stub
		return 40;
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return 100;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 100;
	}

	@Override
	public int getSpeed() {
		// TODO Auto-generated method stub
		return 20;
	}

	@Override
	public Color getColor() {
		// TODO Auto-generated method stub
		return Color.RED;
	}

	@Override
	public boolean isAffectedByGravity() {
		// TODO Auto-generated method stub
		return false;
	}

	public Projectile createProjectile(Mage mage, Point2D.Double target, GameState state) {
		Point2D.Double direction = new Point2D.Double(target.getX() - mage.getPosition().getX(), target.getY() - mage.getPosition().getY());
		int x = (int) (mage.getPosition().getX() + direction.getX()/direction.distance(0, 0)*mage.getHeight());
		int y = (int) (mage.getPosition().getY() + direction.getY()/direction.distance(0, 0)*mage.getHeight());
		Projectile p = new Projectile(x, y, 
				0, -50,
				        getWidth(), getHeight(), 
				        getDamage(), getSlow(), getColor());
		p.isGravitated = isAffectedByGravity();
		return p;
	}
}

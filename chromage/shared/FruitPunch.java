package chromage.shared;

import java.awt.Color;
import java.awt.geom.Point2D;

public class FruitPunch extends Spell {

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

		return 15;
	}

	@Override
	public int getSlow() {

		return 40;
	}

	@Override
	public int getCoolDown() {

		return 25;
	}

	@Override
	public int getManaCost() {

		return 0;
	}

	@Override
	public int getDamage() {

		return 100;
	}

	@Override
	public double getWidth() {

		return 100;
	}

	@Override
	public double getHeight() {

		return 100;
	}

	@Override
	public int getSpeed() {

		return 50;
	}

	@Override
	public Color getColor() {

		return Color.RED;
	}

	@Override
	public boolean isAffectedByGravity() {

		return false;
	}

	public Projectile createProjectile(Mage mage, Point2D.Double target, GameState state) {
		Point2D.Double direction = new Point2D.Double(target.getX() - mage.getPosition().getX(), target.getY() - mage.getPosition().getY());

        Point2D.Double startPosition =  getProjectileStartPosition(mage, direction);
        Projectile p = new Projectile(startPosition, getWidth(), getHeight(), new Point2D.Double(0, -getSpeed()),
                getDamage(), getSlow(), getKnockup(), getColor(), mage);
		p.isGravitated = isAffectedByGravity();
		return p;
	}

	@Override
	public int getKnockup() {
		return 100;
	}
}

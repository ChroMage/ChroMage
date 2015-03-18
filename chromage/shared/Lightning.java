package chromage.shared;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

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
		return 120;
	}

	@Override
	public int getManaCost() {
		// TODO Auto-generated method stub
		return 80;
	}

	@Override
	public int getDamage() {
		// TODO Auto-generated method stub
		return 100;
	}

	@Override
	public double getWidth() {
		// TODO Auto-generated method stub
		return 50;
	}

	@Override
	public double getHeight() {
		// TODO Auto-generated method stub
		return 50;
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
	
	public Projectile createProjectile(Mage mage, Point2D.Double target, GameState state) {
		Point2D.Double direction = new Point2D.Double(target.getX() - mage.getPosition().getX(), target.getY() - mage.getPosition().getY());
        Point2D.Double startPosition =  getProjectileStartPosition(mage, direction);
        Projectile p = new Projectile(startPosition, getWidth(), getHeight(), Utilities.scaleTo(direction, getSpeed()),
                getDamage(), getSlow(), getKnockup(), getColor(), mage) {
			private static final long serialVersionUID = 188689086533652783L;
			public void update(ArrayList<Entity> e) {
				super.update(e);
                setWidth(getWidth() + 3);
                setHeight(getHeight() + 3);
			}
		};
		p.isGravitated = isAffectedByGravity();
		return p;
	}

	@Override
	public int getKnockup() {
		// TODO Auto-generated method stub
		return 80;
	}

}

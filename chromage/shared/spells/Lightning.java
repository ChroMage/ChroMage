package chromage.shared.spells;

import chromage.shared.Mage;
import chromage.shared.engine.Projectile;
import chromage.shared.engine.Entity;
import chromage.shared.engine.GameState;
import chromage.shared.utils.Utilities;

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
		return 30;
	}

	@Override
	public int getManaCost() {
		// TODO Auto-generated method stub
		return 240;
	}

	@Override
	public int getDamage() {
		// TODO Auto-generated method stub
		return 120;
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
	
	public ArrayList<Projectile> createProjectile(Mage mage, Point2D.Double target, GameState state) {
		Point2D.Double direction = new Point2D.Double(target.getX() - mage.getCenter().getX(), target.getY() - mage.getCenter().getY());
        Point2D.Double startPosition =  getProjectileStartPosition(mage, direction);
        Projectile p = new Projectile(startPosition, getWidth(), getHeight(), Utilities.scaleTo(direction, getSpeed()),
                getDamage(), getSlow(), getKnockup(), getColor(), mage, isAffectedByGravity()) {
			private static final long serialVersionUID = 188689086533652783L;
			public ArrayList<? extends Entity> update(ArrayList<Entity> e) {
				super.update(e);
                setWidth(getWidth() + 3);
                setHeight(getHeight() + 3);
                return new ArrayList<Entity>();
			}
		};
        ArrayList<Projectile> a = new ArrayList<Projectile>();
        a.add(p);
		return a;
	}

	@Override
	public int getKnockup() {
		// TODO Auto-generated method stub
		return 80;
	}

}

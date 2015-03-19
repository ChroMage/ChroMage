package chromage.shared.spells;

import chromage.shared.Mage;
import chromage.shared.engine.Projectile;
import chromage.shared.engine.Entity;
import chromage.shared.engine.GameState;
import chromage.shared.utils.Utilities;

import java.awt.Color;
import java.awt.geom.Point2D;

public class Lifesteal extends Spell {

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
		return 90;
	}

	@Override
	public int getCoolDown() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public int getManaCost() {
		// TODO Auto-generated method stub
		return 15;
	}

	@Override
	public int getDamage() {
		// TODO Auto-generated method stub
		return 2;
	}
	
	public int getHeal() {
		return 1;
	}

	@Override
	public double getWidth() {
		// TODO Auto-generated method stub
		return 70;
	}

	@Override
	public double getHeight() {
		// TODO Auto-generated method stub
		return 70;
	}

	@Override
	public int getSpeed() {
		// TODO Auto-generated method stub
		return 50;
	}

	@Override
	public Color getColor() {
		// TODO Auto-generated method stub
		return Color.GREEN;
	}

	@Override
	public int getKnockup() {
		// TODO Auto-generated method stub
		return 10;
	}

	@Override
	public boolean isAffectedByGravity() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public Projectile createProjectile(Mage mage, Point2D.Double target, GameState state) {
		Point2D.Double direction = new Point2D.Double(target.getX() - mage.getCenter().getX(), target.getY() - mage.getCenter().getY());
        Point2D.Double startPosition =  getProjectileStartPosition(mage, direction);
		Projectile p = new Projectile(startPosition, getWidth(), getHeight(), Utilities.scaleTo(direction, getSpeed()),
                getDamage(), getSlow(), getKnockup(), getColor(), mage, isAffectedByGravity()) {
			private static final long serialVersionUID = 188689086533652783L;
			public void hitTarget(Entity target){
				super.hitTarget(target);
				if (target instanceof Mage){
					getOwner().healDamage(getHeal());
				}
			}
		};
		return p;
	}
}

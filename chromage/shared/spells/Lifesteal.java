package chromage.shared.spells;

import chromage.shared.Mage;
import chromage.shared.engine.Projectile;
import chromage.shared.engine.Entity;
import chromage.shared.utils.Utilities;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

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
		return 2;
	}

	@Override
	public int getDamage() {
		// TODO Auto-generated method stub
		return 1;
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
	public boolean isAffectedByGravity() {
		// TODO Auto-generated method stub
		return false;
	}

    @Override
	public ArrayList<Projectile> createProjectiles(Mage mage, Point2D.Double target, ArrayList<Entity> entities) {
		Point2D.Double direction = new Point2D.Double(target.getX() - mage.getPosition().getX(), target.getY() - mage.getPosition().getY());
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
        ArrayList<Projectile> a = new ArrayList<Projectile>();
        a.add(p);
		return a;
	}

	@Override
	public int getKnockup() {
		// TODO Auto-generated method stub
		return 6;
	}
}

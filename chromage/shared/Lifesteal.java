package chromage.shared;

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
		return 3;
	}
	
	public int getHeal() {
		return 1;
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
	
	public Projectile createProjectile(Mage mage, Point2D.Double target, GameState state) {
		Point2D.Double direction = new Point2D.Double(target.getX() - mage.getPosition().getX(), target.getY() - mage.getPosition().getY());
		int x = (int) (mage.getPosition().getX() + direction.getX()/direction.distance(0, 0)*mage.getHeight());
		int y = (int) (mage.getPosition().getY() + direction.getY()/direction.distance(0, 0)*mage.getHeight());
		Projectile p = new Projectile(x, y, 
						(int)(direction.x/direction.distance(0, 0)*getSpeed()), (int)(direction.y/direction.distance(0,0)*getSpeed()),
				        getWidth(), getHeight(), 
				        getDamage(), getSlow(), getKnockup(), getColor(), mage) {
			private static final long serialVersionUID = 188689086533652783L;
			public void hitTarget(Entity target){
				super.hitTarget(target);
				if(target instanceof Mage){
					owner.hp += getHeal();
					owner.hp = Math.min(owner.hp, Mage.MAX_HP);
				}
			}
		};
		p.isGravitated = isAffectedByGravity();
		return p;
	}

	@Override
	public int getKnockup() {
		// TODO Auto-generated method stub
		return 6;
	}
}

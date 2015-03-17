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
		return 90;
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
	
	public Projectile createProjectile(Mage mage, Point2D.Double target, GameState state) {
		Point2D.Double direction = new Point2D.Double(target.getX() - mage.getPosition().getX(), target.getY() - mage.getPosition().getY());
		int x = (int) (mage.getPosition().getX() + direction.getX()/direction.distance(0, 0)*mage.getHeight());
		int y = (int) (mage.getPosition().getY() + direction.getY()/direction.distance(0, 0)*mage.getHeight());
		Projectile p = new Projectile(x, y, 
						(int)(direction.x/direction.distance(0, 0)*getSpeed()), (int)(direction.y/direction.distance(0,0)*getSpeed()),
				        getWidth(), getHeight(), 
				        getDamage(), getSlow(), getColor(), mage) {
			private static final long serialVersionUID = 188689086533652783L;
			public void update(ArrayList<Entity> e) {
				super.update(e);
				width += 10;
				height += 10;
			}
		};
		p.isGravitated = isAffectedByGravity();
		return p;
	}

}

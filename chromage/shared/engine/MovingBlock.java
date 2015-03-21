package chromage.shared.engine;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Collection;

import chromage.shared.utils.Constants;

public class MovingBlock extends Block {
	static final long serialVersionUID = -50077493051991117L;
	static final double VELOCITY_SCALE = .0002;
	Point2D.Double velocity = new Point2D.Double(0,0);
	Point2D.Double centerPosition = new Point2D.Double(0,0);
	int xMovementFromOrigin;
	int yMovementFromOrigin;

	public MovingBlock(int x, int y, int width, int height, int xMovementFromOrigin,  int yMovementFromOrigin) {
        super(x - xMovementFromOrigin, y - yMovementFromOrigin, width, height);
        centerPosition = new Point2D.Double(x,y);
        this.xMovementFromOrigin = xMovementFromOrigin;
        this.yMovementFromOrigin = yMovementFromOrigin;
    }
	
	/**
     * Update this block for this tick.
     *
     * @param entities the entities in the battlefield
     * @return any entities created by this one during this tick
     */
    public Collection<? extends Entity> update(Collection<Entity> entities) {
    	velocity.x += (centerPosition.x - getPosition().x) * VELOCITY_SCALE;
    	velocity.y += (centerPosition.y - getPosition().y) * VELOCITY_SCALE;
    	setPosition(getPosition().x + velocity.x, getPosition().y + velocity.y);
        return new ArrayList<Entity>();
    }

}

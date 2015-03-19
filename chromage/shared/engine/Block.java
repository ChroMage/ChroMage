package chromage.shared.engine;

import chromage.shared.utils.Constants;

import java.awt.*;
import java.io.Serializable;

/**
 * Represents a fixed entity on the map, like the floor or walls.
 */
public class Block extends Entity implements Serializable {
    static final long serialVersionUID = -50077493051991117L;

    public Block(int x, int y, int width, int height) {
        setPosition(x, y);
        setWidth(width);
        setHeight(height);
        this.color = Color.GRAY;
        collisionBitMask = 0;
        categoryBitMask = Constants.BLOCK_TYPE;
    }

    public void acceptCollisionFrom(CollisionProcessor p) {
        p.processCollision(this);
    }
}

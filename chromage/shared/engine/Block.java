package chromage.shared.engine;

import chromage.shared.engine.Entity;
import chromage.shared.utils.Constants;

import java.awt.Color;
import java.io.Serializable;

public class Block extends Entity implements Serializable {
    static final long serialVersionUID = -50077493051991117L;
	public Block(int x, int y, int width, int height){
		setPosition(x, y);
		setWidth(width);
		setHeight(height);
		this.color = Color.GRAY;
		isMobile = false;
        collisionBitMask = 0;
        categoryBitMask = Constants.BLOCK_TYPE;
	}
}

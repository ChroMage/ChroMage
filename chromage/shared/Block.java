package chromage.shared;

import java.awt.Color;
import java.awt.Point;
import java.io.Serializable;

public class Block extends Entity implements Serializable {
    static final long serialVersionUID = -50077493051991117L;
	public Block(int x, int y, int width, int height){
		setPosition(x, y);
		setWidth(width);
		setHeight(height);
		this.color = Color.GRAY;
		isMobile = false;
		type = Constants.BLOCK_TYPE;
	}
}

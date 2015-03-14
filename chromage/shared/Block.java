package chromage.shared;

import java.awt.Color;
import java.awt.Point;

public class Block extends Entity{
	public Block(int x, int y, int width, int height){
		this.position = new Point(x, y);
		this.width = width;
		this.height = height;
		this.color = Color.GRAY;
	}
}

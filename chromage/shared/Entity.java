package chromage.shared;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.Serializable;

public class Entity implements Serializable {
    static final long serialVersionUID = -50077493051991117L;
	Point position = new Point(2000,2000);
	int width = 100;
	int height = 100;
	Color color = Color.MAGENTA;
	
	public void draw(Graphics g, double heightFactor, double widthFactor) {
		int x = (int)(position.x*widthFactor);
		int y = (int)(position.y*heightFactor);
		g.setColor(color);
		g.fillRect(x, y, (int) (width * widthFactor), (int) (height * heightFactor));
		//g.setColor(Color.BLACK);
		//g.fillOval(x, y, (int) (100 * widthFactor), (int) (100 * heightFactor));
	}
}

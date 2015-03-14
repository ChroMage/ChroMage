package chromage.shared;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.Serializable;

public class Projectile extends Entity implements Serializable {
    static final long serialVersionUID = -50077493051991117L;
	boolean isGravitated = false;
	
	public Projectile(int x, int y, int width, int height){
		this.position = new Point(x, y);
		this.velocity = new Point2D.Double(0, 0);
		this.width = width;
		this.height = height;
		this.color = Color.GRAY;
	}
}

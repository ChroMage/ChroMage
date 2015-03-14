package chromage.shared;

import java.awt.Color;
import java.awt.Point;
import java.io.Serializable;

public class Entity implements Serializable {
    static final long serialVersionUID = -50077493051991117L;
	Point position = new Point(2000,2000);
	int width = 100;
	int height = 100;
	Color color = Color.MAGENTA;
}

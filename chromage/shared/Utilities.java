package chromage.shared;

import java.awt.geom.Point2D;

/**
 * Created by ahruss on 3/18/15.
 */
public class Utilities {

    public static final Point2D.Double normalize(Point2D.Double p) {
        return scaleTo(p, 1);
    }

    public static final Point2D.Double subtract(Point2D.Double p1, Point2D.Double p2) {
        return new Point2D.Double(p1.x - p2.x, p1.y - p2.y);
    }

    public static final Point2D.Double scaleTo(Point2D.Double p, double newLength) {
        double scaleFactor = newLength / p.distance(0,0);
        return new Point2D.Double(p.x * scaleFactor, p.y * scaleFactor);
    }

    public static final Point2D.Double add(Point2D.Double p, Point2D.Double v) {
        return new Point2D.Double(p.x + v.x, p.y + v.y);
    }

    public static final Point2D.Double addAll(Point2D.Double... points) {
        Point2D.Double sum = new Point2D.Double();
        for (Point2D.Double p : points) {
            sum = add(sum, p);
        }
        return sum;
    }
}

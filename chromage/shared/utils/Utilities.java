package chromage.shared.utils;

import java.awt.geom.Area;
import java.awt.geom.Point2D;

/**
 * A class to contain various utility functions
 */
public class Utilities {

    /**
     * Scale the given vector such that its length is 1
     * @param p the vector to scale
     * @return  the scaled vector
     */
    public static final Point2D.Double normalize(Point2D.Double p) {
        return scaleTo(p, 1);
    }

    /**
     * @return the difference between the two given vectors
     */
    public static final Point2D.Double subtract(Point2D.Double p1, Point2D.Double p2) {
        return new Point2D.Double(p1.x - p2.x, p1.y - p2.y);
    }

    /**
     * @return the vector, scaled such that its length becomes the given length
     */
    public static final Point2D.Double scaleTo(Point2D.Double p, double newLength) {
        double scaleFactor = newLength / length(p);
        return new Point2D.Double(p.x * scaleFactor, p.y * scaleFactor);
    }

    /**
     * @return the sum of the two vectors
     */
    public static final Point2D.Double add(Point2D.Double p, Point2D.Double v) {
        return new Point2D.Double(p.x + v.x, p.y + v.y);
    }

    /**
     * @return the sum of all the arguments
     */
    public static final Point2D.Double addAll(Point2D.Double... points) {
        Point2D.Double sum = new Point2D.Double();
        for (Point2D.Double p : points) {
            sum = add(sum, p);
        }
        return sum;
    }

    /**
     * @return true iff a and b intersect
     */
    public static boolean areasIntersect(Area a, Area b) {
        Area intersection = new Area(a);
        intersection.intersect(b);
        return !intersection.isEmpty();
    }

    /**
     * @return  a vector of the same length as v pointing in the opposite direction
     */
    public static Point2D.Double invert(Point2D.Double v) {
        return new Point2D.Double(-v.x, -v.y);
    }

    /**
     * @return the length of the given vector
     */
    public static double length(Point2D.Double v) {
        return v.distance(0, 0);
    }
}

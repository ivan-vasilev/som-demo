package bg.tu_sofia.graduiation.robot.motion.som;

import java.awt.geom.Rectangle2D;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;

public class Bounds2DFilter implements CoordinateFilter {

    double minx;
	double miny;
	double maxx;
	double maxy;
    boolean first = true;

    /**
     * First coordinate visited initializes the min and max fields.
     * Subsequent coordinates are compared to current bounds.
     */
    public void filter(Coordinate c) {
        if (first) {
            minx = maxx = c.x;
            miny = maxy = c.y;
            first = false;
        } else {
            minx = Math.min(minx, c.x);
            miny = Math.min(miny, c.y);
            maxx = Math.max(maxx, c.x);
            maxy = Math.max(maxy, c.y);
        }
    }

    /**
     * Return bounds as a Rectangle2D object
     */
    public Rectangle2D getBounds() {
        return new Rectangle2D.Double(minx, miny, maxx - minx, maxy - miny);
    }
}
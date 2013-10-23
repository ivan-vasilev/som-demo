package bg.tu_sofia.graduiation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public abstract class Nature implements Serializable {

    private static final long serialVersionUID = -2282299127929812304L;
    protected List<Geometry> objects = new ArrayList<Geometry>();

    public Nature() {
	super();
    }

    /**
     * @return
     */
    public List<Geometry> getObjects() {
	return objects;
    }

    public void addObject(Geometry g) {
	if (!objects.contains(g)) {
	    objects.add(g);
	}
    }

    public boolean removeObject(Geometry g) {
	return objects.remove(g);
    }

    public abstract List<Geometry> getObjectsWithinDistance(
	    Coordinate coordinate, double distance);

    public abstract List<Geometry> getObjectsWithinDistance(Point point,
	    double distance);

    /**
     * @param objects
     */
    public void setObjects(List<Geometry> objects) {
	this.objects = objects;
    }

}
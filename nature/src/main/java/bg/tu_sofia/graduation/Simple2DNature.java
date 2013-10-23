package bg.tu_sofia.graduation;

import java.util.ArrayList;
import java.util.List;

import org.geotools.geometry.jts.JTSFactoryFinder;

import bg.tu_sofia.graduiation.Nature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class Simple2DNature extends Nature {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8775022549502357430L;

	public Simple2DNature() {
		super();
	}

	public Simple2DNature(List<Geometry> objects) {
		super();
		this.objects = objects;
	}

	public List<Geometry> getObjectsWithinDistance(Coordinate coordinate,
			double distance) {
		GeometryFactory geometryFactory = JTSFactoryFinder
				.getGeometryFactory(null);

		return getObjectsWithinDistance(
				geometryFactory.createPoint(coordinate), distance);
	}

	public synchronized List<Geometry> getObjectsWithinDistance(Point point, double distance) {
		List<Geometry> objectsWithinDistance = new ArrayList<Geometry>();

		for (Geometry o : objects) {
			if (point.isWithinDistance(o, distance)) {
				objectsWithinDistance.add(o);
			}
		}

		return objectsWithinDistance;
	}

}

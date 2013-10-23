package bg.tu_sofia.graduation.robot.motion;

import java.util.List;

import org.geotools.geometry.jts.JTSFactoryFinder;

import bg.tu_sofia.graduiation.robot.motion.Sensor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

public class Simple2DSensor extends Sensor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 937149537321658235L;
	Double angle;

	public Simple2DSensor() {
		super();
	}

	public Simple2DSensor(Coordinate coordinates, Double angle) {
		super();
		this.coordinates = coordinates;
		this.angle = angle;
	}

	public Double getAngle() {
		return angle;
	}

	public void setAngle(Double angle) {
		this.angle = angle;
	}

	public double getDistanceToObstacle(List<Geometry> obstacles,
			double maxDistance) {

		LineString sensorRay = getSensorLine(coordinates, maxDistance, angle);

		GeometryFactory geometryFactory = JTSFactoryFinder
				.getGeometryFactory(null);
		Point sensorPoint = geometryFactory.createPoint(coordinates);

		double minDistance = maxDistance;

		for (Geometry g : obstacles) {
			synchronized (g) {
				if (sensorRay.intersects(g)) {
					double distance = sensorPoint.distance(g);
					if (minDistance > distance) {
						minDistance = distance;
					}
				}
			}
		}

		return minDistance;
	}

	private LineString getSensorLine(Coordinate start, Double distance,
			Double angle) {
		GeometryFactory geometryFactory = JTSFactoryFinder
				.getGeometryFactory(null);

		Double newX = start.x + distance * Math.cos(angle);
		Double newY = start.y + distance * Math.sin(angle);

		Coordinate[] coords = new Coordinate[] { start,
				new Coordinate(newX, newY) };
		LineString line = geometryFactory.createLineString(coords);

		return line;
	}
}

package bg.tu_sofia.graduiation.robot.motion;

import java.io.Serializable;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

public abstract class Sensor implements Serializable {

	private static final long serialVersionUID = -7552136805242888026L;
	protected Coordinate coordinates;

	public Sensor() {
		super();
	}

	public Sensor(Coordinate coordinates) {
		super();
		this.coordinates = coordinates;
	}

	/**
	 * @return
	 */
	public Coordinate getCoordinates() {
		return coordinates;
	}

	/**
	 * @param  coordinates
	 */
	public void setCoordinates(Coordinate coordinates) {
		this.coordinates = coordinates;
	}

	public abstract double getDistanceToObstacle(List<Geometry> obstacles, double maxDistance);
}
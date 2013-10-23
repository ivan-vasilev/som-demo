package bg.tu_sofia.graduation.robot;

import bg.tu_sofia.graduiation.Nature;
import bg.tu_sofia.graduiation.robot.Robot;
import bg.tu_sofia.graduiation.robot.motion.MotionStrategy;
import bg.tu_sofia.graduiation.robot.motion.Sensors;
import bg.tu_sofia.graduiation.som.WeightVector;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

public class Simple2DRobot extends Robot {

	private static final long serialVersionUID = -2256051708943249833L;

	public Simple2DRobot() {
		super();
	}

	public Simple2DRobot(MotionStrategy motionStrategy, Geometry shape,
			Sensors sensors, Nature nature, double speed, String id) {
		super(motionStrategy, shape, sensors, nature, speed, id);
	}

	@Override
	public WeightVector getDirectionToCoordinate(Coordinate c) {
		double xValue = c.x - sensors.getCoordinates().x;
		double yValue = c.y - sensors.getCoordinates().y;

		double length = Math.sqrt(Math.pow(xValue, 2) + Math.pow(yValue, 2));

		WeightVector vectorToGoal = new WeightVector(2);

		// Check if the length is 0. If so -> return zero vector
		if (length == 0) {
			vectorToGoal.setValue(0, 0);
			vectorToGoal.setValue(1, 0);
		} else {
			vectorToGoal.setValue(0, xValue / length);
			vectorToGoal.setValue(1, yValue / length);
		}

		return vectorToGoal;
	}

	@Override
	protected synchronized void changePosition(Geometry geometry,
			WeightVector vector) {
		for (Coordinate c : geometry.getCoordinates()) {
			double speed = calcualteSpeed();
			c.x += vector.getValue(0) * speed;
			c.y += vector.getValue(1) * speed;
		}

		geometry.geometryChanged();
	}

	@Override
	public WeightVector getVectorToCoordinate(Coordinate c) {
		double xValue = c.x - sensors.getCoordinates().x;
		double yValue = c.y - sensors.getCoordinates().y;

		WeightVector vector = new WeightVector(2);
		vector.setValue(0, xValue);
		vector.setValue(1, yValue);

		return vector;
	}
}

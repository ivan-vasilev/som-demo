package bg.tu_sofia.graduation.robot.motion;


import bg.tu_sofia.graduiation.Nature;
import bg.tu_sofia.graduiation.robot.Robot;
import bg.tu_sofia.graduiation.robot.motion.Sensors;
import bg.tu_sofia.graduiation.som.WeightVector;

import com.vividsolutions.jts.geom.Coordinate;

public class Simple2DSensors extends Sensors {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8300528495537609096L;

	public Simple2DSensors(int sensorsCount, Double maxDistance,
			Coordinate coordinates, Nature nature, Robot robot) {
		super(maxDistance, coordinates, nature, robot);

		for (int i = 0; i < sensorsCount; i++) {
			Simple2DSensor sensor = new Simple2DSensor(coordinates, i * 2 * Math.PI / sensorsCount);
			sensors.add(sensor);
		}
	}

	@Override
	public synchronized void changeCoordinates(WeightVector vector) {
		coordinates.x += vector.getValue(0);
		coordinates.y += vector.getValue(1);
	}

}

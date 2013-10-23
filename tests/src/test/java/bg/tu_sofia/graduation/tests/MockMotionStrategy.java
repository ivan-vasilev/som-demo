package bg.tu_sofia.graduation.tests;

import java.util.List;

import bg.tu_sofia.graduiation.robot.motion.MotionStrategy;
import bg.tu_sofia.graduiation.som.WeightVector;

public class MockMotionStrategy implements MotionStrategy {

	/**
	 * 
	 */
	private static final long serialVersionUID = -195003352836460263L;

	private WeightVector vector;

	public MockMotionStrategy(WeightVector vector) {
		super();
		this.vector = vector;
	}

	public WeightVector getVector() {
		return vector;
	}

	public void setVector(WeightVector vector) {
		this.vector = vector;
	}

	@Override
	public WeightVector getDirection(WeightVector sensorData, WeightVector goal) {
		return vector;
	}

	@Override
	public List<WeightVector> getDirections(WeightVector sensorData,
			WeightVector goal) {
		return null;
	}
}

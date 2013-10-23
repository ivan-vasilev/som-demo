package bg.tu_sofia.graduiation.robot.motion;

import java.io.Serializable;
import java.util.List;

import bg.tu_sofia.graduiation.som.WeightVector;

public interface MotionStrategy extends Serializable {
	WeightVector getDirection(WeightVector sensorData, WeightVector goal);
	List<WeightVector> getDirections(WeightVector sensorData, WeightVector goal);
}

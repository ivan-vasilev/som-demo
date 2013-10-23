package bg.tu_sofia.graduiation.robot.motion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bg.tu_sofia.graduiation.Nature;
import bg.tu_sofia.graduiation.robot.Robot;
import bg.tu_sofia.graduiation.som.WeightVector;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

public abstract class Sensors implements Serializable {

    private static final long serialVersionUID = 6802649895569186238L;

    protected List<Sensor> sensors = new ArrayList<Sensor>();
    protected Double maxDistance;
    protected Coordinate coordinates;
    protected Nature nature;
    protected Robot robot;

    public Sensors(List<Sensor> sensors, Double maxDistance,
	    Coordinate coordinates, Nature nature, Robot robot) {
	super();
	this.sensors = sensors;
	this.maxDistance = maxDistance;
	this.coordinates = coordinates;
	this.nature = nature;
	this.robot = robot;
    }

    public Sensors(Double maxDistance, Coordinate coordinates, Nature nature,
	    Robot robot) {
	super();
	this.maxDistance = maxDistance;
	this.coordinates = coordinates;
	this.nature = nature;
	this.robot = robot;
    }

    /**
     * @return
     */
    public Double getMaxDistance() {
	return maxDistance;
    }

    /**
     * @param maxDistance
     */
    public void setMaxDistance(Double maxDistance) {
	this.maxDistance = maxDistance;
    }

    /**
     * @return
     */
    public Robot getRobot() {
	return robot;
    }

    /**
     * @param robot
     */
    public void setRobot(Robot robot) {
	this.robot = robot;
    }

    /**
     * @return
     */
    public Nature getNature() {
	return nature;
    }

    /**
     * @param nature
     */
    public void setNature(Nature nature) {
	this.nature = nature;
    }

    /**
     * @param sensors
     */
    public void setSensors(List<Sensor> sensors) {
	this.sensors = sensors;
    }

    /**
     * @return
     */
    public List<Sensor> getSensors() {
	return sensors;
    }

    public Double getDistance() {
	return maxDistance;
    }

    public void setDistance(Double distance) {
	this.maxDistance = distance;
    }

    /**
     * @return
     */
    public Coordinate getCoordinates() {
	return coordinates;
    }

    /**
     * @param coordinates
     */
    public void setCoordinates(Coordinate coordinates) {
	this.coordinates = coordinates;
    }

    public abstract void changeCoordinates(WeightVector vector);

    public WeightVector getDistances() {
	WeightVector distances = new WeightVector(sensors.size());
	List<Geometry> obstacles = nature.getObjectsWithinDistance(coordinates,
		maxDistance);
	obstacles.remove(robot.getShape());

	for (int i = 0; i < sensors.size(); i++) {
	    double distance = sensors.get(i).getDistanceToObstacle(obstacles,
		    maxDistance);
	    distances.setValue(i, distance);
	}

	return distances;
    }
}

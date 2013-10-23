package bg.tu_sofia.graduiation.robot;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;

import bg.tu_sofia.graduiation.Nature;
import bg.tu_sofia.graduiation.NatureObjectType;
import bg.tu_sofia.graduiation.robot.motion.MotionStrategy;
import bg.tu_sofia.graduiation.robot.motion.Sensors;
import bg.tu_sofia.graduiation.som.WeightVector;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public abstract class Robot implements Runnable, Serializable {

    private static final long serialVersionUID = -3941002708626375883L;
    protected MotionStrategy motionStrategy;
    protected String id;
    protected Geometry shape;
    protected Sensors sensors;
    protected transient Nature nature;
    protected double speed = 1;
    protected transient Thread thread = null;
    protected transient Lock lock = null;
    protected transient Object suspender = null;
    protected transient int collisionCount = 0;
    protected transient int moveCount = 0;

    protected transient List<Point> goals = new ArrayList<Point>();

    public Robot(MotionStrategy motionStrategy, Geometry shape,
	    Sensors sensors, Nature nature, int collisions, double speed,
	    Lock lock, List<Point> goals) {
	super();
	this.motionStrategy = motionStrategy;
	this.shape = shape;
	this.sensors = sensors;
	this.nature = nature;
	this.speed = speed;
	this.lock = lock;
    }

    public Robot(MotionStrategy motionStrategy, Geometry shape,
	    Sensors sensors, Nature nature, double speed, String id) {
	super();
	this.motionStrategy = motionStrategy;
	this.shape = shape;
	this.sensors = sensors;
	this.nature = nature;
	this.speed = speed;
	this.id = id;
    }

    @Override
    public String toString() {
	return id;
    }

    /**
     * @return
     */
    public String getId() {
	return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
	this.id = id;
    }

    /**
     * @return
     */
    public Lock getLock() {
	return lock;
    }

    /**
     * @param lock
     */
    public void setLock(Lock lock) {
	this.lock = lock;
    }

    /**
     * @return
     */
    public Object getSuspender() {
	return suspender;
    }

    /**
     * @param suspender
     */
    public void setSuspender(Object suspender) {
	this.suspender = suspender;
    }

    /**
     * @return
     */
    public MotionStrategy getMotionStrategy() {
	return motionStrategy;
    }

    /**
     * @param motionStrategy
     */
    public void setMotionStrategy(MotionStrategy motionStrategy) {
	this.motionStrategy = motionStrategy;
    }

    /**
     * @return
     */
    public double getSpeed() {
	return speed;
    }

    /**
     * @param speed
     */
    public void setSpeed(double speed) {
	this.speed = speed;
    }

    /**
     * @return
     */
    public Geometry getShape() {
	return shape;
    }

    /**
     * @param shape
     */
    public void setShape(Geometry shape) {
	this.shape = shape;
    }

    /**
     * @return
     */
    public Sensors getSensors() {
	return sensors;
    }

    /**
     * @param sensors
     */
    public void setSensors(Sensors sensors) {
	this.sensors = sensors;
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

    public int getCollisionCount() {
	return collisionCount;
    }

    public void setCollisionCount(int collisionCount) {
	this.collisionCount = collisionCount;
    }

    public int getMoveCount() {
	return moveCount;
    }

    public void setMoveCount(int moveCount) {
	this.moveCount = moveCount;
    }

    /**
     * @return
     */
    public List<Point> getGoals() {
	return goals;
    }

    /**
     * @param goals
     */
    public void setGoals(List<Point> goals) {
	this.goals = goals;
    }

    public void addGoal(Point goal) {
	goals.add(goal);
    }

    public void removeGoal(Point goal) {
	goals.remove(goal);
    }

    public void start() {
	thread = new Thread(this, "Robot " + getId());
	thread.start();
    }

    public void stop() {
	thread = null;
    }

    public boolean isStarted() {
	return (thread != null) ? true : false;
    }

    public Robot() {
	super();
    }

    public synchronized boolean move(WeightVector direction) {
	if (!isCollisionDetected(direction)) {
	    sensors.changeCoordinates(direction);
	    changePosition(shape, direction);

	    return true;
	}

	return false;
    }

    protected synchronized void changePosition(Geometry geometry,
	    WeightVector vector) {
    }

    public Point getCurrentGoal() {
	for (Point g : goals) {
	    Object isReached = g.getUserData();
	    if (NatureObjectType.GOAL_UNREACHED.equals(isReached)) {
		return g;
	    }
	}

	return null;
    }

    public boolean isCurrentGoalReached() {

	Point goal = getCurrentGoal();

	if (goal == null) {
	    return true;
	}

	if (shape.intersects(goal)) {
	    return true;
	} else {
	    return false;
	}
    }

    public synchronized boolean isCollisionDetected(WeightVector direction) {

	Geometry clonedShape = (Geometry) shape.clone();
	changePosition(clonedShape, direction);

	List<Geometry> obstacles = nature.getObjectsWithinDistance(clonedShape
		.getCentroid(), shape.getLength());
	for (Geometry o : obstacles) {
	    if (o.intersects(clonedShape) && (o != shape)) {
		collisionCount++;
		return true;
	    }
	}

	return false;
    }

    public synchronized boolean isCollisionDetected() {
	return isCollisionDetected(motionStrategy.getDirection(sensors
		.getDistances(), getVectorToCurrentGoal()));
    }

    public WeightVector getVectorToCurrentGoal() {
	Point goal = getCurrentGoal();

	if (goal != null) {
	    return getDirectionToPoint(goal);
	}

	return null;
    }

    public boolean allGoalsReached() {
	for (Point p : goals) {
	    if (NatureObjectType.GOAL_UNREACHED.equals(p.getUserData())) {
		return false;
	    }
	}

	return true;
    }

    public abstract WeightVector getDirectionToCoordinate(Coordinate c);

    public abstract WeightVector getVectorToCoordinate(Coordinate c);

    public WeightVector getDirectionToPoint(Point point) {
	return getDirectionToCoordinate(point.getCoordinate());
    }

    public double calcualteSpeed() {
	return 1;
    }

    @Override
    public void run() {
	try {
	    Thread thisThread = Thread.currentThread();

	    skip: while (thisThread == thread) {
		suspend();

		if (isCurrentGoalReached()) {
		    getCurrentGoal().setUserData(NatureObjectType.GOAL_REACHED);
		    if (allGoalsReached()) {
			break;
		    }
		}

		WeightVector goalVector = getVectorToCurrentGoal();
		WeightVector obstacles = getSensors().getDistances();

		List<WeightVector> directions = motionStrategy.getDirections(
			obstacles, goalVector);

		// Move to list of sorted by directions if collision detected
		for (WeightVector v : directions) {
		    if (move(v)) {
			moveCount++;
			continue skip;
		    }
		}

		// if still not moved then move to random position
		WeightVector direction = new WeightVector(directions.get(0)
			.getDimensions());
		Random generator = new Random(Calendar.getInstance()
			.getTimeInMillis());

		do {
		    for (int i = 0; i < direction.getDimensions(); i++) {
			direction.setValue(i, generator.nextDouble());
		    }
		} while (!move(direction));

		moveCount++;
	    }
	} finally {
	    collisionCount = 0;
	    moveCount = 0;
	    thread = null;
	}
    }

    private void suspend() {
	if (lock != null) {
	    try {
		lock.lock();
	    } finally {
		lock.unlock();
	    }
	}

	if (suspender != null) {

	    try {
		synchronized (suspender) {
		    suspender.wait();
		}
	    } catch (InterruptedException e) {
		return;
	    }
	}
    }

    public static void saveToFile(Robot robot, String filename)
	    throws IOException, Exception {
	FileOutputStream fos = null;
	ObjectOutputStream out = null;
	fos = new FileOutputStream(filename);
	out = new ObjectOutputStream(fos);
	synchronized (robot) {
	    out.writeObject(robot);
	}

	out.close();
    }

    public static Robot loadFromFile(String filename) throws IOException,
	    ClassNotFoundException, Exception {
	Robot robot = null;

	FileInputStream fis = null;
	ObjectInputStream in = null;
	fis = new FileInputStream(filename);
	in = new ObjectInputStream(fis);
	robot = (Robot) in.readObject();
	in.close();
	robot.setGoals(new ArrayList<Point>());

	return robot;
    }
}

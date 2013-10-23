package bg.tu_sofia.graduation.tests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.junit.Test;

import bg.tu_sofia.graduation.Simple2DNature;
import bg.tu_sofia.graduation.robot.Simple2DRobot;
import bg.tu_sofia.graduation.robot.motion.Simple2DSensors;
import bg.tu_sofia.graduiation.Nature;
import bg.tu_sofia.graduiation.NatureObjectType;
import bg.tu_sofia.graduiation.robot.Robot;
import bg.tu_sofia.graduiation.robot.motion.som.MotionSOM;
import bg.tu_sofia.graduiation.som.SOMType;
import bg.tu_sofia.graduiation.som.WeightVector;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;


public class RobotTest {
	@Test
	public void testGetVectorToCurrentGoal() {
		List<Geometry> obstacles = new ArrayList<Geometry>();
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
		WKTReader reader = new WKTReader( geometryFactory );

		Point point = null;
		try {
			point = (Point) reader.read("POINT(3 3)");
			point.setUserData(NatureObjectType.GOAL_UNREACHED);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Nature nature = new Simple2DNature(obstacles);
		Coordinate coords = new Coordinate(0, 0);
		Simple2DSensors sensors = new Simple2DSensors(4, Double.valueOf(10), coords, nature, new Simple2DRobot());

		Simple2DRobot robot = new Simple2DRobot();
		robot.setSensors(sensors);
		robot.addGoal(point);
		WeightVector v = robot.getVectorToCurrentGoal();

		assertEquals(Double.valueOf(v.getValue(0)), Double.valueOf(0.7071067811865476));
		assertEquals(Double.valueOf(v.getValue(1)), Double.valueOf(0.7071067811865476));

		point.getCoordinate().x = 0;
		point.getCoordinate().y = 6;

		v = robot.getVectorToCurrentGoal();
		assertEquals(Double.valueOf(v.getValue(0)), Double.valueOf(0));
		assertEquals(Double.valueOf(v.getValue(1)), Double.valueOf(1));

		point.getCoordinate().x = -3;
		point.getCoordinate().y = 3;

		v = robot.getVectorToCurrentGoal();
		assertEquals(Double.valueOf(v.getValue(0)), Double.valueOf(-0.7071067811865476));
		assertEquals(Double.valueOf(v.getValue(1)), Double.valueOf(0.7071067811865476));

		point.getCoordinate().x = 3;
		point.getCoordinate().y = -3;

		v = robot.getVectorToCurrentGoal();
		assertEquals(Double.valueOf(v.getValue(0)), Double.valueOf(0.7071067811865476));
		assertEquals(Double.valueOf(v.getValue(1)), Double.valueOf(-0.7071067811865476));

		point.getCoordinate().x = -3;
		point.getCoordinate().y = -3;

		v = robot.getVectorToCurrentGoal();
		assertEquals(Double.valueOf(v.getValue(0)), Double.valueOf(-0.7071067811865476));
		assertEquals(Double.valueOf(v.getValue(1)), Double.valueOf(-0.7071067811865476));

		point.getCoordinate().x = -3;
		point.getCoordinate().y = -3;

		v = robot.getVectorToCurrentGoal();
		assertEquals(Double.valueOf(v.getValue(0)), Double.valueOf(-0.7071067811865476));
		assertEquals(Double.valueOf(v.getValue(1)), Double.valueOf(-0.7071067811865476));

		point.getCoordinate().x = 3;
		point.getCoordinate().y = 5.1961524227066319;

		v = robot.getVectorToCurrentGoal();
		assertEquals(Double.valueOf(v.getValue(0)), Double.valueOf(0.5));
		assertEquals(Double.valueOf(v.getValue(1)), Double.valueOf(0.8660254037844387));
	}

	@Test
	public void testIsCollisionDetected() {
		List<Geometry> obstacles = new ArrayList<Geometry>();
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
		WKTReader reader = new WKTReader( geometryFactory );

		Point point = null;
		Polygon obstacle = null;
		try {
			point = (Point) reader.read("POINT(6 9)");
			point.setUserData(NatureObjectType.GOAL_UNREACHED);
			obstacle = (Polygon) reader.read("POLYGON((5 1, 7 1, 7 3, 5 3, 5 1))");
			obstacles.add(obstacle);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Nature nature = new Simple2DNature(obstacles);
		Coordinate coords = new Coordinate(3, 2);
		Simple2DSensors sensors = new Simple2DSensors(4, Double.valueOf(10), coords, nature, new Simple2DRobot());

		Simple2DRobot robot = new Simple2DRobot();
		robot.setSensors(sensors);
		robot.addGoal(point);
		robot.setNature(nature);

		WeightVector v = new WeightVector(2);
		v.setValue(0, 3);
		v.setValue(1, 0);
		robot.setMotionStrategy(new MockMotionStrategy(v));
		
		Polygon shape = null;
		try {
			shape = (Polygon) reader.read("POLYGON((1 1, 3 1, 3 3, 1 3, 1 1))");
		} catch (ParseException e) {
			e.printStackTrace();
		}

		robot.setShape(shape);
		Boolean collisionDeteced = robot.isCollisionDetected();
		assertEquals(true, collisionDeteced);

		v.setValue(0, 1);
		v.setValue(1, 0);

		collisionDeteced = robot.isCollisionDetected();
		assertEquals(false, collisionDeteced);

		v.setValue(0, 4);
		v.setValue(1, 3);

		collisionDeteced = robot.isCollisionDetected();
		assertEquals(false, collisionDeteced);

	}

	@Test
	public void testIsCurrentGoalReached() {

		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
		Point p1 = geometryFactory.createPoint( new Coordinate(3, 3) );

		Point p2 = geometryFactory.createPoint( new Coordinate(6, 6) );

		Point p3 = geometryFactory.createPoint( new Coordinate(10, 10) );
		p3.setUserData(false);

		WKTReader reader = new WKTReader( geometryFactory );

		Polygon shape = null;
		try {
			shape = (Polygon) reader.read("POLYGON((8 8, 12 8, 12 12, 8 12, 8 8))");
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Robot robot = new Simple2DRobot();
		robot.setShape(shape);
		robot.addGoal(p1);
		robot.addGoal(p2);
		robot.addGoal(p3);

		p1.setUserData(true);
		p2.setUserData(true);
		p3.setUserData(false);
		
		assertEquals(robot.isCurrentGoalReached(), true);

		p1.setUserData(NatureObjectType.GOAL_UNREACHED);
		p2.setUserData(NatureObjectType.GOAL_UNREACHED);
		p3.setUserData(NatureObjectType.GOAL_UNREACHED);

		assertEquals(robot.isCurrentGoalReached(), false);
	}

	@Test
	public void testSerialization() {
		MotionSOM som = null;
		WeightVector somDimL1 = new WeightVector(1);
		somDimL1.setValue(0, 1);

		WeightVector somDimL2 = new WeightVector(1);
		somDimL2.setValue(0, 1);

		try {
			som = new MotionSOM(SOMType.LINEAR, 1, somDimL1, 1, SOMType.LINEAR, somDimL2, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}

		som.getLevel1SOM().getNeurons().get(0).getAssociatedSOM().getNeurons().get(0).getOutputVector().setValue(0, 1.123);
		List<Geometry> natureObjects = new ArrayList<Geometry>();

		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
		WKTReader reader = new WKTReader( geometryFactory );

		Simple2DNature nature = new Simple2DNature(natureObjects);
		Polygon natureObject = null;
		try {
			natureObject = (Polygon) reader.read("POLYGON((1 1, 300 1, 300 10, 1 100, 1 1))");
			natureObject.setUserData(NatureObjectType.STATIC_OBJECT);
			natureObjects.add(natureObject);
			natureObject = (Polygon) reader.read("POLYGON((500 1, 550 1, 550 100, 500 100, 500 1)))");
			natureObject.setUserData(NatureObjectType.STATIC_OBJECT);
			natureObjects.add(natureObject);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Polygon shape = null;
		try {
			shape = (Polygon) reader.read("POLYGON((0 0, 1 0, 1 1, 0 1, 0 0))");
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Simple2DRobot robot = new Simple2DRobot();
		robot.setMotionStrategy(som);
		robot.setShape(shape);
		robot.setNature(nature);
		Simple2DSensors sensors = new Simple2DSensors(1, 20.0, new Coordinate(0.5, 0.5), nature, robot);
		robot.setSensors(sensors);
		Point goal = geometryFactory.createPoint(new Coordinate(5, 5));
		robot.addGoal(goal);

		try {
			Robot.saveToFile(robot, "robot_test.txt");
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Simple2DRobot robot2 = null;
		try {
			robot2 = (Simple2DRobot) Robot.loadFromFile("robot_test.txt");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	    File f = new File("robot_test.txt");
		f.delete();

		assertEquals(null, robot2.getCurrentGoal());
		assertEquals(null, robot2.getNature());

		MotionSOM som2 = (MotionSOM) robot2.getMotionStrategy();
		assertEquals(2, som2.getLevel1SOM().getNeuronsCount());
		assertEquals(0, som2.getLevel1SOM().getNeurons().get(0).getCoordinates().getValue(0), 0);
		assertEquals(2, som2.getLevel1SOM().getNeurons().get(0).getAssociatedSOM().getInputDimensions());
		assertEquals(1.123, som2.getLevel1SOM().getNeurons().get(0).getAssociatedSOM().getNeurons().get(0).getOutputVector().getValue(0), 0);
	}
}

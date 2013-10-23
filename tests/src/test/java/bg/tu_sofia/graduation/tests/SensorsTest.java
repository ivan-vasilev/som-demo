package bg.tu_sofia.graduation.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.junit.Test;

import bg.tu_sofia.graduation.Simple2DNature;
import bg.tu_sofia.graduation.robot.Simple2DRobot;
import bg.tu_sofia.graduation.robot.motion.Simple2DSensor;
import bg.tu_sofia.graduation.robot.motion.Simple2DSensors;
import bg.tu_sofia.graduiation.Nature;
import bg.tu_sofia.graduiation.som.WeightVector;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class SensorsTest {

	@Test
	public void testGetDistanceToObstacle() {
		List<Geometry> obstacles = new ArrayList<Geometry>();
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
		WKTReader reader = new WKTReader( geometryFactory );

		Polygon polygon1;
		Polygon polygon2;
		Polygon polygon3;
		try {
			polygon1 = (Polygon) reader.read("POLYGON((2 1, 3 1, 3 3, 2 3, 2 1))");
			obstacles.add(polygon1);
			polygon2 = (Polygon) reader.read("POLYGON((5 1, 6 1, 6 4, 5 4, 5 1))");
			obstacles.add(polygon2);
			polygon3 = (Polygon) reader.read("POLYGON((8 1, 9 1, 9 5, 8 5, 8 1))");
			obstacles.add(polygon3);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Simple2DSensor sensor = new Simple2DSensor(new Coordinate(1, 2), Double.valueOf(0));
		assertEquals(Double.valueOf(1), Double.valueOf(sensor.getDistanceToObstacle(obstacles, Double.valueOf(10))));

		obstacles.clear();
		try {
			polygon1 = (Polygon) reader.read("POLYGON((2 1, 3 1, 3 2, 2 2, 2 1))");
			obstacles.add(polygon1);
			polygon2 = (Polygon) reader.read("POLYGON((5 1, 6 1, 6 4, 5 4, 5 1))");
			obstacles.add(polygon2);
			polygon3 = (Polygon) reader.read("POLYGON((8 1, 9 1, 9 5, 8 5, 8 1))");
			obstacles.add(polygon3);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		assertEquals(Double.valueOf(1), Double.valueOf(sensor.getDistanceToObstacle(obstacles, Double.valueOf(10))));

		sensor.getCoordinates().x = 0;
		sensor.getCoordinates().y = 0;
		sensor.setAngle(Math.PI/4);
		obstacles.clear();
		try {
			polygon1 = (Polygon) reader.read("POLYGON((2 2, 3 2, 3 3, 2 3, 2 2))");
			obstacles.add(polygon1);
			polygon2 = (Polygon) reader.read("POLYGON((5 1, 6 1, 6 4, 5 4, 5 1))");
			obstacles.add(polygon2);
			polygon3 = (Polygon) reader.read("POLYGON((8 1, 9 1, 9 5, 8 5, 8 1))");
			obstacles.add(polygon3);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		assertEquals(Double.valueOf(Math.sqrt(8)), Double.valueOf(sensor.getDistanceToObstacle(obstacles, Double.valueOf(10))));
	}

	@Test
	public void testGetDistances() {
		List<Geometry> obstacles = new ArrayList<Geometry>();
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
		WKTReader reader = new WKTReader( geometryFactory );

		LineString line1;
		LineString line2;
		LineString line3;
		LineString line4;
		try {
			line1 = (LineString) reader.read("LINESTRING (2 2, 2 4)");
			obstacles.add(line1);
			line2 = (LineString) reader.read("LINESTRING (7 2, 7 4)");
			obstacles.add(line2);
			line3 = (LineString) reader.read("LINESTRING (2 5, 4 5)");
			obstacles.add(line3);
			line4 = (LineString) reader.read("LINESTRING (1 2, 4 2)");
			obstacles.add(line4);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		Nature nature = new Simple2DNature(obstacles);
		Coordinate coords = new Coordinate(3, 3);
		Simple2DSensors sensors = new Simple2DSensors(4, Double.valueOf(10), coords, nature, new Simple2DRobot());

		WeightVector distances = sensors.getDistances();
		assertEquals(new Double(distances.getValue(0)), new Double(4));
		assertEquals(new Double(distances.getValue(1)), new Double(2));
		assertEquals(new Double(distances.getValue(2)), new Double(1));
		assertEquals(new Double(distances.getValue(3)), new Double(1));
	}
}

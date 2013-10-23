package bg.tu_sofia.graduation.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import bg.tu_sofia.graduiation.som.WeightVector;

public class WeightVectorTest {

	@Test
	public void testGetLength() {
		WeightVector vector = new WeightVector(2);
		vector.setValue(0, 3);
		vector.setValue(1, 4);
		assertEquals(new Double(5.0), new Double(vector.getLength()));

		vector.setValue(0, 0);
		vector.setValue(1, 0);
		assertEquals(new Double(0), new Double(vector.getLength()));
	}

	@Test
	public void testDistance() {
		WeightVector vector = new WeightVector(2);
		vector.setValue(0, 3);
		vector.setValue(1, 0);

		WeightVector vector2 = new WeightVector(2);
		vector2.setValue(0, 0);
		vector2.setValue(1, 4);

		assertEquals(new Double(5), new Double(vector
				.euclideanDistance(vector2)));
		assertEquals(new Double(5), new Double(WeightVector.euclideanDistance(
				vector, vector2)));

		vector = new WeightVector(2);
		vector.setValue(0, 2);
		vector.setValue(1, 2);

		vector2 = new WeightVector(2);
		vector2.setValue(0, 4);
		vector2.setValue(1, 4);

		assertEquals(new Double(Math.sqrt(8)), new Double(vector
				.euclideanDistance(vector2)));
		assertEquals(new Double(Math.sqrt(8)), new Double(WeightVector
				.euclideanDistance(vector, vector2)));

		vector = new WeightVector(2);
		vector.setValue(0, -2);
		vector.setValue(1, -2);

		vector2 = new WeightVector(2);
		vector2.setValue(0, -4);
		vector2.setValue(1, -4);

		assertEquals(new Double(Math.sqrt(8)), new Double(vector
				.euclideanDistance(vector2)));
		assertEquals(new Double(Math.sqrt(8)), new Double(WeightVector
				.euclideanDistance(vector, vector2)));

		vector = new WeightVector(2);
		vector.setValue(0, -3);
		vector.setValue(1, 0);

		vector2 = new WeightVector(2);
		vector2.setValue(0, 0);
		vector2.setValue(1, -4);

		assertEquals(new Double(5), new Double(vector
				.euclideanDistance(vector2)));
		assertEquals(new Double(5), new Double(WeightVector.euclideanDistance(
				vector, vector2)));

		vector.setValue(0, -3);
		vector.setValue(1, 0);

		vector2 = new WeightVector(2);
		vector2.setValue(0, 0);
		vector2.setValue(1, 4);

		assertEquals(new Double(5), new Double(vector
				.euclideanDistance(vector2)));
		assertEquals(new Double(5), new Double(WeightVector.euclideanDistance(
				vector, vector2)));
	}

	@Test
	public void testAreEqual() {
		WeightVector vector = new WeightVector(2);
		vector.setValue(0, 4.5);
		vector.setValue(1, 6.5);

		WeightVector vector2 = new WeightVector(2);
		vector2.setValue(0, 4.5);
		vector2.setValue(1, 6.5);

		assertEquals(true, vector.areEqual(vector2));
		assertEquals(true, vector2.areEqual(vector));
		assertEquals(true, WeightVector.areEqual(vector, vector2));
		assertEquals(true, WeightVector.areEqual(vector2, vector));

		WeightVector vector3 = new WeightVector(2);
		assertEquals(false, vector.areEqual(vector3));
		assertEquals(false, vector3.areEqual(vector));
		assertEquals(false, WeightVector.areEqual(vector, vector3));
		assertEquals(false, WeightVector.areEqual(vector3, vector));
	}

	@Test
	public void testMultiplyByNumber() {
		WeightVector v = new WeightVector(3);
		v.setValue(0, 3);
		v.setValue(1, 4);
		v.setValue(2, 5);
		v.multiplyByNumber(0.5);

		WeightVector v2 = new WeightVector(3);
		v2.setValue(0, 1.5);
		v2.setValue(1, 2);
		v2.setValue(2, 2.5);

		assertEquals(true, WeightVector.areEqual(v, v2));

		v.multiplyByNumber(3);
		WeightVector v3 = new WeightVector(3);
		v3.setValue(0, 4.5);
		v3.setValue(1, 6);
		v3.setValue(2, 7.5);

		assertEquals(true, WeightVector.areEqual(v, v3));
	}
	
	@Test
	public void testSubstract() {
		WeightVector v = new WeightVector(2);
		v.setValue(0, 3.4);
		v.setValue(1, 2);

		WeightVector v2 = new WeightVector(2);
		v2.setValue(0, -3.4);
		v2.setValue(1, 3);
		
		WeightVector v3 = WeightVector.subtract(v, v2);
		v.subtract(v2);

		assertEquals(true, 6.8 == v.getValue(0));
		assertEquals(true, -1 == v.getValue(1));
		assertEquals(true, 6.8 == v3.getValue(0));
		assertEquals(true, -1 == v3.getValue(1));
	}

	@Test
	public void testAdd() {
		WeightVector v = new WeightVector(2);
		v.setValue(0, 3.4);
		v.setValue(1, 2);

		WeightVector v2 = new WeightVector(2);
		v2.setValue(0, -3.4);
		v2.setValue(1, 3);

		WeightVector v3 = WeightVector.add(v, v2);
		v.add(v2);

		assertEquals(true, 0 == v.getValue(0));
		assertEquals(true, 5 == v.getValue(1));
		assertEquals(true, 0 == v3.getValue(0));
		assertEquals(true, 5 == v3.getValue(1));
	}
}

package bg.tu_sofia.graduiation.som;

import java.io.Serializable;

public class WeightVector implements Serializable {

    private static final long serialVersionUID = -7808751067817113845L;
    private double[] values;
    private int dimensions;

    public WeightVector(int dimensions) {
	super();
	this.dimensions = dimensions;
	values = new double[dimensions];
    }

    /**
     * @return
     */
    public int getDimensions() {
	return dimensions;
    }

    public double getValue(int dimension) {
	double value = -1;
	try {
	    value = values[dimension];
	} catch (ArrayIndexOutOfBoundsException ex) {
	    System.out.println(ex.getMessage());
	}

	return value;
    }

    public void setValue(int dimension, double value) {
	try {
	    values[dimension] = value;
	} catch (ArrayIndexOutOfBoundsException ex) {
	    System.out.println(ex.getMessage());
	}
    }

    public double getLength() {
	double length = 0;

	for (double d : values) {
	    length += Math.pow(d, 2);
	}

	return Math.sqrt(length);
    }

    public double euclideanDistance(WeightVector vector)
	    throws ArrayIndexOutOfBoundsException {
	return euclideanDistance(this, vector);
    }

    public static double euclideanDistance(WeightVector v1, WeightVector v2)
	    throws ArrayIndexOutOfBoundsException {

	if (v1.getDimensions() != v2.getDimensions()) {
	    throw new ArrayIndexOutOfBoundsException(
		    "Error: vectors have different dimensions");
	}

	double distance = 0;

	int dimensions = v1.getDimensions();

	for (int i = 0; i < dimensions; i++) {
	    double diff = v1.getValue(i) - v2.getValue(i);
	    distance += Math.pow(diff, 2);
	}

	return Math.sqrt(distance);
    }

    public static boolean areEqual(WeightVector v1, WeightVector v2) {
	if (v1 == v2) {
	    return true;
	}

	if ((v1 == null) || (v2 == null)) {
	    return false;
	}

	if (v1.getDimensions() != v2.getDimensions()) {
	    return false;
	}

	for (int i = 0; i < v1.getDimensions(); i++) {
	    if (v1.getValue(i) != v2.getValue(i)) {
		return false;
	    }
	}

	return true;
    }

    public boolean areEqual(WeightVector v) {
	return areEqual(this, v);
    }

    public void multiplyByNumber(double n) {
	for (int i = 0; i < dimensions; i++) {
	    values[i] *= n;
	}
    }

    public void subtract(WeightVector v) {

	if (this.getDimensions() != v.getDimensions()) {
	    throw new ArrayIndexOutOfBoundsException(
		    "Error: Vectors are with different dimensions");
	}

	for (int i = 0; i < dimensions; i++) {
	    double value = this.getValue(i) - v.getValue(i);
	    this.setValue(i, value);
	}
    }

    public static WeightVector subtract(WeightVector v1, WeightVector v2) {
	if (v1.getDimensions() != v2.getDimensions()) {
	    throw new ArrayIndexOutOfBoundsException(
		    "Error: Vectors are with different dimensions");
	}

	WeightVector res = new WeightVector(v1.getDimensions());

	for (int i = 0; i < v1.getDimensions(); i++) {
	    double value = v1.getValue(i) - v2.getValue(i);
	    res.setValue(i, value);
	}

	return res;
    }

    public void add(WeightVector v) {
	if (this.getDimensions() != v.getDimensions()) {
	    throw new ArrayIndexOutOfBoundsException(
		    "Error: Vectors are with different dimensions");
	}

	for (int i = 0; i < dimensions; i++) {
	    values[i] += v.getValue(i);
	}
    }

    public static WeightVector add(WeightVector v1, WeightVector v2) {
	if (v1.getDimensions() != v2.getDimensions()) {
	    throw new ArrayIndexOutOfBoundsException(
		    "Error: Vectors are with different dimensions");
	}

	WeightVector res = new WeightVector(v1.getDimensions());

	for (int i = 0; i < v1.getDimensions(); i++) {
	    double value = v1.getValue(i) + v2.getValue(i);
	    res.setValue(i, value);
	}

	return res;
    }
}

package bg.tu_sofia.graduiation.som;

/**
 * @author Ivan Vasilev
 * 
 */
public class MatrixSOM extends SOM {

	/**
	 * 
	 */
	private static final long serialVersionUID = -114124802149206726L;

	public MatrixSOM(int inputDimensions, WeightVector coordinateDimensions, int outputDimensions, double neuronDistance) throws Exception {
		super(inputDimensions, coordinateDimensions, outputDimensions, neuronDistance);
		if (coordinateDimensions.getDimensions() != 2) {
			throw new Exception("Only two dimensional vectors allowed");
		}
	}

	@Override
	protected void generateNeuronsCoordinates(WeightVector dim, double neuronsDistance) {
		int rows = (int) dim.getValue(0);
		int columns = (int) dim.getValue(1);

		for (int i = 0; i <= rows; i++) {
			for (int j = 0; j <= columns; j++) {
				WeightVector coordinates = new WeightVector(2);
				coordinates.setValue(0, j * neuronsDistance);
				coordinates.setValue(1, i * neuronsDistance);

				Neuron nr = new Neuron(inputDimensions);
				neurons.add(nr);
				nr.setCoordinates(coordinates);
			}
		}
	}
}

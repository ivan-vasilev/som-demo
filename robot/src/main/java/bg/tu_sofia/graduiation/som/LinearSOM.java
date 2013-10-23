package bg.tu_sofia.graduiation.som;

public class LinearSOM extends SOM {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4689094361292249397L;

	public LinearSOM(int inputDimensions, WeightVector coordinateDimensions, int outputDimensions, double neuronDistance) throws Exception {
		super(inputDimensions, coordinateDimensions, outputDimensions, neuronDistance);

		if (coordinateDimensions.getDimensions() != 1) {
			throw new Exception("Only one dimensional vectors allowed");
		}
	}

	public Neuron getNeuron(int index) {
		return neurons.get(index);
	}

	@Override
	protected void generateNeuronsCoordinates(WeightVector dim, double neuronsDistance) {
		int neuronsCount = (int) dim.getValue(0);

		for (int i = 0; i <= neuronsCount; i++) {
			WeightVector coordinates = new WeightVector(1);
			coordinates.setValue(0, i * neuronsDistance);

			Neuron nr = new Neuron(inputDimensions);
			neurons.add(nr);
			nr.setCoordinates(coordinates);
		}
	}
}

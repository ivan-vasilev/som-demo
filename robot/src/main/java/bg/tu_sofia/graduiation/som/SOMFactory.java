package bg.tu_sofia.graduiation.som;

public class SOMFactory {
	public static SOM createSOM(SOMType type, int inputDimensions, WeightVector coordinateDimensions, int outputDimensions, double neuronDistance) throws Exception {

		switch (type) {
		case MATRIX:
			return new MatrixSOM(inputDimensions, coordinateDimensions, outputDimensions, neuronDistance);
		case LINEAR:
			return new LinearSOM(inputDimensions, coordinateDimensions, outputDimensions, neuronDistance);
		}

		return null;
	}
}

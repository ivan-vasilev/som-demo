package bg.tu_sofia.graduiation.som;

import java.util.Calendar;
import java.util.Random;

import bg.tu_sofia.graduiation.som.Neuron.NeuronVectors;

public class Trainer {


    private SOM som;
    private Integer iterations = 1;
    private int maxIterations;
    private double initLearningRate;			     
    private double initNeighborhoodParam;

    public Trainer(SOM som, int maxIterations, double initLearningRate,
	    double initNeighborhoodParam) {
	super();
	this.som = som;
	this.maxIterations = maxIterations;
	this.initLearningRate = initLearningRate;
	this.initNeighborhoodParam = initNeighborhoodParam;

	generateRandomInputWeightValues(null);
    }

    public Trainer(int maxIterations, double initLearningRate,
	    double initNeighborhoodParam) {
	super();
	this.maxIterations = maxIterations;
	this.initLearningRate = initLearningRate;
	this.initNeighborhoodParam = initNeighborhoodParam;
    }

    /**
     * @return
     */
    public SOM getSom() {
	return som;
    }

    /**
     * @param som
     */
    public void setSom(SOM som) {
	this.som = som;
    }

    /**
     * @return
     */
    public Integer getIterations() {
	return iterations;
    }

    /**
     * @param iterations
     */
    public void setIterations(Integer iterations) {
	this.iterations = iterations;
    }

    public int getIterationsMax() {
	return maxIterations;
    }

    public void setIterationsMax(int iterationsMax) {
	this.maxIterations = iterationsMax;
    }

    /**
     * @return
     */
    public int getMaxIterations() {
	return maxIterations;
    }

    /**
     * @param maxIterations
     */
    public void setMaxIterations(int maxIterations) {
	this.maxIterations = maxIterations;
    }

    /**
     * @return
     */
    public double getInitLearningRate() {
	return initLearningRate;
    }

    /**
     * @param initLearningRate
     */
    public void setInitLearningRate(double initLearningRate) {
	this.initLearningRate = initLearningRate;
    }

    /**
     * @return
     */
    public double getInitNeighborhoodParam() {
	return initNeighborhoodParam;
    }

    /**
     * @param initNeighborhoodParam
     */
    public void setInitNeighborhoodParam(double initNeighborhoodParam) {
	this.initNeighborhoodParam = initNeighborhoodParam;
    }

    private void trainInput(WeightVector input) throws Exception {

	if (input.getDimensions() != som.getInputDimensions()) {
	    throw new ArrayIndexOutOfBoundsException(
		    "Error: Vectors are with different number of dimensions");
	}

	if (iterations > maxIterations) {
	    throw new Exception("Error: Maximum number of iterations reached");
	}

	WeightVector winNeuronCoords = som.getWinningNeuronByInput(input)
		.getCoordinates();

	double learningRate = getLearningRate();

	for (Neuron n : som.getNeurons()) {

	    WeightVector weight = n.getInputVector();

	    WeightVector difference = WeightVector.subtract(input, weight);

	    double neighborParam = getNeighborParam(winNeuronCoords, n
		    .getCoordinates());

	    difference.multiplyByNumber(learningRate * neighborParam);
	    weight.add(difference);
	}
    }

    private void trainOutput(WeightVector input, WeightVector output)
	    throws Exception {
	if (output.getDimensions() != som.getOutputDimensions()) {
	    throw new Exception("Error: Vectors are with different dimensions");
	}

	if (iterations > maxIterations) {
	    throw new Exception("Error: Maximum number of iterations reached");
	}

	WeightVector winNeuronCoords = som.getWinningNeuronByInput(input)
		.getCoordinates();

	double learningRate = getLearningRate();

	for (Neuron n : som.getNeurons()) {
	    double neighborParam = getNeighborParam(winNeuronCoords, n
		    .getCoordinates());

	    WeightVector neuronOutput = n.getOutputVector();

	    WeightVector difference = WeightVector.subtract(output,
		    neuronOutput);
	    difference.multiplyByNumber(learningRate * neighborParam);
	    neuronOutput.add(difference);
	}
    }

    public void train(WeightVector input, WeightVector output,
	    boolean increaseIterations) throws Exception {
	if (input == null) {
	    throw new Exception("Error: No input set provided");
	}

	if (iterations > maxIterations) {
	    throw new Exception("Error: Maximum number of iterations reached");
	}

	trainInput(input);

	if (output != null) {
	    trainOutput(input, output);
	}

	if (increaseIterations) {
	    iterations++; // last !
	}
    }

    public void increaseIterations() {
	if (iterations < maxIterations) {
	    iterations++;
	}
    }

    public double getLearningRate() {
	    double rate = initLearningRate
		    * (1 - iterations / Double.valueOf(maxIterations));
	    return rate;
    }

    public double getNeighborParam(WeightVector coords1, WeightVector coords2) {
	double neighborParam = 0;
	    WeightVector vectorDistance = WeightVector.subtract(coords1,
		    coords2);
	    double vectorLength = vectorDistance.getLength();

	    switch ((int) vectorLength) {
	    case 0:
		neighborParam = 1;
		break;
	    case 1:
		neighborParam = 0.5;
		break;
	    case 2:
		neighborParam = 0.25;
		break;
	    case 3:
		neighborParam = 0.125;
		break;
	    case 4:
		neighborParam = 0.0625;
		break;
	    case 5:
		neighborParam = 0.03125;
		break;
	    case 6:
		neighborParam = 0.015625;
		break;
	    case 7:
		neighborParam = 0.0078125;
		break;
	    }

	    neighborParam *= initNeighborhoodParam
		    * (1 - iterations / Double.valueOf(maxIterations));

	    return neighborParam;
    }

    public void generateRandomInputWeightValues(Double maxValue) {
	generateRandomWeightValues(NeuronVectors.INPUT, maxValue);
    }

    public void generateRandomOutputWeightValues(Double maxValue) {
	generateRandomWeightValues(NeuronVectors.OUTPUT, maxValue);
    }

    public void resetSOM(SOM som, Integer iterations) {
	if (iterations != null) {
	    this.iterations = iterations;
	}

	this.som = som;
    }

    private void generateRandomWeightValues(NeuronVectors v, Double maxValue) {
	Random random = new Random(Calendar.getInstance().getTimeInMillis());
	if (maxValue == null) {
	    maxValue = 1.0;
	}

	for (Neuron n : som.getNeurons()) {
	    WeightVector vector = null;
	    if (v.equals(NeuronVectors.INPUT)) {
		vector = n.getInputVector();
	    } else if (v.equals(NeuronVectors.OUTPUT)) {
		vector = n.getOutputVector();
	    }

	    int dim = vector.getDimensions();

	    for (int i = 0; i < dim; i++) {
		vector.setValue(i, random.nextDouble() * maxValue);
	    }
	}
    }
}

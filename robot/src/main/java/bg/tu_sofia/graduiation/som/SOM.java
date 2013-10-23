package bg.tu_sofia.graduiation.som;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bg.tu_sofia.graduiation.som.Neuron.NeuronVectors;

public abstract class SOM implements Serializable {

    private static final long serialVersionUID = 4399786648291042114L;

    protected int inputDimensions;
    protected int outputDimensions;

    protected List<Neuron> neurons = new ArrayList<Neuron>();

    public SOM(int inputDimensions, WeightVector coordinateDimensions,
	    int outputDimensions, double neuronDistance) {
	super();
	this.inputDimensions = inputDimensions;
	this.outputDimensions = outputDimensions;

	generateNeuronsCoordinates(coordinateDimensions, neuronDistance);
    }

    public Neuron getWinningNeuronByInput(WeightVector inputVector) {
	return getWinningNeuron(inputVector, NeuronVectors.INPUT);
    }

    public Neuron getWinningNeuronByOutput(WeightVector outputVector) {
	return getWinningNeuron(outputVector, NeuronVectors.OUTPUT);
    }

    private Neuron getWinningNeuron(WeightVector vector, NeuronVectors type) {
	Neuron winner = null;
	double winnerDistance = Double.POSITIVE_INFINITY;

	try {
	    for (Neuron n : neurons) {
		double distance = Double.POSITIVE_INFINITY;
		if (type == NeuronVectors.INPUT) {
		    distance = n.getInputVector().euclideanDistance(vector);
		} else if (type == NeuronVectors.OUTPUT) {
		    distance = n.getOutputVector().euclideanDistance(vector);
		}

		if (distance < winnerDistance) {
		    winner = n;
		    winnerDistance = distance;
		}
	    }
	} catch (ArrayIndexOutOfBoundsException ex) {
	    System.out.println(ex.getMessage());
	}

	return winner;
    }

    public List<Neuron> getWinningNeuronsByInput(WeightVector inputVector) {
	return getWinningNeurons(inputVector, NeuronVectors.INPUT);
    }

    public List<Neuron> getWinningNeuronsByOutput(WeightVector outputVector) {
	return getWinningNeurons(outputVector, NeuronVectors.OUTPUT);
    }

    private List<Neuron> getWinningNeurons(final WeightVector vector,
	    final NeuronVectors type) {

	List<Neuron> sortedNeurons = new ArrayList<Neuron>(neurons);

	Comparator<Neuron> neuronComparator = new Comparator<Neuron>() {

	    @Override
	    public int compare(Neuron o1, Neuron o2) {
		double distance1 = Double.POSITIVE_INFINITY;

		if (type == NeuronVectors.INPUT) {
		    distance1 = o1.getInputVector().euclideanDistance(vector);
		} else if (type == NeuronVectors.OUTPUT) {
		    distance1 = o1.getOutputVector().euclideanDistance(vector);
		}

		double distance2 = Double.POSITIVE_INFINITY;

		if (type == NeuronVectors.INPUT) {
		    distance2 = o2.getInputVector().euclideanDistance(vector);
		} else if (type == NeuronVectors.OUTPUT) {
		    distance2 = o2.getOutputVector().euclideanDistance(vector);
		}

		if (distance1 < distance2) {
		    return -1;
		} else if (distance1 > distance2) {
		    return 1;
		}

		return 0;
	    }
	};

	Collections.sort(sortedNeurons, neuronComparator);

	return sortedNeurons;
    }

    /**
     * @return
     */
    public int getInputDimensions() {
	return inputDimensions;
    }

    /**
     * @return
     */
    public int getOutputDimensions() {
	return outputDimensions;
    }

    /**
     * @return
     */
    public List<Neuron> getNeurons() {
	return neurons;
    }

    public int getNeuronsCount() {
	return neurons.size();
    }

    public Neuron getNeuron(WeightVector coordinates) {

	if (coordinates.getDimensions() != getInputDimensions()) {
	    throw new ArrayIndexOutOfBoundsException(
		    "Error: Vectors have different dimensions");
	}

	for (Neuron n : neurons) {
	    if (n.getCoordinates().areEqual(coordinates)) {
		return n;
	    }
	}

	return null;
    }

    protected abstract void generateNeuronsCoordinates(WeightVector dim,
	    double neuronsDistance);

    protected void addNeuron(Neuron n) {
	neurons.add(n);
    }
}

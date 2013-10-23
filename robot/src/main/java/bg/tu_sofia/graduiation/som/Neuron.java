package bg.tu_sofia.graduiation.som;

import java.io.Serializable;

/**
 * @author   hok
 */
public class Neuron implements Serializable {

	private static final long serialVersionUID = 1769656477021552417L;

	private WeightVector coordinates;
	private WeightVector inputVector;
	private WeightVector outputVector = null;
	private SOM associatedSOM = null;

	public enum NeuronVectors {
		INPUT,
		OUTPUT,
		COORDINATES
	};

	public Neuron(int dimensions) {
		super();
		inputVector = new WeightVector(dimensions);
	}

	public Neuron(WeightVector inputVector) {
		super();
		this.inputVector = inputVector;
	}

	/**
	 * @return
	 */
	public WeightVector getInputVector() {
		return inputVector;
	}

	/**
	 * @param  inputVector
	 */
	public void setInputVector(WeightVector inputVector) {
		this.inputVector = inputVector;
	}

	/**
	 * @return
	 */
	public WeightVector getOutputVector() {
		return outputVector;
	}

	/**
	 * @param  outputVector
	 */
	public void setOutputVector(WeightVector outputVector) {
		this.outputVector = outputVector;
	}

	/**
	 * @return
	 */
	public SOM getAssociatedSOM() {
		return associatedSOM;
	}

	/**
	 * @param  associatedSOM
	 */
	public void setAssociatedSOM(SOM associatedSOM) {
		this.associatedSOM = associatedSOM;
	}	

	/**
	 * @return
	 */
	public WeightVector getCoordinates() {
		return coordinates;
	}

	/**
	 * @param  coordinates
	 */
	public void setCoordinates(WeightVector coordinates) {
		this.coordinates = coordinates;
	}
}

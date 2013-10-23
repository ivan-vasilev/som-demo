package bg.tu_sofia.graduiation.robot.motion.som;

import java.util.ArrayList;
import java.util.List;

import bg.tu_sofia.graduiation.robot.motion.MotionStrategy;
import bg.tu_sofia.graduiation.som.*;

public class MotionSOM implements MotionStrategy {

    private static final long serialVersionUID = 965846445147171573L;
    private SOM level1SOM;

    public MotionSOM(SOMType level1Type, int level1inputDim,
	    WeightVector level1Coords, double l1neuronDistance,
	    SOMType level2Type, WeightVector level2Coords,
	    double l2neuronDistance) throws Exception {
	super();
	level1SOM = SOMFactory.createSOM(level1Type, level1inputDim,
		level1Coords, 0, l1neuronDistance);

	for (Neuron n : level1SOM.getNeurons()) {
	    SOM level2SOM = SOMFactory.createSOM(level2Type, 2, level2Coords,
		    2, l2neuronDistance);
	    n.setAssociatedSOM(level2SOM);

	    // Creating output vectors
	    for (Neuron n1 : level2SOM.getNeurons()) {
		WeightVector v = new WeightVector(2);
		n1.setOutputVector(v);
	    }
	}
    }

    /**
     * @return
     */
    public SOM getLevel1SOM() {
	return level1SOM;
    }

    public Neuron getLevel1Winner(WeightVector input) {
	return level1SOM.getWinningNeuronByInput(input);
    }

    public SOM getLevel2WinnerSOM(WeightVector input) {
	SOM som = level1SOM.getWinningNeuronByInput(input).getAssociatedSOM();
	return som;
    }

    @Override
    public WeightVector getDirection(WeightVector sensorData, WeightVector goal) {

	SOM winnerSOM = level1SOM.getWinningNeuronByInput(sensorData)
		.getAssociatedSOM();

	Neuron n = winnerSOM.getWinningNeuronByInput(goal);

	return getNormalizedVector(n.getOutputVector());
    }

    @Override
    public List<WeightVector> getDirections(WeightVector sensorData,
	    WeightVector goal) {

	List<WeightVector> directions = new ArrayList<WeightVector>();

	SOM winnerSOM = level1SOM.getWinningNeuronByInput(sensorData)
		.getAssociatedSOM();

	for (Neuron n : winnerSOM.getWinningNeuronsByInput(goal)) {
	    directions.add(getNormalizedVector(n.getOutputVector()));
	}

	return directions;
    }

    private WeightVector getNormalizedVector(WeightVector v) {
	WeightVector res = new WeightVector(v.getDimensions());
	double length = v.getLength();

	if (length != 0) {
	    for (int i = 0; i < v.getDimensions(); i++) {
        	res.setValue(i, v.getValue(i) / length);
	    }
	}

	return res;
    }
}

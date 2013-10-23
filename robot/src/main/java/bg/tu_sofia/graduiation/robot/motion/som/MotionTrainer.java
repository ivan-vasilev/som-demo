package bg.tu_sofia.graduiation.robot.motion.som;

import java.awt.geom.Rectangle2D;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import bg.tu_sofia.graduiation.Nature;
import bg.tu_sofia.graduiation.robot.Robot;
import bg.tu_sofia.graduiation.som.Neuron;
import bg.tu_sofia.graduiation.som.SOM;
import bg.tu_sofia.graduiation.som.Trainer;
import bg.tu_sofia.graduiation.som.WeightVector;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

public class MotionTrainer {

    private MotionSOM som;
    private Trainer trainer;
    private Nature nature;
    private Robot robot;

    Integer level1SOMitereations = 1;
    HashMap<SOM, Integer> iterations = new HashMap<SOM, Integer>();

    public MotionTrainer() {
	super();
    }

    public MotionTrainer(MotionSOM som, Trainer trainer, Nature nature,
	    Robot robot) {
	super();
	this.som = som;
	this.trainer = trainer;
	this.trainer.setSom(som.getLevel1SOM());
	this.nature = nature;
	this.robot = robot;

	setInitialWeights();

    }

    /**
     * @return
     */
    public int getLevel1SOMitereations() {
	return level1SOMitereations;
    }

    /**
     * @return
     */
    public MotionSOM getSom() {
	return som;
    }

    /**
     * @param som
     */
    public void setSom(MotionSOM som) {
	this.som = som;
    }

    public Trainer getLevel1Trainer() {
	return trainer;
    }

    public void setLevel1Trainer(Trainer level1Trainer) {
	this.trainer = level1Trainer;
    }

    /**
     * @return
     */
    public Nature getNature() {
	return nature;
    }

    /**
     * @param nature
     */
    public void setNature(Nature nature) {
	this.nature = nature;
    }

    /**
     * @return
     */
    public Robot getRobot() {
	return robot;
    }

    /**
     * @param robot
     */
    public void setRobot(Robot robot) {
	this.robot = robot;
    }

    public void trainLevel1SOM(WeightVector input) throws Exception {
	trainer.setSom(som.getLevel1SOM());
	trainer.setIterations(level1SOMitereations);
	trainer.train(input, null, true);
	level1SOMitereations = trainer.getIterations();
    }

    public void trainLevel2SOM(WeightVector inputLevel1,
	    WeightVector inputLevel2, WeightVector outputLevel2)
	    throws Exception {
	SOM winningSOM = som.getLevel2WinnerSOM(inputLevel1);
	trainer.setSom(winningSOM);

	// if SOM not entered yet
	if (!iterations.containsKey(winningSOM)) {
	    iterations.put(winningSOM, 1);
	}

	Integer winningSOMIterations = iterations.get(winningSOM);

	trainer.setIterations(winningSOMIterations);

	trainer.train(inputLevel2, outputLevel2, true);

	iterations.put(winningSOM, trainer.getIterations());
    }

    public void trainAllLevels(WeightVector inputLevel1,
	    WeightVector inputLevel2, WeightVector outputLevel2)
	    throws Exception {
	trainLevel1SOM(inputLevel1);
	trainLevel2SOM(inputLevel1, inputLevel2, outputLevel2);
    }

    public void moveToRandomPosition() {
	List<Geometry> objects = nature.getObjects();

	Bounds2DFilter bf = new Bounds2DFilter();
	for (Geometry g : objects) {
	    g.apply(bf);
	}

	Rectangle2D bounds = bf.getBounds();

	Random generator = new Random(Calendar.getInstance().getTimeInMillis());

	WeightVector direction = null;
	do {
	    double randX = Math.abs(generator.nextDouble() * bounds.getWidth()
		    + bounds.getMinX());
	    double randY = Math.abs(generator.nextDouble() * bounds.getHeight()
		    + bounds.getMinY());

	    direction = robot
		    .getVectorToCoordinate(new Coordinate(randX, randY));
	} while (!robot.move(direction));
    }

    public void setInitialWeights() {
	// Level 1 neurons initial random values
	trainer.setSom(som.getLevel1SOM());
	trainer.generateRandomInputWeightValues(robot.getSensors()
		.getMaxDistance());

	int maxIterations = trainer.getMaxIterations();
	try {
	    trainer.setMaxIterations(maxIterations * 10);
	    while (level1SOMitereations <= trainer.getMaxIterations()) {
		moveToRandomPosition();
		try {
		    trainLevel1SOM(robot.getSensors().getDistances());
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	} finally {
	    level1SOMitereations = 1;
	    trainer.setIterations(1);
	    trainer.setMaxIterations(maxIterations);
	}

	// Level 2 neurons initial values
	List<Neuron> neurons = som.getLevel1SOM().getNeurons();
	for (Neuron n : neurons) {
	    SOM level2som = n.getAssociatedSOM();
	    int neuronsCount = level2som.getNeuronsCount();

	    for (int i = 0; i < neuronsCount; i++) {
		Neuron level2neuron = level2som.getNeurons().get(i);

		WeightVector inputVector = new WeightVector(2);
		inputVector.setValue(0, Math
			.cos(i * 2 * Math.PI / neuronsCount));
		inputVector.setValue(1, Math
			.sin(i * 2 * Math.PI / neuronsCount));
		level2neuron.setInputVector(inputVector);

		WeightVector outputVector = new WeightVector(2);
		outputVector.setValue(0, Math.cos(i * 2 * Math.PI
			/ neuronsCount));
		outputVector.setValue(1, Math.sin(i * 2 * Math.PI
			/ neuronsCount));
		level2neuron.setOutputVector(outputVector);
	    }
	}
    }
}

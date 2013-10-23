package bg.tu_sofia.graduation.tests;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import bg.tu_sofia.graduiation.som.MatrixSOM;
import bg.tu_sofia.graduiation.som.Neuron;
import bg.tu_sofia.graduiation.som.SOM;
import bg.tu_sofia.graduiation.som.SOMFactory;
import bg.tu_sofia.graduiation.som.SOMType;
import bg.tu_sofia.graduiation.som.Trainer;
import bg.tu_sofia.graduiation.som.WeightVector;

public class TrainerTest {

	@Test
	public void testGetLearningRate() {
		WeightVector vector = new WeightVector(2);
		vector.setValue(0, 2);
		vector.setValue(1, 2);
		
		SOM som = null;
		try {
			som = SOMFactory.createSOM(SOMType.MATRIX, 2, vector, 2, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Trainer trainer = new Trainer(som, 3, 0.1, 0.1);
		double learningRate = trainer.getLearningRate();
		assertEquals(new Double(learningRate), new Double(0.06666666666666668));

		try {
			trainer.train(vector, null, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		learningRate = trainer.getLearningRate();
		assertEquals(new Double(learningRate), new Double(0.03333333333333334));

		try {
			trainer.train(vector, null, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		learningRate = trainer.getLearningRate();
		assertEquals(new Double(learningRate), new Double(0));		
	}

	@Test
	public void testGetNeighborParam() {
		WeightVector vector = new WeightVector(2);
		vector.setValue(0, 2);
		vector.setValue(1, 2);

		SOM som = null;
		try {
			som = SOMFactory.createSOM(SOMType.MATRIX, 2, vector, 2, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Trainer trainer = new Trainer(som, 3, 0.1, 0.1);
		double neighborParam = trainer.getNeighborParam(vector, vector);
		assertEquals(new Double(neighborParam), new Double(0.06666666666666668));

		try {
			trainer.train(vector, null, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		neighborParam = trainer.getNeighborParam(vector, vector);
		assertEquals(new Double(neighborParam), new Double(0.03333333333333334));

		try {
			trainer.train(vector, null, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		neighborParam = trainer.getNeighborParam(vector, vector);
		assertEquals(new Double(neighborParam), new Double(0));		
	}

	@Test
	public void testTrainInput() {
		WeightVector dim = new WeightVector(2);
		dim.setValue(0, 1);
		dim.setValue(1, 1);

		MatrixSOM som = null;
		try {
			som = new MatrixSOM(2, dim, 0, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Trainer t = new Trainer(som, 4, 0.1, 0.1);

		List<Neuron> ns = som.getNeurons();

		Neuron n = ns.get(0);
		n.getInputVector().setValue(0, 3);
		n.getInputVector().setValue(1, 4);

		n = ns.get(1);
		n.getInputVector().setValue(0, -3);
		n.getInputVector().setValue(1, 4);

		n = ns.get(2);
		n.getInputVector().setValue(0, -3);
		n.getInputVector().setValue(1, -4);

		n = ns.get(3);
		n.getInputVector().setValue(0, 3);
		n.getInputVector().setValue(1, -4);

		WeightVector vTrain = new WeightVector(2);
		vTrain.setValue(0, 5);
		vTrain.setValue(1, 6);

		try {
			t.train(vTrain, null, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		WeightVector vTest = new WeightVector(2);
		vTest.setValue(0, 3.01125);
		vTest.setValue(1, 4.01125);

		n = ns.get(0);
		assertEquals(true, WeightVector.areEqual(vTest, n.getInputVector()));

		try {
			t.train(vTrain, null, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		vTest.setValue(0, 3.016221875);
		vTest.setValue(1, 4.016221875);

		n = ns.get(0);
		assertEquals(true, WeightVector.areEqual(vTest, n.getInputVector()));
	}

	@Test
	public void testTrainOutput() {
		WeightVector dim = new WeightVector(2);
		dim.setValue(0, 1);
		dim.setValue(1, 1);

		MatrixSOM som = null;
		try {
			som = new MatrixSOM(2, dim, 2, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Trainer t = new Trainer(som, 4, 0.1, 0.1);

		List<Neuron> ns = som.getNeurons();

		WeightVector vOut = new WeightVector(2);
		Neuron n = ns.get(0);
		n.setOutputVector(vOut);
		vOut.setValue(0, 3);
		vOut.setValue(1, 4);
		n.getInputVector().setValue(0, 3);
		n.getInputVector().setValue(1, 4);

		n = ns.get(1);
		vOut = new WeightVector(2);
		n.setOutputVector(vOut);
		vOut.setValue(0, -3);
		vOut.setValue(1, 4);
		n.getInputVector().setValue(0, -3);
		n.getInputVector().setValue(1, 4);

		n = ns.get(2);
		vOut = new WeightVector(2);
		n.setOutputVector(vOut);
		vOut.setValue(0, -3);
		vOut.setValue(1, -4);
		n.getInputVector().setValue(0, -3);
		n.getInputVector().setValue(1, -4);

		n = ns.get(3);
		vOut = new WeightVector(2);
		n.setOutputVector(vOut);
		vOut.setValue(0, 3);
		vOut.setValue(1, -4);
		n.getInputVector().setValue(0, 3);
		n.getInputVector().setValue(1, -4);

		WeightVector vTrain = new WeightVector(2);
		vTrain.setValue(0, 5);
		vTrain.setValue(1, 6);

		try {
			t.train(vTrain, vTrain, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		WeightVector vTest = new WeightVector(2);
		vTest.setValue(0, 3.01125);
		vTest.setValue(1, 4.01125);

		n = ns.get(0);
		assertEquals(true, WeightVector.areEqual(vTest, n.getInputVector()));

		try {
			t.train(vTrain, vTrain, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		vTest.setValue(0, 3.016221875);
		vTest.setValue(1, 4.016221875);

		n = ns.get(0);
		assertEquals(true, WeightVector.areEqual(vTest, n.getInputVector()));
	}
}

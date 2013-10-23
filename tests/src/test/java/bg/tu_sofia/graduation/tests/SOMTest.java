package bg.tu_sofia.graduation.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import bg.tu_sofia.graduiation.som.LinearSOM;
import bg.tu_sofia.graduiation.som.MatrixSOM;
import bg.tu_sofia.graduiation.som.Neuron;
import bg.tu_sofia.graduiation.som.WeightVector;

import java.util.List;

public class SOMTest {

	@Test
	public void testWinningNeuron() {
		LinearSOM som = null;
		try {
			WeightVector v = new WeightVector(1);
			v.setValue(0, 1);
			som = new LinearSOM(2, v, 2, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}

		WeightVector v1 = som.getNeuron(0).getInputVector();
		som.getNeuron(0).setOutputVector(v1);
		v1.setValue(0, 2);
		v1.setValue(1, 2);

		WeightVector v2 = som.getNeuron(1).getInputVector();
		som.getNeuron(1).setOutputVector(v2);
		v2.setValue(0, 5);
		v2.setValue(1, 5);

		WeightVector vTest = new WeightVector(2);
		vTest.setValue(0, 3);
		vTest.setValue(1, 3);

		Neuron winner = som.getWinningNeuronByInput(vTest);
		assertEquals(winner, som.getNeuron(0));

		winner = som.getWinningNeuronByOutput(vTest);
		assertEquals(winner, som.getNeuron(0));

		List<Neuron> sortedWinners = som.getWinningNeuronsByInput(vTest); 
		assertEquals(winner, sortedWinners.get(0));
		assertEquals(som.getNeurons().get(1), sortedWinners.get(1));

		v1.setValue(0, -2);
		v1.setValue(1, -2);

		v2.setValue(0, -5);
		v2.setValue(1, -5);

		vTest.setValue(0, -3);
		vTest.setValue(1, -3);

		winner = som.getWinningNeuronByInput(vTest);
		assertEquals(winner, som.getNeuron(0));

		winner = som.getWinningNeuronByOutput(vTest);
		assertEquals(winner, som.getNeuron(0));

		sortedWinners = som.getWinningNeuronsByInput(vTest); 
		assertEquals(winner, sortedWinners.get(0));
		assertEquals(som.getNeurons().get(1), sortedWinners.get(1));

		v1.setValue(0, 6);
		v1.setValue(1, 5);

		v2.setValue(0, 1);
		v2.setValue(1, 3);

		vTest.setValue(0, 2);
		vTest.setValue(1, 3);

		winner = som.getWinningNeuronByInput(vTest);
		assertEquals(winner, som.getNeuron(1));

		winner = som.getWinningNeuronByOutput(vTest);
		assertEquals(winner, som.getNeuron(1));

		sortedWinners = som.getWinningNeuronsByInput(vTest); 
		assertEquals(winner, sortedWinners.get(0));
		assertEquals(som.getNeurons().get(1), sortedWinners.get(0));
	}

	@Test
	public void testMatrixSOM() {
		WeightVector v = new WeightVector(2);
		v.setValue(0, 1);
		v.setValue(1, 1);

		MatrixSOM mSOM = null;
		try {
			mSOM = new MatrixSOM(2, v, 2, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Neuron n1 = mSOM.getNeurons().get(2);

		v.setValue(0, 0);
		v.setValue(1, 1);
		Neuron n2 = mSOM.getNeuron(v);
		assertEquals(n1, n2);

		v.setValue(0, 1);
		v.setValue(1, 1);
		n1 = mSOM.getNeurons().get(3);
		n2 = mSOM.getNeuron(v);
		assertEquals(n1, n2);
	}
}

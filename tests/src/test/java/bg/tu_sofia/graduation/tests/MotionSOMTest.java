package bg.tu_sofia.graduation.tests;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import bg.tu_sofia.graduiation.robot.motion.som.MotionSOM;
import bg.tu_sofia.graduiation.som.Neuron;
import bg.tu_sofia.graduiation.som.SOMType;
import bg.tu_sofia.graduiation.som.WeightVector;

public class MotionSOMTest {

	@Test
	public void Motion2DSOMConstructorTest() {
		WeightVector v1 = new WeightVector(1);
		v1.setValue(0, 3);

		WeightVector v2 = new WeightVector(2);
		v2.setValue(0, 1);
		v2.setValue(1, 2);

		MotionSOM som = null;

		try {
			som = new MotionSOM(SOMType.LINEAR, 2, v1, 1, SOMType.MATRIX, v2, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (Neuron n : som.getLevel1SOM().getNeurons()) {
			assertNotNull(n.getAssociatedSOM());
		}
	}

	@Test
	public void getDirectionTest() {
		WeightVector v1 = new WeightVector(1);
		v1.setValue(0, 1);

		MotionSOM som = null;
		try {
			som = new MotionSOM(SOMType.LINEAR, 2, v1, 1, SOMType.LINEAR, v1, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// level 1 som
		List<Neuron> l1Neurons = som.getLevel1SOM().getNeurons();
		WeightVector v = l1Neurons.get(0).getInputVector();
		v.setValue(0, 2);
		v.setValue(1, 1);

		v = l1Neurons.get(1).getInputVector();
		v.setValue(0, 1);
		v.setValue(1, 2);

		// level 2 som 1
		List<Neuron> l2Neurons = l1Neurons.get(0).getAssociatedSOM().getNeurons();
		v = l2Neurons.get(0).getInputVector();		
		v.setValue(0, -1);
		v.setValue(1, -2);
		WeightVector vOut = new WeightVector(2);
		l2Neurons.get(0).setOutputVector(vOut);
		vOut.setValue(0, 1);
		vOut.setValue(0, 0);

		v = l2Neurons.get(1).getInputVector();
		v.setValue(0, -2);
		v.setValue(1, -1);
		l2Neurons.get(1).setOutputVector(vOut);
		vOut.setValue(0, 2);
		vOut.setValue(0, 0);

		// level 2 som 2
		l2Neurons = l1Neurons.get(1).getAssociatedSOM().getNeurons();
		v = l2Neurons.get(0).getInputVector();
		v.setValue(0, 3);
		v.setValue(1, 4);
		l2Neurons.get(0).setOutputVector(vOut);
		vOut.setValue(0, 3);
		vOut.setValue(0, 0);

		v = l2Neurons.get(1).getInputVector();
		v.setValue(0, 4);
		v.setValue(1, 3);
		l2Neurons.get(1).setOutputVector(vOut);
		vOut.setValue(0, 4);
		vOut.setValue(0, 0);

		// input data
		WeightVector inputL1 = new WeightVector(2);
		inputL1.setValue(0, 4);
		inputL1.setValue(1, 1);

		WeightVector inputL2 = new WeightVector(2);
		inputL2.setValue(0, -0.5);
		inputL2.setValue(1, -5);

		WeightVector direction = new WeightVector(2);
		direction.setValue(0, 1);
		direction.setValue(0, 0);

		Neuron winner = l1Neurons.get(0).getAssociatedSOM().getNeurons().get(0);
		assertEquals(true, WeightVector.areEqual(winner.getOutputVector(), direction));

		/////
		inputL1.setValue(0, 1);
		inputL1.setValue(1, 4);

		inputL2.setValue(0, -0.5);
		inputL2.setValue(1, -5);

		direction = new WeightVector(2);
		direction.setValue(0, 3);
		direction.setValue(0, 0);

		winner = l1Neurons.get(0).getAssociatedSOM().getNeurons().get(0);
		assertEquals(true, WeightVector.areEqual(winner.getOutputVector(), direction));

	}
}

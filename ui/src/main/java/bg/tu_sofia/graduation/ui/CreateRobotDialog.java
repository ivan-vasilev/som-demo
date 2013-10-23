package bg.tu_sofia.graduation.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.geotools.geometry.jts.JTSFactoryFinder;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import bg.tu_sofia.graduation.robot.Simple2DRobot;
import bg.tu_sofia.graduation.robot.motion.Simple2DSensors;
import bg.tu_sofia.graduiation.Nature;
import bg.tu_sofia.graduiation.NatureObjectType;
import bg.tu_sofia.graduiation.robot.Robot;
import bg.tu_sofia.graduiation.robot.motion.som.MotionSOM;
import bg.tu_sofia.graduiation.som.SOMType;
import bg.tu_sofia.graduiation.som.WeightVector;

public class CreateRobotDialog extends JDialog {

	private static final long serialVersionUID = 7598469655303961052L;

	Nature nature = null;
	private JTextField id;
	private JTextField sensorsCount;
	private JTextField sensorsMaxDistance;
	private JTextField somLevel1Cols;
	private JTextField somLevel1Rows;
	private JTextField somLevel2Cols;
	private JTextField somNeuronDistance;
	private JTextField robotWidth;
	private JTextField robotHeight;

	private int selectedOption = JOptionPane.CANCEL_OPTION;

	public CreateRobotDialog(JFrame frame) {
		super(frame, "Create robot", true);
		setSize(400, 400);

		// Center the dialog
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		setLocation((screenWidth - getWidth()) / 2,
				(screenHeight - getHeight()) / 2);

		Container container = getContentPane();
		container.setLayout(new GridLayout(10, 5, 5, 10));
		((JComponent) container).setBorder(new TitledBorder(new EtchedBorder(),
				"Robot"));

		id = new JTextField(10);
		JLabel idLabel = new JLabel("ID");
		container.add(idLabel);
		container.add(id);

		sensorsCount = new JTextField(10);
		JLabel sensorsCountLabel = new JLabel("Sensors count");
		container.add(sensorsCountLabel);
		container.add(sensorsCount);

		sensorsMaxDistance = new JTextField(10);
		sensorsMaxDistance.setText("");
		JLabel sensorsMaxDistanceLabel = new JLabel("Sensors maximum distance");
		container.add(sensorsMaxDistanceLabel);
		container.add(sensorsMaxDistance);

		somLevel1Cols = new JTextField(10);
		somLevel1Cols.setText("");
		JLabel somLevel1ColsLabel = new JLabel("SOM level one grid columns");
		container.add(somLevel1ColsLabel);
		container.add(somLevel1Cols);

		somLevel1Rows = new JTextField(10);
		somLevel1Rows.setText("");
		JLabel somLevel1RowsLabel = new JLabel("SOM level one grid rows");
		container.add(somLevel1RowsLabel);
		container.add(somLevel1Rows);

		somLevel2Cols = new JTextField(10);
		somLevel2Cols.setText("");
		JLabel somLevel2ColsLabel = new JLabel("SOM level two neurons count");
		container.add(somLevel2ColsLabel);
		container.add(somLevel2Cols);

		somNeuronDistance = new JTextField(10);
		somNeuronDistance.setText("");
		JLabel somNeuronDistanceLabel = new JLabel("SOM neuron distance");
		container.add(somNeuronDistanceLabel);
		container.add(somNeuronDistance);

		robotWidth = new JTextField(10);
		robotWidth.setText("");
		JLabel widthLabel = new JLabel("Robot width");
		container.add(widthLabel);
		container.add(robotWidth);

		robotHeight = new JTextField(10);
		robotHeight.setText("");
		JLabel heightLabel = new JLabel("Robot height");
		container.add(heightLabel);
		container.add(robotHeight);

		JButton buttonOK = new JButton("OK");
		buttonOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isDataCorrect()) {
					selectedOption = JOptionPane.OK_OPTION;
					setVisible(false);
				} else {
					JOptionPane.showMessageDialog(getParent(),
							"Please enter correct data", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		container.add(buttonOK);

		JButton buttonCancel = new JButton("Cancel");
		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				selectedOption = JOptionPane.CANCEL_OPTION;
			}
		});
		container.add(buttonCancel);

		container.invalidate();
	}

	public String getId() {
		return id.getText();
	}

	public int getSensorsCount() {
		return Integer.valueOf(sensorsCount.getText());
	}

	public double getSensorsMaxDistance() {
		return Double.valueOf(sensorsMaxDistance.getText());
	}

	public int getSomLevel1Cols() {
		return Integer.valueOf(somLevel1Cols.getText()) - 1;
	}

	public int getSomLevel1Rows() {
		return Integer.valueOf(somLevel1Rows.getText()) - 1;
	}

	public int getSomLevel2Cols() {
		return Integer.valueOf(somLevel2Cols.getText()) - 1;
	}

	public double getSomNeuronDistance() {
		return Double.valueOf(somNeuronDistance.getText());
	}

	public double getRobotWidth() {
		return Double.valueOf(robotWidth.getText());
	}

	public double getRobotHeight() {
		return Double.valueOf(robotHeight.getText());
	}

	public int getSelectedOption() {
		return selectedOption;
	}

	private boolean isDataCorrect() {
		try {
			if (id.getText().equals("")) {
				return false;
			}

			Integer sensors = Integer.valueOf(sensorsCount.getText());
			Double maxDist = Double.valueOf(sensorsMaxDistance.getText());
			Integer l1Cols = Integer.valueOf(somLevel1Cols.getText());
			Integer l1Rows = Integer.valueOf(somLevel1Rows.getText());
			Integer l2Cols = Integer.valueOf(somLevel2Cols.getText());
			Double neuronDist = Double.valueOf(somNeuronDistance.getText());
			Double rWidth = Double.valueOf(robotWidth.getText());
			Double rHeight = Double.valueOf(robotHeight.getText());

			if ((sensors <= 0) || (maxDist <= 0) || ((l1Cols - 1) <= 1)
					|| ((l1Rows - 1) <= 1) || ((l2Cols - 1) <= 1)
					|| (neuronDist <= 0) || (rWidth <= 0) || (rHeight <= 0)) {
				return false;
			}
		} catch (NumberFormatException e) {
			return false;
		} catch (NullPointerException e) {
			return false;
		}

		return true;
	}

	public Robot createRobot(Nature nature) {
		Robot robot = null;
		if (isDataCorrect() && (nature != null)) {
			robot = new Simple2DRobot();
			robot.setId(getId());

			MotionSOM som = null;
			try {
				WeightVector somDimL1 = new WeightVector(2);
				somDimL1.setValue(0, getSomLevel1Cols());
				somDimL1.setValue(1, getSomLevel1Rows());

				WeightVector somDimL2 = new WeightVector(1);
				somDimL2.setValue(0, getSomLevel2Cols());

				som = new MotionSOM(SOMType.MATRIX, getSensorsCount(),
						somDimL1, getSomNeuronDistance(), SOMType.LINEAR,
						somDimL2, getSomNeuronDistance());
			} catch (Exception e) {
				e.printStackTrace();
			}

			robot.setMotionStrategy(som);

			Polygon shape = null;

			GeometryFactory geometryFactory = JTSFactoryFinder
					.getGeometryFactory(null);
			WKTReader reader = new WKTReader(geometryFactory);
			try {
				double width = getRobotWidth();
				double height = getRobotHeight();
				shape = (Polygon) reader.read("POLYGON((0 0, " + width + " 0, "
						+ width + " " + height + ", 0 " + height + ", 0 0))");
				shape.setUserData(NatureObjectType.ROBOT);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			robot.setShape(shape);
			Point sensorCoords = shape.getCentroid();
			robot.setNature(nature);
			Simple2DSensors sensors = new Simple2DSensors(getSensorsCount(),
					getSensorsMaxDistance(), new Coordinate(
							sensorCoords.getX(), sensorCoords.getY()), nature,
					robot);
			robot.setSensors(sensors);

			return robot;
		}

		return null;
	}
}

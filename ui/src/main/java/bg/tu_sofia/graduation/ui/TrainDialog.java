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

public class TrainDialog extends JDialog {

	private static final long serialVersionUID = 5707783074912969556L;

	private JTextField iterations;
	private JTextField initLearningParam;
	private JTextField initNeighbourhoodParam;
	private int selectedOption = JOptionPane.CANCEL_OPTION;

	public TrainDialog(JFrame frame) {
		super(frame, "Create trainer", true);
		setSize(300, 190);

		// Center the dialog
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		setLocation((screenWidth - getWidth()) / 2,
				(screenHeight - getHeight()) / 2);

		Container container = getContentPane();
		container.setLayout(new GridLayout(4, 2, 5, 10));
		((JComponent) container).setBorder(new TitledBorder(new EtchedBorder(),
				"Training parameters"));

		iterations = new JTextField(10);
		iterations.getSize().width = 30;
		iterations.getSize().height = 10;
		JLabel maxIterationLabel = new JLabel("Iterations");
		container.add(maxIterationLabel);
		container.add(iterations);

		initLearningParam = new JTextField(10);
		initLearningParam.setText("1.0");
		JLabel initLearningRateLabel = new JLabel("Initial Learning Rate");
		container.add(initLearningRateLabel);
		container.add(initLearningParam);

		initNeighbourhoodParam = new JTextField(10);
		initNeighbourhoodParam.setText("1.0");
		JLabel initDistanceRateLabel = new JLabel("Initial Distance Rate");
		container.add(initDistanceRateLabel);
		container.add(initNeighbourhoodParam);

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
	}

	public int getIterations() {
		return Integer.valueOf(iterations.getText());
	}

	public double getInitLearningParam() {
		return Double.valueOf(initLearningParam.getText());
	}

	public double getInitNeigbourhoodParam() {
		return Double.valueOf(initNeighbourhoodParam.getText());
	}

	public int getSelectedOption() {
		return selectedOption;
	}

	private boolean isDataCorrect() {
		try {
			Double neighbourParam = Double.valueOf(initNeighbourhoodParam
					.getText());
			Double learningParam = Double.valueOf(initLearningParam.getText());
			Integer iter = Integer.valueOf(iterations.getText());

			if ((neighbourParam <= 0) || (learningParam <= 0) || (iter <= 0)) {
				return false;
			}
		} catch (NumberFormatException e) {
			return false;
		} catch (NullPointerException e) {
			return false;
		}

		return true;
	}

}

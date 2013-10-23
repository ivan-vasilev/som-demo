package bg.tu_sofia.graduation.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import bg.tu_sofia.graduiation.robot.Robot;

public class SelectRobotGoalDialog extends JDialog {

	private static final long serialVersionUID = 8366652136411046362L;
	private JComboBox robotsListCombo;
	private int selectedOption = JOptionPane.CANCEL_OPTION;

	public SelectRobotGoalDialog(JFrame frame, List<Robot> robots) {
		super(frame, "Select goal dialog", true);
		setSize(380, 130);

		// Center the dialog
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		setLocation((screenWidth - getWidth()) / 2,
				(screenHeight - getHeight()) / 2);

		Container container = getContentPane();
		container.setLayout(new GridLayout(2, 2, 5, 10));
		((JComponent) container).setBorder(new TitledBorder(new EtchedBorder(),
				"Select robot or choose from the map"));

		robotsListCombo = new JComboBox();
		robotsListCombo.setPreferredSize(new Dimension(120, 26));
		robotsListCombo.getSize().height = 26;
		robotsListCombo.getSize().width = 120;
		for (Robot r : robots) {
			robotsListCombo.addItem(r);
		}

		JLabel selectRobotLabel = new JLabel("Select Robot");
		container.add(selectRobotLabel);
		container.add(robotsListCombo);

		JButton buttonOK = new JButton("OK");
		buttonOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedOption = JOptionPane.OK_OPTION;
				setVisible(false);
			}
		});
		container.add(buttonOK);

		JButton buttonSelectAnotherGoal = new JButton(
				"Select goal from the map");
		buttonSelectAnotherGoal.setPreferredSize(new Dimension(200, 26));
		buttonSelectAnotherGoal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				selectedOption = JOptionPane.CANCEL_OPTION;
			}
		});

		container.add(buttonSelectAnotherGoal);
	}

	public int getSelectedOption() {
		return selectedOption;
	}

	public Robot getSelectedRobot() {
		return (Robot) robotsListCombo.getSelectedItem();
	}
}

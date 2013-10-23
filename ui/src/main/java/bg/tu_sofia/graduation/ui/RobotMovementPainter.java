package bg.tu_sofia.graduation.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.geotools.geometry.jts.JTSFactoryFinder;

import bg.tu_sofia.graduation.Simple2DNature;
import bg.tu_sofia.graduiation.Nature;
import bg.tu_sofia.graduiation.NatureObjectType;
import bg.tu_sofia.graduiation.robot.Robot;
import bg.tu_sofia.graduiation.robot.motion.som.Bounds2DFilter;
import bg.tu_sofia.graduiation.robot.motion.som.MotionSOM;
import bg.tu_sofia.graduiation.robot.motion.som.MotionTrainer;
import bg.tu_sofia.graduiation.som.Trainer;
import bg.tu_sofia.graduiation.som.WeightVector;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class RobotMovementPainter {

	private GeometryPainter painter = new GeometryPainter();
	private Nature nature;
	private JFrame frame;
    private JPanel contentPane;
    private JMenuBar menuBar;
    private JButton startButton;
    private JButton stopButton;
    private JButton trainButton;
    private JButton restartButton;
    private JButton createButton;
    private JButton removeButton;
    private JComboBox robotsListCombo;
    private StatusBar statusBar;
	private ReentrantLock lock = new ReentrantLock();
	private MotionTrainer motion2DTrainer;
	private TrainingMouseListener mouseListener;

	public class TrainingMouseListener implements MouseListener {
		public TrainingMouseListener() {
			super();
		}

		private int iterations = 1;
		private boolean trainingMode = false;
		private boolean spawnPositionMode = false;
		private boolean selectGoalMode = false;

		public void startTraining() {
			Robot r = (Robot) robotsListCombo.getSelectedItem();
			if (r != null) {
				trainingMode = true;
				selectGoalMode = false;
				spawnPositionMode = false;
				start();
				motion2DTrainer.moveToRandomPosition();
				painter.repaint(true);
				statusBar.setMessage("Iteration 1: Choose direction");
			}
		}

		public void startSpawnPosition() {
			Robot r = (Robot) robotsListCombo.getSelectedItem();
			if (r != null) {
				trainingMode = false;
				selectGoalMode = false;
				spawnPositionMode = true;
				start();
				statusBar.setMessage("Select initial robot position");
			}
		}

		public void startSelectGoal() {
			Robot r = (Robot) robotsListCombo.getSelectedItem();
			if (r != null) {
				trainingMode = true;
				spawnPositionMode = false;
				selectGoalMode = true;
				start();
				statusBar.setMessage("Select goal");
			}
		}

		private void start() {
			Robot r = (Robot) robotsListCombo.getSelectedItem();
			if (r != null) {
				iterations = 0;
				robotsListCombo.setEnabled(false);
				startButton.setEnabled(false);
				restartButton.setEnabled(false);
				trainButton.setEnabled(false);
				nature.removeObject(r.getShape());
			}
		}

		public void stopAll() {
			iterations = 0;
			trainingMode = false;
			spawnPositionMode = false;
			selectGoalMode = false;
			robotsListCombo.setEnabled(true);
			startButton.setEnabled(true);
			restartButton.setEnabled(true);
			trainButton.setEnabled(true);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (spawnPositionMode) {
				robotSpawnProcedure(e);
			} else if (trainingMode) {
				trainingProcedure(e);
			} else if (selectGoalMode) {
				selectGoalFromMapProcedure(e);
			}
		}

		private void selectGoalProcedure() {
			List<Robot> robots = new ArrayList<Robot>();
			Robot r = (Robot) robotsListCombo.getSelectedItem();
			if (r != null) {
				for (int i = 0; i < robotsListCombo.getItemCount(); i++) {
					if (r != robotsListCombo.getItemAt(i)) {
						robots.add((Robot) robotsListCombo.getItemAt(i));
					}
				}

				int selectedOption = JOptionPane.CANCEL_OPTION;
				SelectRobotGoalDialog dialog = null;
				if (robots.size() >= 1) {
					dialog = new SelectRobotGoalDialog(frame, robots);
					dialog.setVisible(true);
					selectedOption = dialog.getSelectedOption();
				}

				statusBar.setMessage("Select goal");

				if (selectedOption == JOptionPane.OK_OPTION) {	
					Robot goalRobot = dialog.getSelectedRobot();
					if (goalRobot != null) {
						GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
						Point goal = geometryFactory.createPoint(goalRobot.getSensors().getCoordinates());
						goal.setUserData(NatureObjectType.GOAL_UNREACHED);
						r.getGoals().clear();
						r.setMoveCount(0);
						r.setCollisionCount(0);
						r.addGoal(goal);
						nature.addObject(r.getShape());

						stopAll();
						populatePainter();
						painter.repaint(true);

						if (!r.isStarted()) {
							r.start();
						}

						statusBar.setMessage("Goal selected: " + goalRobot);
					}
				} else if (selectedOption == JOptionPane.CANCEL_OPTION) {
					stopAll();
					spawnPositionMode = false;
					trainingMode = false;
					selectGoalMode = true;
				}
			}
		}

		private void selectGoalFromMapProcedure(MouseEvent e) {
			Robot r = (Robot) robotsListCombo.getSelectedItem();

			if (r != null) {
				AffineTransform transform = painter.getAffineTransform();

				double x = e.getX() / transform.getScaleX();
				double y = e.getY() / transform.getScaleY();

				GeometryFactory geometryFactory = JTSFactoryFinder
						.getGeometryFactory(null);
				Point goal = geometryFactory.createPoint(new Coordinate(x, y));
				r.setMoveCount(0);
				r.setCollisionCount(0);
				r.getGoals().clear();
				goal.setUserData(NatureObjectType.GOAL_UNREACHED);
				r.addGoal(goal);
				nature.addObject(r.getShape());

				stopAll();
				populatePainter();
				painter.repaint(true);

				if (!r.isStarted()) {
					r.start();
				}

				statusBar.setMessage("Goal selected with coordiantes X: " + x
						+ ", Y " + y);
			}
		}

		private void trainingProcedure(MouseEvent e) {
			try {
				Robot r = (Robot) robotsListCombo.getSelectedItem();
	
				if (r != null) {
					AffineTransform transform = painter.getAffineTransform();
					double x = e.getX() / transform.getScaleX();
					double y = e.getY() / transform.getScaleY();
	
					WeightVector outputVector = r.getDirectionToCoordinate(new Coordinate(x, y));
					WeightVector vectorToGoal = r.getVectorToCurrentGoal();
					WeightVector distances = r.getSensors().getDistances();
	
					motion2DTrainer.trainLevel2SOM(distances, vectorToGoal, outputVector);
					motion2DTrainer.moveToRandomPosition();
					painter.getTraces().clear();
					painter.repaint(true);
	
					iterations++;
					statusBar.setMessage("Iteration " + iterations + ": Choose direction");
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			} finally {
				if (iterations >= motion2DTrainer.getLevel1Trainer().getMaxIterations()) {
					statusBar.setMessage("Training completed. Select robot spawn position");
					spawnPositionMode = true;
					trainingMode = false;
					selectGoalMode = false;
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		private void robotSpawnProcedure(MouseEvent e) {
			Robot r = (Robot) robotsListCombo.getSelectedItem();
			if (r != null) {
				AffineTransform transform = painter.getAffineTransform();

				double x = e.getX() / transform.getScaleX();
				double y = e.getY() / transform.getScaleY();

				WeightVector direction = r.getVectorToCoordinate(new Coordinate(x, y));
	
				if (r.move(direction)) {
					painter.repaint(true);
					selectGoalProcedure();						
				}
			}
		}
	}

	public RobotMovementPainter() {
		super();
		frame = new JFrame("Robot Motion");
		frame.setPreferredSize(new Dimension(800, 600));
		frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });

        frame.pack();
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setBackground(Color.WHITE);
		frame.validate();

        menuBar = createMenuBar();
        frame.setJMenuBar(menuBar);

        contentPane = createContentPane();
        frame.setContentPane(contentPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.validate();
		painter.start();
	}

	public void setVisible(boolean b) {
		frame.setVisible(b);
	}

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();;
        JMenu menuFile = new JMenu("File");
        menuFile.setMnemonic(KeyEvent.VK_F);
        menuFile.getAccessibleContext().setAccessibleDescription("Load and Save robots");
        menuBar.add(menuFile);

        JMenuItem menuItemChangeNature= new JMenuItem("Change Nature", KeyEvent.VK_C);
        menuItemChangeNature.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItemChangeNature.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
        menuItemChangeNature.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"Shape files (*.shp)", "shp");
				fc.setFileFilter(filter);

				int returnVal = fc.showOpenDialog(frame);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						List<Geometry> obstacles = Util.getShapeFileContent(frame, fc.getSelectedFile().getAbsolutePath());

						Simple2DNature n = new Simple2DNature(obstacles);
						changeNature(n);
					} catch (FileNotFoundException e1) {
						JOptionPane.showMessageDialog(frame, "File not found",
								"Error", JOptionPane.ERROR_MESSAGE);
						return;
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(frame, "Error loading file",
								"Error", JOptionPane.ERROR_MESSAGE);

						return;
					}
					statusBar.setMessage("Nature changed");
				}
				statusBar.setMessage("Nature not changed");
			}
		});
        menuFile.add(menuItemChangeNature);

        JMenuItem menuItemLoad = new JMenuItem("Load", KeyEvent.VK_L);
        menuItemLoad.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItemLoad.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
        menuItemLoad.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (nature != null) {
					JFileChooser fc = new JFileChooser();
					int returnVal = fc.showOpenDialog(frame);

					if (returnVal == JFileChooser.APPROVE_OPTION) {
						Robot robot = null;

						try {
							robot = Robot.loadFromFile(fc.getSelectedFile()
									.getAbsolutePath());
							if (!robotIdExist(robot.getId())) {
								lock.lock();
								addRobot(robot);
								statusBar.setMessage("Robot added");
								mouseListener.startSpawnPosition();
							} else {
								JOptionPane.showMessageDialog(frame,
										"Robot with this ID already exists",
										"Warning", JOptionPane.WARNING_MESSAGE);
							}
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(frame,
									"Error loading robot", "Error",
									JOptionPane.ERROR_MESSAGE);
							return;
						} catch (ClassNotFoundException e1) {
							JOptionPane.showMessageDialog(frame,
									"Error loading robot", "Error",
									JOptionPane.ERROR_MESSAGE);
							return;
						} catch (Exception e2) {
							return;
						}
					}
				} else {
					JOptionPane.showMessageDialog(frame, "Nature not loaded",
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
        menuFile.add(menuItemLoad);

        JMenuItem menuItemSave = new JMenuItem("Save", KeyEvent.VK_S);
        menuItemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.ALT_MASK));
        menuItemSave.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
        menuItemSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Robot robot = (Robot) robotsListCombo.getSelectedItem();

				if (robot != null) {
					JFileChooser fc = new JFileChooser();
					int returnVal = fc.showSaveDialog(frame);

					if (returnVal == JFileChooser.APPROVE_OPTION) {

						try {
							Robot.saveToFile(robot, fc.getSelectedFile()
									.getAbsolutePath());
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(frame,
									"Error saving robot", "Error",
									JOptionPane.ERROR_MESSAGE);
							return;
						} catch (Exception e2) {
							return;
						}
						statusBar.setMessage("Robot " + robot.getId()
								+ " saved");
					}
				} else {
					JOptionPane.showMessageDialog(frame, "No robots loaded",
							"Warning", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
        menuFile.add(menuItemSave);

        menuFile.addSeparator();

        JMenuItem menuItemExit = new JMenuItem("Exit", KeyEvent.VK_X);
        menuItemExit.getAccessibleContext().setAccessibleDescription("Exit");
        menuItemExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
                int exit = JOptionPane.showConfirmDialog(frame, "Confirm exit?", "Exit",  JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if (exit == JOptionPane.YES_OPTION) {
                	System.exit(0);
                }
			}});

        menuFile.add(menuItemExit);

        return menuBar;
    }

    private JPanel createContentPane() {
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setOpaque(false);

        painter = new GeometryPainter();
        painter.setBackground(Color.WHITE);
        painter.setOpaque(true);
		painter.paintStaticObjects(true);
		painter.setLock(lock);

		mouseListener = new TrainingMouseListener();
		painter.addMouseListener(mouseListener);
		contentPane.add(painter, BorderLayout.CENTER);
        JPanel bottomBar = createBottomBar();

        painter.addComponentListener(new ComponentListener()
        {
			@Override
			public void componentHidden(ComponentEvent e) {
				painter.repaint(true);
			}

			@Override
			public void componentMoved(ComponentEvent e) {
			}

			@Override
			public void componentResized(ComponentEvent e) {
				AffineTransform transform = painter.getAffineTransform();
				if ((nature != null) && (transform != null)) {
					calculateScale(nature, painter.getAffineTransform());
					painter.repaint(true);
				}
			}

			@Override
			public void componentShown(ComponentEvent e) {
				painter.repaint(true);
			}
        });

        contentPane.add(bottomBar, BorderLayout.SOUTH);
        painter.repaint(true);

        return contentPane;
    }

    private JPanel createBottomBar() {
    	JPanel bottomPane = new JPanel(new BorderLayout());

    	JPanel buttonPane = new JPanel();
    	buttonPane.setBorder(BorderFactory.createTitledBorder("Control"));

        ImageIcon startButtonIcon = new ImageIcon("");

    	startButton = new JButton("Start", startButtonIcon);
    	startButton.setPreferredSize(new Dimension(100,26));
    	startButton.setVerticalTextPosition(AbstractButton.CENTER);
    	startButton.setHorizontalTextPosition(AbstractButton.LEADING);
    	startButton.setMnemonic(KeyEvent.VK_S);
    	startButton.setActionCommand("start");
    	startButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {

				lock.lock();
				if (lock.isHeldByCurrentThread()) {
					while (lock.getHoldCount() > 0) {
						lock.unlock();
					}

					statusBar.setMessage("Running");
				}
			}});

    	buttonPane.add(startButton);

    	stopButton = new JButton("Stop", startButtonIcon);
    	stopButton.setVerticalTextPosition(AbstractButton.CENTER);
    	stopButton.setPreferredSize(new Dimension(100,26));
    	stopButton.setMnemonic(KeyEvent.VK_T);
    	stopButton.setActionCommand("stop");
    	stopButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				lock.lock();
				mouseListener.stopAll();
				statusBar.setMessage("Stopped");
			}
		});

    	buttonPane.add(stopButton);

    	buttonPane.add(Box.createHorizontalStrut(10));
    	buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
    	buttonPane.add(Box.createHorizontalStrut(5));
    	JLabel robotsLabel = new JLabel("Selected Robot", JLabel.CENTER);
    	robotsLabel.setVerticalTextPosition(JLabel.BOTTOM);
    	robotsLabel.setHorizontalTextPosition(JLabel.CENTER);
    	buttonPane.add(robotsLabel);

        robotsListCombo = new JComboBox();
        robotsListCombo.setPreferredSize(new Dimension(120, 26));
        buttonPane.add(robotsListCombo);

    	trainButton = new JButton("Train", startButtonIcon);
    	trainButton.setVerticalTextPosition(AbstractButton.CENTER);
    	trainButton.setPreferredSize(new Dimension(90,26));
    	trainButton.setMnemonic(KeyEvent.VK_A);
    	trainButton.setActionCommand("train");
    	trainButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				lock.lock();
				Robot r = (Robot) robotsListCombo.getSelectedItem();
				if (r != null) {
					if (!r.allGoalsReached()) {
						TrainDialog dialog = new TrainDialog(frame);
						dialog.setVisible(true);

						int selectedOption = dialog.getSelectedOption();
						MotionSOM som = (MotionSOM) r.getMotionStrategy();
						if (selectedOption == JOptionPane.OK_OPTION) {

							Trainer trainer = new Trainer(dialog
									.getIterations(), dialog
									.getInitLearningParam(), dialog
									.getInitNeigbourhoodParam());
							motion2DTrainer = new MotionTrainer(som, trainer,
									r.getNature(), r);
							mouseListener.startTraining();
						}
					} else {
						JOptionPane.showMessageDialog(frame,
								"No goals are set to robot", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(frame, "No robots loaded",
							"Warning", JOptionPane.WARNING_MESSAGE);
				}
			}
		}); 
    	buttonPane.add(trainButton);

    	restartButton = new JButton("Restart", startButtonIcon);
    	restartButton.setVerticalTextPosition(AbstractButton.CENTER);
    	restartButton.setPreferredSize(new Dimension(90,26));
    	restartButton.setMnemonic(KeyEvent.VK_R);
    	restartButton.setActionCommand("restart");
    	restartButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				lock.lock();
				Robot r = (Robot) robotsListCombo.getSelectedItem();
				if (r != null) {
					populatePainter();
					painter.repaint(true);

					mouseListener.startSpawnPosition();
				} else {
					JOptionPane.showMessageDialog(frame, "No robots loaded",
							"Warning", JOptionPane.WARNING_MESSAGE);
				}
			}
		}); 
    	buttonPane.add(restartButton);

    	createButton = new JButton("Create", startButtonIcon);
    	createButton.setVerticalTextPosition(AbstractButton.CENTER);
    	createButton.setPreferredSize(new Dimension(90,26));
    	createButton.setMnemonic(KeyEvent.VK_C);
    	createButton.setActionCommand("create");
    	createButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				lock.lock();

				CreateRobotDialog dialog = new CreateRobotDialog(frame);
				dialog.setVisible(true);

				int selectedOption = dialog.getSelectedOption();
				if (selectedOption == JOptionPane.OK_OPTION) {
					Robot r = dialog.createRobot(nature);
					if (!robotIdExist(r.getId())) {
						addRobot(r);
					} else {
						JOptionPane.showMessageDialog(frame, "Robot with this ID already exists",
								"Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}); 
    	buttonPane.add(createButton);

    	removeButton = new JButton("Remove", startButtonIcon);
    	removeButton.setVerticalTextPosition(AbstractButton.CENTER);
    	removeButton.setPreferredSize(new Dimension(90,26));
    	removeButton.setMnemonic(KeyEvent.VK_M);
    	removeButton.setActionCommand("remove");
    	removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				lock.lock();
				Robot r = (Robot) robotsListCombo.getSelectedItem();
				if (r != null) {
					int exit = JOptionPane.showConfirmDialog(frame,
							"Remove selected robot?", "Remove robot",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.INFORMATION_MESSAGE);
					if (exit == JOptionPane.YES_OPTION) {
				    	mouseListener.stopAll();
						removeRobot(r);
						statusBar.setMessage("Robot " + r.getId() + " removed");
					}
				} else {
					JOptionPane.showMessageDialog(frame, "No robots loaded",
							"Warning", JOptionPane.WARNING_MESSAGE);
				}
			}
		}); 
    	buttonPane.add(removeButton);

        bottomPane.add(buttonPane, BorderLayout.CENTER);

        statusBar = new StatusBar();

        bottomPane.add(statusBar, BorderLayout.SOUTH);

    	return bottomPane;
    }


	public GeometryPainter getPainter() {
		return painter;
	}

	public void setPainter(GeometryPainter painter) {
		this.painter = painter;
	}

	public Nature getNature() {
		return nature;
	}

	private void clearAll() {
		for (int i = 0; i < robotsListCombo.getItemCount(); i++) {
			Robot r = (Robot) robotsListCombo.getItemAt(i);
			r.stop();
		}

		robotsListCombo.removeAllItems();
		painter.eraseAllObjects();
	}

	public void changeNature(Nature nature) {
		this.nature = nature;
		clearAll();

		AffineTransform affineTransform = new AffineTransform();
		calculateScale(nature, affineTransform);
		painter.setAffineTransform(affineTransform);
		populatePainter();
		painter.repaint(true);
	}

	public void populatePainter() {
		painter.eraseAllObjects();

		List<Geometry> obstacles = nature.getObjects();
		for (Geometry g : obstacles) {
			if ((g.getUserData() != null) && (g.getUserData().getClass() != NatureObjectType.class)) {
				g.setUserData(NatureObjectType.STATIC_OBJECT);
			}
			painter.addDrawableObject(g);
		}

		for (int i = 0; i < robotsListCombo.getItemCount(); i++) {
			Robot r = (Robot) robotsListCombo.getItemAt(i);

			painter.addDrawableObject(r.getShape());
			for (Geometry g : r.getGoals()) {
				painter.addDrawableObject(g);
			}
		}
	}

	public synchronized void addRobot(Robot r) {
		r.setLock(lock);
		r.setSuspender(painter);
		r.setNature(nature);
		robotsListCombo.addItem(r);
		robotsListCombo.setSelectedItem(r);
		painter.addDrawableObject(r.getShape());

		for (Point p : r.getGoals()) {
			painter.addDrawableObject(p);
		}

		r.start();
	}

    public synchronized void removeRobot(Robot r) {
    	r.stop();
    	nature.removeObject(r.getShape());
    	robotsListCombo.removeItem(r);
    	populatePainter();
		painter.repaint(true);
	}

    private boolean robotIdExist(String id) {
		for (int i = 0; i < robotsListCombo.getItemCount(); i++) {
			Robot r = (Robot) robotsListCombo.getItemAt(i);
			if (r.getId().equals(id)) {
				return true;
			}
		}

		return false;
    }

    private void calculateScale(Nature nature, AffineTransform affineTransform) {
		List<Geometry> obstacles = nature.getObjects();
		Bounds2DFilter bf = new Bounds2DFilter();
		for (Geometry g : obstacles) {
		    g.apply(bf);
		}

		Rectangle2D bounds = bf.getBounds();
		
		double width = bounds.getMaxX() - bounds.getMinX();
		double height = bounds.getMaxY() - bounds.getMinY();

		affineTransform.setTransform(((painter.getWidth() - 2) / width), 0, 0, ((painter.getHeight() - 2) / height), affineTransform.getTranslateX(), affineTransform.getTranslateY());
    }

    public static void main(String[] args) {
    	RobotMovementPainter main = new RobotMovementPainter();
    	main.setVisible(true);
    }
}

package bg.tu_sofia.graduation.ui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JPanel;

import org.geotools.geometry.jts.LiteShape;

import sun.java2d.SunGraphics2D;
import bg.tu_sofia.graduiation.NatureObjectType;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public class GeometryPainter extends JPanel implements Runnable {

	private static final long serialVersionUID = 4732868096382350920L;

	private static final Color clrLoOrange = new Color(255, 105, 0);
	private static final Color clrGlowInnerLo = new Color(255, 209, 0);

	List<Geometry> drawableObjects = new ArrayList<Geometry>();
	List<Geometry> traces = new ArrayList<Geometry>();

	HashMap<Geometry, LiteShape> motionObjects = new HashMap<Geometry, LiteShape>();
	private boolean paintStaticObjects = false;
	private AffineTransform affineTransform = new AffineTransform();
	private Thread thread = null;
	private Boolean repaintAll = true;

	private ReentrantLock lock;

	public ReentrantLock getLock() {
		return lock;
	}

	public void setLock(ReentrantLock lock) {
		this.lock = lock;
	}

	public List<Geometry> getTraces() {
		return traces;
	}

	public void setTraces(List<Geometry> traces) {
		this.traces = traces;
	}

	public GeometryPainter() throws HeadlessException {
		super();
	}

	public GeometryPainter(List<Geometry> drawableObjects,
			boolean paintStaticObjects, AffineTransform affineTransform)
			throws HeadlessException {
		super();
		this.drawableObjects = drawableObjects;
		this.paintStaticObjects = paintStaticObjects;
		setBackground(Color.GREEN);
		this.affineTransform = affineTransform;
	}

	public boolean paintStaticObjects(boolean b) {
		return paintStaticObjects = b;
	}

	public AffineTransform getAffineTransform() {
		return affineTransform;
	}

	public void setAffineTransform(AffineTransform affineTransform) {
		this.affineTransform = affineTransform;
	}

	public HashMap<Geometry, LiteShape> getMotionObjects() {
		return motionObjects;
	}

	public void getMotionObjects(HashMap<Geometry, LiteShape> redrawableObjects) {
		this.motionObjects = redrawableObjects;
	}

	@Override
	public void paint(Graphics g) {
		SunGraphics2D g2 = (SunGraphics2D) g;
		if (repaintAll) {
			g2.setPaint(Color.WHITE);
			g2.fill(new Rectangle(0, 0, getWidth(), getHeight()));
			repaintAll = false;
		}

		affineTransform.setTransform(affineTransform.getScaleX(), 0, 0,
				affineTransform.getScaleY(), g2.transform.getTranslateX(),
				g2.transform.getTranslateY());
		g2.transform.setToScale(1, 1);

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_ON);

		for (Geometry geometry : traces) {
			paintGeometry(geometry, g2);
		}

		for (Geometry geometry : drawableObjects) {
			paintGeometry(geometry, g2);
		}
	}

	public void repaint(Boolean repaintAll) {
		this.repaintAll = repaintAll;
		repaint();
	}

	private synchronized void paintGeometry(Geometry geometry,
			SunGraphics2D graphics2D) {
		Object type = geometry.getUserData();

		LiteShape shapeNewPosition = null;
		try {
			shapeNewPosition = new LiteShape(geometry, affineTransform, false);
		} catch (Exception e) {
			return;
		}

		LiteShape shapeOldPosition = motionObjects.get(geometry);
		if (shapeOldPosition != null) {
			if (!shapeOldPosition.getBounds2D().equals(
					shapeNewPosition.getBounds2D())) {
				graphics2D.setPaint(Color.WHITE);
				graphics2D.fill(shapeOldPosition);
				Point p = geometry.getCentroid();
				p.setUserData(NatureObjectType.TRACE);
				traces.add(p);
			}
		}

		if ((type == null) || (!(type instanceof NatureObjectType))) {
			graphics2D.setPaint(Color.BLACK);
			graphics2D.draw(shapeNewPosition);
			return;
		}

		if (NatureObjectType.ROBOT.equals(type)) {
			motionObjects.remove(geometry);
			motionObjects.put(geometry, shapeNewPosition);

			Ellipse2D robotShape = (Ellipse2D) createShape(shapeNewPosition,
					(NatureObjectType) type);
			paintBorderShadow(graphics2D, robotShape, 4);
			graphics2D.setComposite(AlphaComposite.SrcAtop);
			graphics2D.setPaint(clrLoOrange);
			graphics2D.setPaintMode();

			graphics2D.fill(robotShape);

			// paintBorderGlow(graphics2D, robotShape, 2);

			return;
		}

		if (NatureObjectType.STATIC_OBJECT.equals(type) && paintStaticObjects) {
			graphics2D.setPaint(Color.BLACK);
			BasicStroke stroke = new BasicStroke(10);
			graphics2D.setStroke(stroke);
			graphics2D.draw(shapeNewPosition);
			return;
		}

		if (NatureObjectType.GOAL_REACHED.equals(type)) {

			Ellipse2D goalShape = (Ellipse2D) createShape(shapeNewPosition,
					(NatureObjectType) type);
			graphics2D.setPaint(Color.WHITE);
			graphics2D.fill(goalShape);

			return;
		}

		if (NatureObjectType.GOAL_UNREACHED.equals(type)) {
			Ellipse2D goalShape = (Ellipse2D) createShape(shapeNewPosition,
					(NatureObjectType) type);
			paintBorderShadow(graphics2D, goalShape, 4);
			graphics2D.setPaint(clrGlowInnerLo);
			graphics2D.fill(goalShape);

			return;
		}

		if (NatureObjectType.TRACE.equals(type)) {
			Ellipse2D traceShape = (Ellipse2D) createShape(shapeNewPosition,
					(NatureObjectType) type);
			graphics2D.setPaint(Color.LIGHT_GRAY);
			graphics2D.fill(traceShape);

			return;
		}

	}

	private RectangularShape createShape(LiteShape shape, NatureObjectType type) {
		try {
			if (NatureObjectType.ROBOT.equals(type)) {
				Rectangle2D bounds = shape.getBounds2D();
				double ellipseX = bounds.getMinX();
				double ellipseY = bounds.getMinY();

				ellipseX = ellipseX * affineTransform.getScaleX()
						+ affineTransform.getTranslateX() + 4;
				ellipseY = ellipseY * affineTransform.getScaleY()
						+ affineTransform.getTranslateY() + 4;

				double width = bounds.getWidth() * affineTransform.getScaleX()
						- 8;
				double height = bounds.getHeight()
						* affineTransform.getScaleY() - 8;
				Ellipse2D robotShape = new Ellipse2D.Double(ellipseX, ellipseY,
						width, height);

				return robotShape;
			} else if (NatureObjectType.GOAL_UNREACHED.equals(type)
					|| NatureObjectType.GOAL_REACHED.equals(type)) {
				Rectangle2D bounds = shape.getBounds2D();

				double ellipseX = bounds.getCenterX();
				double ellipseY = bounds.getCenterY();

				ellipseX = ellipseX * affineTransform.getScaleX()
						+ affineTransform.getTranslateX() - 5;
				ellipseY = ellipseY * affineTransform.getScaleY()
						+ affineTransform.getTranslateY() - 5;

				Ellipse2D goalShape = new Ellipse2D.Double(ellipseX, ellipseY,
						10, 10);

				return goalShape;
			} else if (NatureObjectType.TRACE.equals(type)) {
				Rectangle2D bounds = shape.getBounds2D();

				double ellipseX = bounds.getCenterX();
				double ellipseY = bounds.getCenterY();

				ellipseX = ellipseX * affineTransform.getScaleX()
						+ affineTransform.getTranslateX();
				ellipseY = ellipseY * affineTransform.getScaleY()
						+ affineTransform.getTranslateY();

				Ellipse2D traceShape = new Ellipse2D.Double(ellipseX, ellipseY,
						10, 10);

				return traceShape;
			}

		} catch (Exception e) {
			return null;
		}

		return null;
	}

	private static Color getMixedColor(Color c1, float pct1, Color c2,
			float pct2) {
		float[] clr1 = c1.getComponents(null);
		float[] clr2 = c2.getComponents(null);
		for (int i = 0; i < clr1.length; i++) {
			clr1[i] = (clr1[i] * pct1) + (clr2[i] * pct2);
		}
		return new Color(clr1[0], clr1[1], clr1[2], clr1[3]);
	}

	private void paintBorderShadow(Graphics2D g2, Shape shape, int shadowWidth) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		int sw = shadowWidth * 2;
		for (int i = sw; i >= 2; i -= 2) {
			float pct = (float) (sw - i) / (sw - 1);
			g2.setColor(getMixedColor(Color.LIGHT_GRAY, pct, Color.WHITE,
					1.0f - pct));
			g2.setStroke(new BasicStroke(i));
			g2.draw(shape);
		}
	}

	public List<Geometry> getDrawableObjects() {
		return drawableObjects;
	}

	public void setDrawableObjects(List<Geometry> drawableObjects) {
		this.drawableObjects = drawableObjects;
	}

	public boolean addDrawableObject(Geometry g) {
		if (!drawableObjects.contains(g)) {
			drawableObjects.add(g);
			return true;
		}

		return false;
	}

	public void removeDrawableObject(Geometry g) {
		drawableObjects.remove(g);
	}

	public void start() {
		thread = new Thread(this, "Painter thread");
		thread.start();
	}

	public void stop() {
		thread = null;
	}

	public boolean isStarted() {
		return (thread != null) ? true : false;
	}

	public void eraseAllObjects() {
		traces.clear();
		drawableObjects.clear();
		motionObjects.clear();
	}

	@Override
	public void run() {
		Thread thisThread = Thread.currentThread();
		while (thread == thisThread) {
			if (lock != null) {
				lock.lock();
				lock.unlock();
			}

			synchronized (this) {
				try {
					repaint();
					wait(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					notifyAll();
				}
			}
		}

		repaint();
	}
}

package edu.cmu.hcii.paint;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class PaintObjectConstructor implements MouseListener, MouseMotionListener {

    private List<Point> pointsGathered;
    private final PaintObjectConstructorListener constructorListener;
    private Class<? extends PaintObject> paintObjectClass;
    private PaintObject temporaryObject;

    private Color color;
    private int thickness;

    public PaintObjectConstructor(PaintObjectConstructorListener listener) {
        this.constructorListener = listener;
    }

    public void setThickness(int thickness) { this.thickness = thickness; }
    public void setColor(Color color) { this.color = color; }
    public Color getColor() { return this.color; }
    public void setClass(Class<? extends PaintObject> paintObjectClass) { this.paintObjectClass = paintObjectClass; }

	public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {
        constructorListener.hoveringOverConstructionArea(null);
    }

    public void mouseMoved(MouseEvent e) {
        updateHover(e.getPoint());
    }

    public void mousePressed(MouseEvent e) {
        pointsGathered = new ArrayList<>();
        pointsGathered.add(e.getPoint());

        temporaryObject = createPaintObject();
        if (temporaryObject == null) {
            return;
        }

        applyStyle(temporaryObject);
        temporaryObject.define(toPointArray(pointsGathered));
        updateHover(e.getPoint());
        constructorListener.constructionBeginning(temporaryObject);
    }

    public void mouseDragged(MouseEvent e) {
        if (temporaryObject == null || pointsGathered == null) {
            return;
        }

		pointsGathered.add(e.getPoint());
		temporaryObject.define(toPointArray(pointsGathered));
		updateHover(e.getPoint());
		constructorListener.constructionContinuing(temporaryObject);
    }

	public void mouseReleased(MouseEvent e) {
        if (temporaryObject == null || pointsGathered == null) {
            return;
        }

		pointsGathered.add(e.getPoint());
		temporaryObject.define(toPointArray(pointsGathered));
		constructorListener.constructionComplete(temporaryObject);
		constructorListener.hoveringOverConstructionArea(null);

		pointsGathered = null;
		temporaryObject = null;
    }

    private PaintObject createPaintObject() {
        if (paintObjectClass == null) {
            return null;
        }

        try {
            Constructor<? extends PaintObject> constructor = paintObjectClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException exception) {
            System.err.println("There was a problem making the paint object.");
            return null;
        }
    }

    private PaintObject makeHoveringPrototype(Point p) {
		PaintObject prototype = createPaintObject();
		if (prototype == null) {
			return null;
		}

    PaintObject safePrototype = java.util.Objects.requireNonNull(prototype);

		Point[] points = new Point[] { p, p };
    safePrototype.define(points);
    applyStyle(safePrototype);

    return safePrototype;
			
    }

    private void applyStyle(PaintObject paintObject) {
        paintObject.setColor(color);
        paintObject.setThickness(thickness);
    }

    private Point[] toPointArray(List<Point> points) {
        return points.toArray(new Point[0]);
    }

    // Helper to update hovering prototype to reduce duplicated calls
    private void updateHover(Point p) {
        constructorListener.hoveringOverConstructionArea(makeHoveringPrototype(p));
    }

}

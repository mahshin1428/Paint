package edu.cmu.hcii.paint;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PaintCanvas extends JPanel {

    private final List<List<PaintObject>> history;
    private List<PaintObject> paintObjects;

    private final int initialWidth;
    private final int initialHeight;

    private PaintObject temporaryObject;
    private PaintObject hoveringObject;

    public PaintCanvas(int initialWidth, int initialHeight) {
        this.initialWidth = initialWidth;
        this.initialHeight = initialHeight;

        setPreferredSize(new Dimension(initialWidth, initialHeight));

        paintObjects = new ArrayList<>();
        history = new ArrayList<>();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON));

        Rectangle clipBounds = g2.getClipBounds();
        g2.setColor(Color.white);
        g2.fillRect((int) clipBounds.getX(), (int) clipBounds.getY(),
                (int) clipBounds.getWidth(), (int) clipBounds.getHeight());

        for (PaintObject paintObject : paintObjects) {
            paintObject.paint(g2);
        }

        if (temporaryObject != null) {
            temporaryObject.paint(g2);
        }

        if (hoveringObject != null) {
            Rectangle rect = hoveringObject.getBoundingBox();
            g2.setColor(Color.black);
            g2.drawRect((int) rect.getX() - 1, (int) rect.getY() - 1,
                    (int) rect.getWidth() + 2, (int) rect.getHeight() + 2);
            hoveringObject.paint(g2);
        }
    }

    public int sizeOfHistory() {
        return history.size();
    }

    public void setTemporaryObject(PaintObject temporaryObject) {
        this.temporaryObject = temporaryObject;
        repaint();
    }

    public void setHoveringObject(PaintObject hoveringObject) {
        this.hoveringObject = hoveringObject;
        repaint();
    }

    public void addPaintObject(PaintObject newObject) {
        addHistorySnapshot();
        paintObjects.add(newObject);
        recalculateCanvasSize();
        repaint();
    }

    public void clear() {
        addHistorySnapshot();
        paintObjects.clear();
        recalculateCanvasSize();
        repaint();
    }

    public void undo() {
        if (history.isEmpty()) {
            return;
        }

        paintObjects = new ArrayList<>(history.remove(history.size() - 1));
        recalculateCanvasSize();
        repaint();
    }

    private void addHistorySnapshot() {
        history.add(new ArrayList<>(paintObjects));
    }

    private void recalculateCanvasSize() {
        int maxWidth = initialWidth;
        int maxHeight = initialHeight;

        for (PaintObject paintObject : paintObjects) {
            Rectangle bounds = paintObject.getBoundingBox();
            if (bounds != null) {
                maxWidth = Math.max(maxWidth, bounds.x + bounds.width + 20);
                maxHeight = Math.max(maxHeight, bounds.y + bounds.height + 20);
            }
        }

        Dimension newSize = new Dimension(maxWidth, maxHeight);
        if (!newSize.equals(getPreferredSize())) {
            setPreferredSize(newSize);
            revalidate();
        }
    }
}

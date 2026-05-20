package edu.cmu.hcii.paint;

import java.awt.*;

@SuppressWarnings("unused")
public class LinePaint extends PaintObject {

	private Point[] points;

	public void define(Point[] points) {
		this.points = points;
	}

	public Rectangle getBoundingBox() {
		int x1 = (int) points[0].getX();
		int y1 = (int) points[0].getY();
		int x2 = (int) points[points.length - 1].getX();
		int y2 = (int) points[points.length - 1].getY();

		int minX = Math.min(x1, x2) - thickness / 2;
		int minY = Math.min(y1, y2) - thickness / 2;
		int maxX = Math.max(x1, x2) + thickness / 2;
		int maxY = Math.max(y1, y2) + thickness / 2;

		return new Rectangle(minX, minY, maxX - minX, maxY - minY);
	}

	public void paint(Graphics2D g) {
		Stroke oldStroke = g.getStroke();
		g.setStroke(new BasicStroke(thickness));
		g.setColor(color);
		g.drawLine((int) points[0].getX(), (int) points[0].getY(),
				(int) points[points.length - 1].getX(), (int) points[points.length - 1].getY());
		g.setStroke(oldStroke);
	}
}



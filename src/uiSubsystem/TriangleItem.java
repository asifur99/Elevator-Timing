package uiSubsystem;

import java.awt.*;
import javax.swing.*;

/**
 * A TriangleItem is a shape which can be lit up depending if the elevator is moving in the given direction.
 * 
 * @Author Ashton Mohns
 */
public class TriangleItem extends JLabel {
	private static final long serialVersionUID = 3388147927410376425L;
	private final Polygon triangle;

    /**
     * Constructor for a new TriangleItem
     *
     * @param upwards if the point should be upwards or downwards
     */
    public TriangleItem(boolean upwards) {
        triangle = createTriangle(upwards);
    }

    /**
     * Paint the border of the triangle
     *
     * @param g
     */
    @Override
    public void paintBorder( Graphics g ) {
        g.drawPolygon(triangle);
    }

    /**
     * Fill in the triangle, base Color = Color.BLACK
     *
     * @param g
     */
    @Override
    public void paintComponent( Graphics g ) {
        g.setColor(Color.BLACK);
        g.fillPolygon(triangle);
    }

    /**
     * Get the size of the TriangleItem
     * @return
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200,100);
    }

    /**
     * Check if the triangle contains the tuple (x,y)
     * @param x
     * @param y
     * @return
     */
    public boolean contains(int x, int y) {
        return triangle.contains(x, y);
    }

    /**
     * Set the fill of the triangle to a specific Color
     * @param color
     */
    public void setBackground(Color color) {
        Graphics g = getGraphics();
        if(g != null) {
            g.setColor(color);
            g.fillPolygon(triangle);
        } else {
            super.setBackground(color);
        }
    }

    /**
     * Create a new triangle
     * @param upwards true then point up
     * @return
     */
    private Polygon createTriangle(boolean upwards) {
        Polygon p = new Polygon();
        if(upwards) {
            p.addPoint(30, 75);
            p.addPoint(80, 25);
            p.addPoint(130, 75);
        } else {
            p.addPoint(30, 25);
            p.addPoint(80, 75);
            p.addPoint(130, 25);
        }
        return p;
    }
}
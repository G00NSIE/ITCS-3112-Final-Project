// Manny Merino
// Finite Subdivision for a triangle
// ITCS 3112


package src;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;

public class TriangleSubdivions {
    private static Point2D.Double getMidpoint(Point2D.Double pointA, Point2D.Double pointB) {
        return new Point2D.Double((pointA.x + pointB.x) / 2, (pointA.y + pointB.y) / 2);
    }

    private static ArrayList<Polygon> divide(ArrayList<Point2D.Double> triangleVertices, int iteration, int[] strategy) {
        int indexToSubdivide = strategy[iteration % strategy.length];
        Point2D.Double subdivideVertex = triangleVertices.remove(indexToSubdivide);
        Point2D.Double midpoint = getMidpoint(triangleVertices.get(0), triangleVertices.get(1));

        ArrayList<Polygon> newTriangles = new ArrayList<>();
        Polygon triangle1 = new Polygon();
        triangle1.addPoint((int) subdivideVertex.x, (int) subdivideVertex.y);
        triangle1.addPoint((int) midpoint.x, (int) midpoint.y);
        triangle1.addPoint((int) triangleVertices.get(0).x, (int) triangleVertices.get(0).y);

        Polygon triangle2 = new Polygon();
        triangle2.addPoint((int) subdivideVertex.x, (int) subdivideVertex.y);
        triangle2.addPoint((int) midpoint.x, (int) midpoint.y);
        triangle2.addPoint((int) triangleVertices.get(1).x, (int) triangleVertices.get(1).y);

        newTriangles.add(triangle1);
        newTriangles.add(triangle2);

        return newTriangles;
    }

    private static ArrayList<Polygon> subdivideTriangle(int[] strategy, int iterations, int width, int height) {
        ArrayList<Point2D.Double> triangleVertices = new ArrayList<>();
        triangleVertices.add(new Point2D.Double(width / 2.0, 0));  // vertex A
        triangleVertices.add(new Point2D.Double(0, height));       // vertex B
        triangleVertices.add(new Point2D.Double(width, height));   // vertex C

        ArrayList<Polygon> previousGenerationTriangles = new ArrayList<>();
        previousGenerationTriangles.add(new Polygon(
                new int[]{(int) triangleVertices.get(0).x, (int) triangleVertices.get(1).x, (int) triangleVertices.get(2).x},
                new int[]{(int) triangleVertices.get(0).y, (int) triangleVertices.get(1).y, (int) triangleVertices.get(2).y},
                3));

        for (int iteration = 0; iteration < iterations; iteration++) {
            ArrayList<Polygon> newTriangles = new ArrayList<>();
            for (Polygon triangle : previousGenerationTriangles) {
                newTriangles.addAll(divide(new ArrayList<>(Arrays.asList(
                        new Point2D.Double(triangle.xpoints[0], triangle.ypoints[0]),
                        new Point2D.Double(triangle.xpoints[1], triangle.ypoints[1]),
                        new Point2D.Double(triangle.xpoints[2], triangle.ypoints[2]))), iteration, strategy));
            }
            previousGenerationTriangles = newTriangles;
        }
        return previousGenerationTriangles;
    }

    public static void main(String[] args) {
        int[] strategy = {0, 2, 1};
        int initialWidth = 800;
        int initialHeight = 600;

      
        String input = JOptionPane.showInputDialog(null, "Enter the number of iterations:\n recommended: <14");
        int iterations = Integer.parseInt(input);

        ArrayList<Polygon> triangles = subdivideTriangle(strategy, iterations, initialWidth, initialHeight);

        JFrame frame = new JFrame("Subdivided Triangles");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(initialWidth, initialHeight);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


                // Set scaling factors for zooming
                double scaleX = (double) getWidth() / initialWidth;
                double scaleY = (double) getHeight() / initialHeight;
                g2d.scale(scaleX, scaleY);

                for (Polygon triangle : triangles) {
                    g2d.draw(triangle);
                }
            }
        };

        
        frame.add(panel);

        final Timer[] timer = {new Timer(300, new ActionListener() {
            private int iteration = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (iteration >= iterations) {
                    ((Timer) e.getSource()).stop(); 
                    return;
                }

                triangles.clear();
                triangles.addAll(subdivideTriangle(strategy, iteration + 1, initialWidth, initialHeight));
                panel.repaint();
                iteration++;
            }
        })};

        JPanel buttonPanel = new JPanel();
        JButton redrawButton = new JButton("Redraw");
        buttonPanel.add(redrawButton);

        // Add action listener to the redraw button
        redrawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show input dialog again
                String newInput = JOptionPane.showInputDialog(null, "Enter the number of iterations:\nrecommended: <14");
                int newIterations = Integer.parseInt(newInput);

                // Update the triangles with new iterations
                triangles.clear();
                triangles.addAll(subdivideTriangle(strategy, newIterations, initialWidth, initialHeight));

                // Restart the timer
                timer[0].stop(); // Stop the timer
                timer[0] = new Timer(300, new ActionListener() { // Assign a new timer instance
                    private int iteration = 0;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (iteration >= newIterations) {
                            ((Timer) e.getSource()).stop(); // Stop the timer when all iterations are drawn
                            return;
                        }

                        triangles.clear();
                        triangles.addAll(subdivideTriangle(strategy, iteration + 1, initialWidth, initialHeight));
                        panel.repaint();
                        iteration++;
                    }
                });
                timer[0].start(); // Start the timer
            }
        });

        // Add panels to the frame
        frame.add(buttonPanel, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.CENTER);

        timer[0].start(); // Start the timer

        frame.setVisible(true);
    }
}

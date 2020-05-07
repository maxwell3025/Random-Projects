package com.max31415.projects.ballpit;

import com.max31415.util.Vector2D;

import javax.swing.*;
import java.awt.*;

public class SimulationPanel extends JPanel {
    public Simulation simulation;
    double scaleFactor = 100;

    public SimulationPanel(Simulation s) {
        super();
        this.setPreferredSize(new Dimension(1000, 1000));
        simulation = s;
    }

    @Override
    public void paint(Graphics g) {
        g.fillRect(0,0, (int) (10 * scaleFactor), (int) (10 * scaleFactor));
        for (Ball b : simulation.balls) {
            g.setColor(new Color(Math.min((int)(b.pressureFinal/5),255),0,0));
            g.fillOval((int) ((b.x - b.radius) * scaleFactor), (int) ((b.y - b.radius) * scaleFactor), (int) (b.radius*scaleFactor*2), (int) (b.radius*scaleFactor*2));
            g.setColor(Color.blue);
            g.drawOval((int) ((b.x - b.radius) * scaleFactor), (int) ((b.y - b.radius) * scaleFactor), (int) (b.radius*scaleFactor*2), (int) (b.radius*scaleFactor*2));
        }
        //connection rendering
//        g.setColor(Color.white);
//        for (int i = 0; i < simulation.balls.size(); i++) {
//            try {
//                Ball b = simulation.balls.get(i);
//                for (int j = 0; j < b.connections.size(); j++) {
//                    try {
//                        Vector2D vector = b.connections.get(j);
//                        if(vector!=null&&b!=null)
//                        g.drawLine((int) (b.x * scaleFactor), (int) (b.y * scaleFactor), (int) (vector.getX() * scaleFactor), (int) (vector.getY() * scaleFactor));
//                    } catch (IndexOutOfBoundsException e) {
//                        continue;
//                    }
//                }
//            } catch (IndexOutOfBoundsException e) {
//                continue;
//            }
//        }
    }
}

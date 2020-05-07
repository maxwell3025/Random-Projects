package com.max31415.projects.ballpit;

import javax.swing.*;

public class BallpitMain {
    public static void main(String[] args) {
        JFrame window = new JFrame();
        Simulation s = new Simulation();
        window.add(s.display);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
        for (int i = 0; i < 500; i++) {
            s.addBall(new Ball(Math.random() * 10, Math.random() * 10, 0.5*Math.exp(-Math.random()*Math.log(2))));
        }
        while (true) {
            window.repaint();
            s.update(0.001);
            try {
                Thread.sleep(0, 10000);
            } catch (InterruptedException e) {

            }
        }
    }
}

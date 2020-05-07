package com.max31415.projects.fluids;

import javax.swing.*;

public class FluidsMain {
    //program settings
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    public static void main(String[] args) {
        JFrame window = new JFrame();
        FluidPanel panel = new FluidPanel();
        MACGrid fluid = new MACGrid();
        panel.bindToSimulation(fluid);
        window.add(panel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);

        while (true) {
//            try {
//                Thread.sleep(50);
//            } catch (InterruptedException e) {
//
//            }
            fluid.update(0.05);
            panel.repaint();
        }
    }
}

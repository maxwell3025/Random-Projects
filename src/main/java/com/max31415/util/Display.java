package com.max31415.util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;


/**
 *
 */
public abstract class Display extends JPanel {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    public boolean closeRequested = false;
    protected JFrame window = new JFrame();
    private boolean initialized = false;

    /**
     * constructor, standard stuff for JPanel
     */
    public Display() {
        super();
        initJPanel();
        initJFrame();
    }

    private void initJPanel() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }

    private void initJFrame() {
        window.add(this);
        window.pack();
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        window.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                closeRequested = true;
            }
        });

    }

    public void init() {
        if(initialized)
            return;
        initialized = true;
        new Thread(() -> {
            start();
            while (!closeRequested) {
                loop();
                repaint();
            }
            close();
        }).start();
    }

    abstract protected void start();

    abstract protected void loop();

    abstract protected void close();

    abstract protected void draw(Graphics g);

    /**
     * paint method, draws a rectangle
     *
     * @param g
     */
    @Override
    public void paint(Graphics g) {
        draw(g);
    }

}

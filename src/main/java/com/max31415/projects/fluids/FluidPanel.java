package com.max31415.projects.fluids;

import com.max31415.util.Vector2D;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class FluidPanel extends JPanel {
    private BufferedImage currentFrame;//this should only be accessed by setting its value to a completed frame
    private MACGrid simulation;
    private int leftBound = 0;
    private int rightBound = 8*16;
    private int bottomDound = 0;
    private int upperBound = 6*16;
    private int panelWidth = FluidsMain.WIDTH;
    private int panelHeight = FluidsMain.HEIGHT;

    public FluidPanel() {
        super();
        this.setPreferredSize(new Dimension(FluidsMain.WIDTH, FluidsMain.HEIGHT));
    }

    public void bindToSimulation(MACGrid simulation) {
        this.simulation = simulation;
    }

    @Override
    public void paint(Graphics g) {
        if (simulation == null) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, panelWidth, panelHeight);
            return;
        }
        BufferedImage markersLayer = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gMarkers = (Graphics2D)markersLayer.getGraphics();
        BufferedImage velocityLayer = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gVelocity = (Graphics2D)velocityLayer.getGraphics();
        BufferedImage gridLayer = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gGrid = (Graphics2D)gridLayer.getGraphics();
        //render the underlying simulation
        int width = rightBound - leftBound;
        int height = upperBound - bottomDound;
//        for (int xOffset = 0; xOffset < width; xOffset++) {
//            for (int yOffset = 0; yOffset < height; yOffset++) {
//                //find the bounds of the cell to be rendered
//                int cellLeft = xOffset * panelWidth / width;
//                int cellRight = (xOffset + 1) * panelWidth / width;
//                int cellBottom = yOffset * panelHeight / height;
//                int cellTop = (yOffset + 1) * panelHeight / height;
//                //find the width and height
//                int cellWidth = cellRight - cellLeft;
//                int cellHeight = cellTop - cellBottom;
//                //find the type of cell
//                Cell cell = simulation.getOld(leftBound + xOffset, bottomDound + yOffset);
//                gGrid.setColor(Color.black);
//                if (cell != null) {
//                    switch (cell.state) {
//                        case FLUID:
//                            gGrid.setColor(Color.BLUE);
//                            break;
//                        case SOLID:
//                            gGrid.setColor(Color.GRAY);
//                            break;
//                        case AIR:
//                            gGrid.setColor(Color.WHITE);
//                            break;
//                        default:
//                            gGrid.setColor(Color.RED);
//                    }
//                }
//                //render!
//                gGrid.fillRect(cellLeft, panelHeight - cellTop, cellWidth, cellHeight);g.setColor(Color.GREEN);
//                if (cell != null) {
//                    gVelocity.setColor(Color.RED);
//                    gVelocity.drawLine(cellLeft, panelHeight - cellTop + cellHeight / 2, cellLeft + (int) (cell.xVelocity * cellWidth * .05), panelHeight - cellTop + cellHeight / 2);
//                    gVelocity.setColor(Color.GREEN);
//                    gVelocity.drawLine(cellLeft + cellWidth / 2, panelHeight - cellBottom, cellLeft + cellWidth / 2, panelHeight - cellBottom - (int) (cell.yVelocity * cellWidth * .05));
//                }
//            }
//        }
        //render the markers
        for(int i = 0; i< simulation.markers.size(); i++){
            Vector2D marker = simulation.markers.get(i).position;
            switch(simulation.markers.get(i).type){
                case WATER:
                    gMarkers.setColor(new Color(0,128,255,128));
                    break;
                case OIL:
                    gMarkers.setColor(new Color(255,128,0,128));
                    break;
            }
//            if((Integer.hashCode())<1)
            gMarkers.fillOval((int)((marker.getX()-leftBound)*panelWidth/width)-2,(int)(panelHeight-(marker.getY()-bottomDound)*panelHeight/height)-2,4,4);
        }
        g.setColor(Color.BLACK);
        g.fillRect(0,0,panelWidth, panelHeight);
        //g.drawImage(gridLayer, 0, 0, panelWidth, panelHeight, null);
        //g.drawImage(velocityLayer, 0, 0, panelWidth, panelHeight, null);
        g.drawImage(markersLayer, 0, 0, panelWidth, panelHeight, null);
    }
}

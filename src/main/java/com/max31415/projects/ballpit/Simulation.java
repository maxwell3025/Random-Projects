package com.max31415.projects.ballpit;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Simulation {
    ArrayList<Ball> balls = new ArrayList<>();
    HashMap<Point, ArrayList<Integer>> partitions = new HashMap<>();
    SimulationPanel display;
    public Simulation() {
    display = new SimulationPanel(this);
    }

    public void addBall(Ball ball) {
        int index = balls.size();
        balls.add(ball);
        int cellX = (int) Math.floor(ball.x);
        int cellY = (int) Math.floor(ball.y);
        addIndex(new Point(cellX, cellY), index);

    }

    private void addIndex(Point position, int index) {
        if (!partitions.containsKey(position)) partitions.put(position, new ArrayList<>());
        ArrayList<Integer> partition = partitions.get(position);
        partition.add(index);
    }

    private void removeIndex(Point position, int index) {
        if (!partitions.containsKey(position)) return;
        ArrayList<Integer> partition = partitions.get(position);
        partition.removeIf((x) -> x == index);
        if (partition.isEmpty()) {
            partitions.remove(position);
        }
    }

    public void update(double dt) {
        //interact with the other balls
        for (Ball ball : balls) {
            ball.touching = false;
            ball.pressure = 0;
            ball.connections.clear();
            int cellX = (int) Math.floor(ball.x);
            int cellY = (int) Math.floor(ball.y);
            //loop through all neighbor partitions
            for (int i = cellX - 1; i < cellX + 2; i++) {
                for (int j = cellY - 1; j < cellY + 2; j++) {
                    if(partitions.containsKey(new Point(i,j))){
                        for(int ballIndex: partitions.get(new Point(i,j))){
                            Ball ball2 = balls.get(ballIndex);
                            if(ball!=ball2){
                            ball.interact(ball2);
                            }
                        }
                    }
                }
            }
            ball.pressureFinal = ball.pressure;
        }
        for(int i = 0;i<balls.size();i++){
            Ball ball = balls.get(i);
            int cellX = (int) Math.floor(ball.x);
            int cellY = (int) Math.floor(ball.y);
            removeIndex(new Point(cellX, cellY), i);
            ball.applyForces();
            ball.update(dt);
            cellX = (int) Math.floor(ball.x);
            cellY = (int) Math.floor(ball.y);
            addIndex(new Point(cellX, cellY), i);
        }
    }
}

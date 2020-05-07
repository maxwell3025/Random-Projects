package com.max31415.projects.fluids;

public class Cell {
    //the cell position is an integer pair (x,y). the cell is centered 0.5 units above and to the right of that point.
    //the cell's territory is a square with the bottom left corner at (x,y)
    public CellState state;//encompasses the cells territory.
    public double xVelocity;//this is positioned 0.5 divisions above the cell position
    public double yVelocity;//this is positioned 0.5 devisions right the cell position
    public double divergence = 0;
    public double pressure = 0;
    public int markerDensity = 0;
    public double density = 0;
    //all other variables are positioned at the center of the cell(x+.5,y+.5)
    public int depth;
    public static enum CellState{
        FLUID, AIR, SOLID
    }
    private Cell(){

    }
    public static Cell fluidCell(double xVelocity, double yVelocity){
        Cell out = new Cell();
        out.state = CellState.FLUID;
        out.xVelocity = xVelocity;
        out.yVelocity = yVelocity;
        out.depth = -1;
        return out;
    }
    public static Cell airCell(double xVelocity, double yVelocity, int depth){
        Cell out = new Cell();
        out.state = CellState.AIR;
        out.xVelocity = xVelocity;
        out.yVelocity = yVelocity;
        out.depth = depth;
        return out;
    }
    public static Cell solidCell(){
        Cell out = new Cell();
        out.state = CellState.SOLID;
        out.xVelocity = 0;
        out.yVelocity = 0;
        out.depth = -1;
        return out;
    }
    //add the characteristics of the other cell onto this one
    public void add(Cell other) {
        this.xVelocity += other.xVelocity;
        this.yVelocity += other.yVelocity;
    }

    //divide the characteristics for averaging purposes
    public void divide(double amount) {
        this.xVelocity /= amount;
        this.yVelocity /= amount;
    }

}

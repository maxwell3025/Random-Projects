package com.max31415.projects.fluids;

import com.max31415.util.Vector2D;

public class Marker {
    public MarkerType type;
    public Vector2D position;
    public double density;
    public static enum MarkerType{
        OIL,WATER
    }
    private Marker(){
    }
    public static Marker oil(double x, double y){
        Marker out = new Marker();
        out.position = new Vector2D(x,y);
        out.type = MarkerType.OIL;
        out.density = 0.5;
        return out;
    }
    public static Marker water(double x, double y){
        Marker out = new Marker();
        out.position = new Vector2D(x,y);
        out.type = MarkerType.WATER;
        out.density = 10;
        return out;
    }

}

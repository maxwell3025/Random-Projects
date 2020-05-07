package com.max31415.util;

public final class Vector2D {
    private double x;
    private double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D add(Vector2D other){
        return new Vector2D(this.x+other.x,this.y+other.y);
    }

    public Vector2D subtract(Vector2D other){
        return new Vector2D(this.x-other.x,this.y-other.y);
    }

    public Vector2D scale(double factor){
        return new Vector2D(this.x*factor,this.y*factor);
    }

    public Vector2D rotate(double radians){
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        return new Vector2D(this.x*cos-this.y*sin,this.y*cos+this.x*sin);
    }

    public Vector2D perpendicular(){
        return new Vector2D(this.y,-this.x);
    }

    public double dot(Vector2D other){
        return this.x*other.x+this.y*other.y;
    }

    public double cross(Vector2D other){
        return this.x*other.y-this.y*other.x;
    }

    /**
     * projects this vector to another vector
     * @param axis
     * @return
     */
    public Vector2D projectTo(Vector2D axis){
        return axis.scale(axis.dot(this)/axis.dot(axis));
    }

    /**
     * does a vector projection with itself as the axis
     * @param other
     * @return
     */

    public Vector2D projectFrom(Vector2D other){
        return other.projectTo(this);
    }

    public double magnitude(){
        return Math.sqrt(x*x+y*y);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
    public String toString(){
        return "("+this.x+","+this.y+")";
    }
}

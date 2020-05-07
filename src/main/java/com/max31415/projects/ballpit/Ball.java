package com.max31415.projects.ballpit;

import com.max31415.util.Vector2D;

import java.util.ArrayList;

public class Ball {
    //balls have a radius of 1
    public double x;
    public double y;
    public double vx;
    public double vy;
    public double fx;
    public double fy;
    public double radius;
    public boolean touching;
    public double pressure;
    public double pressureFinal;
    public static final double forceMultiplier = 4000;
    public static final double friction = 0.01;
    public ArrayList<Vector2D> connections = new ArrayList<>();
    public Ball(double x, double y, double radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public void update(double dt) {
        x += vx * dt;
        y += vy * dt;
        vx += fx * dt;
        vy += fy * dt;
        fx = 0;
        fy = 0;
    }
    public void applyForces(){
        boolean wallContact = false;
        if(y>10-radius){
            //normal force
            fy+=(10-radius-y)*forceMultiplier;
            wallContact = true;
        }else{
            //apply gravity
            fy+=4;
        }
        if(x<radius){
            fx-=(x-radius)*forceMultiplier;
            wallContact = true;
        }
        if(x>10-radius){
            fx+=(10-radius-x)*forceMultiplier;
            wallContact = true;
        }
        if(wallContact){
            fx-=vx*friction*forceMultiplier;
            fy-=vy*friction*forceMultiplier;
            touching = true;
        }
    }
    public void interact(Ball other) {
        connections.add(new Vector2D(other.x,other.y));
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        double touchingRadius = this.radius+other.radius;
        if (dist < touchingRadius) {
            touching = true;
            double forceMagnitude = (touchingRadius - dist)*forceMultiplier/dist*touchingRadius;
            pressure +=forceMagnitude;
            fx+=dx/dist*forceMagnitude;
            fy+=dy/dist*forceMagnitude;
            //average velocity
            double avx = (this.vx+other.vx)/2;
            double avy = (this.vy+other.vy)/2;
            //difference in average velocity
            double dvx = this.vx-avx;
            double dvy = this.vy-avy;
            fx-=dvx*friction*forceMultiplier;
            fy-=dvy*friction*forceMultiplier;
        }

    }
}

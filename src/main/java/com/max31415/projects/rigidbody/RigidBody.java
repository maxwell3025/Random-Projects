package com.max31415.projects.rigidbody;

import com.max31415.util.Vector2D;

import java.util.ArrayList;

public class RigidBody {
    private ArrayList<Vector2D> contactPoints = new ArrayList<>();
    Vector2D centerOfMass;
    Vector2D momentum;
    double mass;
    Vector2D force;
    double rotation;
    double angularMomentum;
    double angularIntertia;
    double torque;

    public RigidBody(Vector2D position) {
    }

    /**
     * @param impulse  the amount of impulse to apply to that point as a vector
     * @param position world centerOfMass to apply the force from
     */
    public void applyImpulse(Vector2D impulse, Vector2D position, boolean objectCoordinates) {
        if (objectCoordinates) {
            //TODO
        } else {
            Vector2D difference = position.subtract(centerOfMass);
            angularMomentum += difference.cross(impulse);
            momentum = momentum.add(impulse);
        }
    }

    public void applyForce(Vector2D force, Vector2D position) {

    }

    public void update(double dt) {
        centerOfMass = centerOfMass.add(momentum.scale(dt / mass));
        rotation += angularMomentum * dt / angularIntertia;
    }

    public int numberOfPoints() {
        return contactPoints.size();
    }

    public Vector2D getPoint(int index) {
        return contactPoints.get(index);
    }

    public void addPoint(Vector2D point) {

    }
}

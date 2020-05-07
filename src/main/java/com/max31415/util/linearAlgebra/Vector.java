package com.max31415.util.linearAlgebra;

import java.util.ArrayList;

public class Vector {
    private ArrayList<Double> data = new ArrayList<>();
    private int size;
    private boolean row;

    private Vector(int size, boolean row) {
        this.size = size;
        this.row = row;
        for(int i = 0;i<size;i++){
            data.add(0D);
        }
    }

    /**
     * Constructor, creates a column vector by default
     */
    public Vector(int size){
        this.size = size;
        this.row = false;
        for(int i = 0;i<size;i++){
            data.add(0D);
        }
    }

    /**
     * generates a row vector
     * @param size size of the vector
     * @return returns a row vector initialized with zeroes
     */
    public static Vector rowVector(int size){
        return new Vector(size, true);
    }
    /**
     * generates a column vector. This is equivalent to the default initializer
     * @param size size of the vector
     * @return returns a row vector initialized with zeroes
     */
    public static Vector columnVector(int size){
        return new Vector(size, false);
    }
    public double get(int index){
        return data.get(index);
    }
    public void set(int index, double value){
        data.set(index,value);
    }
}

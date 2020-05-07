package com.max31415.util.linearAlgebra;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * A class used to represent a sparse matrix
 */
public class SparseMatrix {
    //the amount of rows
    private int rowCount;
    //the amount of columns
    private int columnCount;
    //the entries in the array
    private HashMap<Point, Double> data = new HashMap<>();
    //the column numbers for the entries in each row
    private List<Set<Integer>> valuesInRow = new ArrayList<>();
    //the row numbers for the entries in each column
    private List<Set<Integer>> valuesInColumn = new ArrayList<>();
    //is the matrix transpose
    private boolean transpose = false;

    private SparseMatrix(int rowCount, int columnCount) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        //initalizing the sets
        for (int i = 0; i < rowCount; i++) {
            valuesInRow.add(new HashSet<>());
        }
        for (int i = 0; i < columnCount; i++) {
            valuesInColumn.add(new HashSet<>());
        }
    }

    /**
     * creates a square identity matrix
     *
     * @param size the size of the array
     * @return a square identity matrix
     */
    public static SparseMatrix squareMatrix(int size) {
        SparseMatrix out = new SparseMatrix(size, size);
        for(int i = 0;i<size;i++){
            out.set(i,i,1);
        }
        return out;
    }

    public static SparseMatrix multiply(SparseMatrix a, SparseMatrix b) {
        if (a.columnCount != b.rowCount) {
            throw new IllegalArgumentException("unable to multiply matrices");
        }
        SparseMatrix out = new SparseMatrix(a.rowCount, b.columnCount);
        //iterate through all of the points
        for (Map.Entry<Point, Double> entry : a.data.entrySet()) {
            Point entryA = entry.getKey();
            //iterate through each data value that point can interact with
            for (int columnB : b.valuesInRow.get(entryA.y)) {
                //add to the point at that index
                Point entryB = new Point(entryA.y, columnB);
                Point entryC = new Point(entryA.x, entryB.y);
                double change = a.get(entryA.x, entryA.y) * b.get(entryB.x, entryB.y);
                out.set(entryC.x, entryC.y, out.get(entryC.x, entryC.y) + change);
            }
        }
        return out;
    }

    /**
     * This is a method that returns the transpose of this matrix. This changes the matrix.
     * @returns the transpose of this matrix
     */
    public SparseMatrix setTranspose(){
        //only the actual data isn't reversed, so that will be dealt with by the get and set functions
        transpose = !transpose;
        List<Set<Integer>> templist = valuesInRow;
        valuesInRow = valuesInColumn;
        valuesInColumn = templist;
        int tempCount = rowCount;
        rowCount = columnCount;
        columnCount = tempCount;
        return this;
    }
    public SparseMatrix transpose(){
        SparseMatrix out = new SparseMatrix(columnCount, rowCount);
        for (Map.Entry<Point, Double> entry : data.entrySet()) {
            Point entryPosition = entry.getKey();
            out.set(entryPosition.y, entryPosition.x, entry.getValue());
        }
        return out;
    }

    /**
     * sets the value of a certain entry in a the matrix. if set to 0, the matrix entry is removed
     *
     * @param row    the row of the cell
     * @param column the column of the cell
     * @param value  the value to set it as
     * @throws IndexOutOfBoundsException if the given row or column is out of bounds for the array
     */
    public void set(int row, int column, double value) {
        if (row < 0 || row >= rowCount) {
            throw new IndexOutOfBoundsException(String.format("Index out of range: %d", row));
        }
        if (column < 0 || column >= columnCount) {
            throw new IndexOutOfBoundsException(String.format("Index out of range: %d", column));
        }
        Point entryPosition = new Point(row, column);
        if(transpose){
            entryPosition = new Point(column, row);
        }
        if (value == 0) {
            //if the value is zero, the entry is erased
            valuesInRow.get(row).remove(column);
            valuesInColumn.get(column).remove(row);
            data.remove(entryPosition);
        } else {
            //otherwise, set the point to that value
            valuesInRow.get(row).add(column);
            valuesInColumn.get(column).add(row);
            data.put(entryPosition, value);
        }
    }

    /**
     * returns the entry value at the given position, returns 0 if there is no entry in that position
     *
     * @param row    the row of the entry
     * @param column the column of the entry
     * @return the value at the given position
     * @throws IndexOutOfBoundsException if the given row or column is out of bounds for the array
     */
    public double get(int row, int column) {
        if (row < 0 || row >= rowCount) {
            throw new IndexOutOfBoundsException(String.format("Index out of range: %d", row));
        }
        if (column < 0 || column >= columnCount) {
            throw new IndexOutOfBoundsException(String.format("Index out of range: %d", column));
        }
        Point entryPosition = new Point(row, column);
        if(transpose){
            entryPosition = new Point(column, row);
        }
        if (data.containsKey(entryPosition))
            return data.get(entryPosition);
        else
            return 0;
    }

    public String toString() {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < rowCount; i++) {
            out.append("\n[");
            for (int j = 0; j < columnCount; j++) {
                if (j != 0)
                    out.append("\t");
                out.append(String.format("% .2f", get(i, j)));
            }
            out.append("]");
        }
        return out.toString();
    }
}

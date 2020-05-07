package com.max31415.projects.fluids;

import com.max31415.util.Vector2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class MACGrid {
    public static final int AIR_DEPTH = 2;
    public static final int SOLVER_ITERATIONS = 20;
    public static final int MARKERS_PER_CELL = 4;
    public static final int CONFINING_FORCE = 1;

    private double age = 0;
    private long prevTime;
    HashMap<Point, Cell> cells_old = new HashMap<>();
    HashMap<Point, Cell> cells_new = new HashMap<>();
    ArrayList<Marker> markers = new ArrayList<>();

    //"internal" variables
    private ArrayList<Point> indices = new ArrayList<>();
    private HashSet<Point> fluidCells = new HashSet<>();
    private HashSet<Point> airCells = new HashSet<>();
    private HashSet<Point> solidCells = new HashSet<>();
    private HashSet<Point> xCorrected = new HashSet<>();
    private HashSet<Point> yCorrected = new HashSet<>();

    public MACGrid() {
        for(int y = 0;y<128;y++){
            for(int x = 0;x<64;x++){
                markers.add(Marker.water(1.25+x*.5,1.25+y*.5));
            }
        }
//        markers.add(new Vector2D(10.5,10.5));
        for(int i = 0;i<128;i++){
            cells_new.put(new Point(i,0),Cell.solidCell());
            cells_new.put(new Point(0,i),Cell.solidCell());
            cells_new.put(new Point(127,i),Cell.solidCell());
        }
    }

    public void update(double dt) {
        init(dt);
        timestamp("Initialization");
        //propagate the marker cells
        advectMarkers(dt);
        timestamp("Advection");
        //propagate the cells
        propagateCell(dt);
        timestamp("Propagation");
        //apply external forces(e.g. gravity)
        applyForces(dt);
        timestamp("Forces");
        //project out the velocities
        project();
        timestamp("Projection");
        //create a buffer of air cells
        createBuffer();
        timestamp("Buffer");

    }
    //----------INITIALIZATION-------------------
    private void init(double dt){
        age+=dt;
        System.out.println(age);
        fluidCells.clear();
        airCells.clear();
        solidCells.clear();
        xCorrected.clear();
        yCorrected.clear();
        //swap
        HashMap<Point, Cell> temp_grid = cells_old;
        cells_old = cells_new; //cells_old should not change after this until the next iteration
        cells_new = temp_grid; //cells_new is volatile
        cells_new.clear();
        //copy solid cells
        for(HashMap.Entry<Point, Cell>e:cells_old.entrySet()){
            if(e.getValue().state== Cell.CellState.SOLID){
                cells_new.put(e.getKey(),e.getValue());
                solidCells.add(e.getKey());
            }
        }
    }
    //----------MARKER ADVECTION-----------------
    private void advectMarkers(double dt){
        for (int i = 0; i < markers.size(); i++) {
            Vector2D marker = markers.get(i).position;
            //propagate with RK4 modified
//            Vector2D k1 = getVelocity(marker);
//            Vector2D k2 = getVelocity(marker.add(k1.scale(dt/3)));
//            Vector2D k3 = getVelocity(marker.add(k1.scale(dt/-3)).add(k2.scale(dt)));
//            Vector2D k4 = getVelocity(marker.add(k1.scale(dt)).add(k2.scale(-dt)).add(k3.scale(dt)));
//            Vector2D newPos = marker.add(k1.scale(.125*dt)).add(k2.scale(.375*dt)).add(k3.scale(.375*dt)).add(k4.scale(.125*dt));
            Vector2D k1 = getVelocity(marker);
            Vector2D k2 = getVelocity(marker.add(k1.scale(dt)));
            Vector2D newPos = marker.add(k1.scale(.5*dt)).add(k2.scale(.5*dt));
            markers.get(i).position = newPos;
            //start diffusing around if it's stuck in a solid
            Point cell = floor(markers.get(i).position);
            if(getOldSafe(cell.x,cell.y).state == Cell.CellState.SOLID){
                markers.get(i).position = markers.get(i).position.add(new Vector2D(Math.random()-.5,Math.random()-.5));
            }
        }
    }

    //----------CELL ADVECTION-------------------
    private void propagateCell(double dt){
        for (int i = 0; i < markers.size(); i++) {
            Vector2D marker = markers.get(i).position;
            Point cellPosition = floor(marker);
            addAsFluid(cellPosition,dt);
//            addAsFluid(new Point(cellPosition.x+1,cellPosition.y),dt);
//            addAsFluid(new Point(cellPosition.x-1,cellPosition.y),dt);
//            addAsFluid(new Point(cellPosition.x,cellPosition.y+1),dt);
//            addAsFluid(new Point(cellPosition.x,cellPosition.y-1),dt);
            getNew(cellPosition).markerDensity++;
            getNew(cellPosition).density+=markers.get(i).density;
        }
    }

    private void addAsFluid(Point position, double dt){
        if(getOldSafe(position.x,position.y).state== Cell.CellState.SOLID)
            return;
        if (fluidCells.add(position)) {
            setFluid(position.x,position.y);
            propagate(position.x,position.y,dt);
            propagate(position.x+1,position.y,dt);
            propagate(position.x,position.y+1,dt);
        }
    }

    private void propagate(int x, int y, double dt) {
        Vector2D position;
        //get x velocity
        position = new Vector2D(x,y+0.5);
        position = position.add(getVelocity(position).scale(-dt));
        double xVelocity = getXVelocity(position);
        //get y velocity
        position = new Vector2D(x+0.5,y);
        position = position.add(getVelocity(position).scale(-dt));
        double yVelocity = getYVelocity(position);
        if (cells_new.containsKey(new Point(x, y))) {
            Cell current = cells_new.get(new Point(x,y));
            current.xVelocity = xVelocity;
            current.yVelocity = yVelocity;
        }else{
            //if the cell does not exist
            cells_new.put(new Point(x, y), Cell.airCell(xVelocity, yVelocity, 0));
        }
    }

    //----------APPLY FORCES---------------------
    public void applyForces(double dt){
        for(Point p: fluidCells){
            getNew(p).yVelocity-=cells_new.get(p).density/(cells_new.get(p).markerDensity)*dt;
        }
//        for (int i = 0; i < 16; i++) {
//            getNewSafe(63, i).yVelocity += 500 * dt;
//        }
    }

    //----------PROJECTION-----------------------
    private void project(){
        for (Point p : fluidCells) {
            Cell cell = cells_new.get(p);
            cell.pressure = 0;
            double divergenceX = getNew(p.x+1,p.y).xVelocity-getNew(p.x,p.y).xVelocity;
            double divergenceY = getNew(p.x,p.y+1).yVelocity-getNew(p.x,p.y).yVelocity;
            cell.divergence = divergenceX+divergenceY-CONFINING_FORCE*(Math.max(cell.markerDensity - MARKERS_PER_CELL,0));
        }
        //calculate pressure
        for(int i = 0;i<SOLVER_ITERATIONS;i++){
            for(Point p:fluidCells){
                int neighbors = 0;
                double rho = 0;
                if(getNewSafe(p.x+1,p.y).state == Cell.CellState.AIR){
                    neighbors++;
                }
                if(getNewSafe(p.x+1,p.y).state == Cell.CellState.FLUID){
                    rho+=getNewSafe(p.x+1,p.y).pressure;
                    neighbors++;
                }

                if(getNewSafe(p.x,p.y+1).state == Cell.CellState.AIR){
                    neighbors++;
                }
                if(getNewSafe(p.x,p.y+1).state == Cell.CellState.FLUID){
                    rho+=getNewSafe(p.x,p.y+1).pressure;
                    neighbors++;
                }

                if(getNewSafe(p.x-1,p.y).state == Cell.CellState.AIR){
                    neighbors++;
                }
                if(getNewSafe(p.x-1,p.y).state == Cell.CellState.FLUID){
                    rho+=getNewSafe(p.x-1,p.y).pressure;
                    neighbors++;
                }

                if(getNewSafe(p.x,p.y-1).state == Cell.CellState.AIR){
                    neighbors++;
                }
                if(getNewSafe(p.x,p.y-1).state == Cell.CellState.FLUID){
                    rho+=getNewSafe(p.x,p.y-1).pressure;
                    neighbors++;
                }
                getNew(p.x,p.y).pressure = (getNew(p.x,p.y).divergence+rho)/neighbors;
            }
        }
        //apply that pressure
        for(Point p:fluidCells){
            applyPressureX(p.x,p.y);
            applyPressureX(p.x+1,p.y);
            applyPressureY(p.x,p.y);
            applyPressureY(p.x,p.y+1);
        }
    }

    private void applyPressureY(int x, int y){
        if(!yCorrected.add(new Point(x,y)))
            return;
        if(getNewSafe(x,y).state == Cell.CellState.SOLID||getNewSafe(x,y-1).state == Cell.CellState.SOLID){
            getNew(x,y).yVelocity=0;
        }
        else if(getNewSafe(x,y).state == Cell.CellState.AIR){
            getNew(x,y).yVelocity-=getNewSafe(x,y-1).pressure;
        }
        else if(getNewSafe(x,y-1).state == Cell.CellState.AIR){
            getNew(x,y).yVelocity+=getNewSafe(x,y).pressure;
        }
        else{
            getNew(x,y).yVelocity+=getNewSafe(x,y).pressure-getNewSafe(x,y-1).pressure;
        }
    }

    private void applyPressureX(int x, int y){
        if(!xCorrected.add(new Point(x,y)))
            return;
        if(getNewSafe(x,y).state == Cell.CellState.SOLID||getNewSafe(x-1,y).state == Cell.CellState.SOLID){
            getNew(x,y).xVelocity=0;
        }
        else if(getNewSafe(x,y).state == Cell.CellState.AIR){
            getNew(x,y).xVelocity-=getNewSafe(x-1,y).pressure;
        }
        else if(getNewSafe(x-1,y).state == Cell.CellState.AIR){
            getNew(x,y).xVelocity+=getNewSafe(x,y).pressure;
        }
        else{
            getNew(x,y).xVelocity+=getNewSafe(x,y).pressure-getNewSafe(x-1,y).pressure;
        }
    }

    //----------CREATE AIR BUFFER----------------
    private void createBuffer(){
        //create a buffer of air cells
        //kick start it by adding fluid cells to the queue
        LinkedList<Point> queue = new LinkedList<>();
        LinkedList<Point> queue_new = new LinkedList<>();
        for(Point p:fluidCells){
            //if any adjacent cells are empty, add to queue
            if((!cellEmpty(p.x+1,p.y))||(!cellEmpty(p.x-1,p.y))||(!cellEmpty(p.x,p.y+1))||(!cellEmpty(p.x,p.y-1))) {
                queue_new.add(p);
            }
        }
        //do N iterations to add the layers
        for(int i = 0;i<AIR_DEPTH;i++){
            queue = queue_new;
            queue_new = new LinkedList<>();
            for(Point p: queue){
                if(fillWithAir(p.x+1,p.y,i)){
                    queue_new.add(new Point(p.x+1,p.y));
                }
                if(fillWithAir(p.x-1,p.y,i)){
                    queue_new.add(new Point(p.x-1,p.y));
                }
                if(fillWithAir(p.x,p.y+1,i)){
                    queue_new.add(new Point(p.x,p.y+1));
                }
                if(fillWithAir(p.x,p.y-1,i)){
                    queue_new.add(new Point(p.x,p.y-1));
                }
            }
        }
    }

    private boolean fillWithAir(int x, int y, int depth){

        if(cells_new.containsKey(new Point(x,y))){
            if(cells_new.get(new Point(x,y)).state == Cell.CellState.AIR&&airCells.add(new Point(x,y))){
                return true;
            }
            return false;
        }
        //average the surrounding cells with a lower depth
        Cell total = Cell.airCell(0,0,depth);
        int neighborCount = 0;
        if(cells_new.containsKey(new Point(x+1,y))&&depth(x+1,y)<depth){
            total.add(cells_new.get(new Point(x+1,y)));
            neighborCount++;
        }
        if(cells_new.containsKey(new Point(x,y+1))&&depth(x,y+1)<depth){
            total.add(cells_new.get(new Point(x,y+1)));
            neighborCount++;
        }
        if(cells_new.containsKey(new Point(x-1,y))&&depth(x-1,y)<depth){
            total.add(cells_new.get(new Point(x-1,y)));
            neighborCount++;
        }
        if(cells_new.containsKey(new Point(x,y-1))&&depth(x,y-1)<depth){
            total.add(cells_new.get(new Point(x,y-1)));
            neighborCount++;
        }
        total.divide(neighborCount);
        cells_new.put(new Point(x,y),total);
        airCells.add(new Point(x,y));
        return true;
    }

    private boolean cellEmpty(int x, int y){
        Cell current = cells_new.get(new Point(x,y));
        if(current==null||current.state == Cell.CellState.AIR){
            return false;
        }
        return true;
    }

    private int depth(int x,int y){
        return getNew(x,y).depth;
    }

    //----------CELL METHODS-----------------
    public Cell getOld(int x, int y){
        return cells_old.get(new Point(x,y));
    }

    public Cell getOld(Point p){
        return cells_old.get(p);
    }

    public Cell getOldSafe(int x, int y){
        if(cells_old.containsKey(new Point(x,y)))
            return cells_old.get(new Point(x,y));
        else
            return Cell.airCell(0,0,-1);
    }

    private Cell getNew(int x, int y){
        return cells_new.get(new Point(x,y));
    }

    private Cell getNew(Point p){
        return cells_new.get(p);
    }

    private Cell getNewSafe(int x, int y) {
        if (cells_new.containsKey(new Point(x, y))) {
            return cells_new.get(new Point(x, y));
        } else {
            return Cell.airCell(0, 0, 0);
        }
    }

    private void setFluid(int x, int y){
        if(cells_new.containsKey(new Point(x,y))){
            Cell current = cells_new.get(new Point(x,y));
            cells_new.put(new Point(x,y),Cell.fluidCell(current.xVelocity,current.yVelocity));
        }else{
            cells_new.put(new Point(x,y),Cell.fluidCell(-1,0));
        }
    }

    private void setAir(int x, int y, int depth){
        if(cells_new.containsKey(new Point(x,y))){
            Cell current = cells_new.get(new Point(x,y));
            cells_new.put(new Point(x,y),Cell.airCell(current.xVelocity,current.yVelocity,depth));
        }else{
            cells_new.put(new Point(x,y),Cell.airCell(0,0, depth));
        }
    }

    //----------VARIABLE METHODS-----------------
    private Vector2D getVelocity(Vector2D position){
        double xVelocity = getXVelocity(position);
        double yVelocity = getYVelocity(position);
        return new Vector2D(xVelocity,yVelocity);
    }

    private double getXVelocity(Vector2D position){
        Vector2D translatedPosition = position.subtract(new Vector2D(0,0.5));
        Vector2D residual = residuals(translatedPosition);
        Point floor = floor(translatedPosition);
        Cell c00 = getOldSafe(floor.x,floor.y);
        Cell c01 = getOldSafe(floor.x+1,floor.y);
        Cell c10 = getOldSafe(floor.x,floor.y+1);
        Cell c11 = getOldSafe(floor.x+1,floor.y+1);
        return interpolate2D(c00.xVelocity,c01.xVelocity,c10.xVelocity,c11.xVelocity,residual.getX(),residual.getY());
    }

    private double getYVelocity(Vector2D position){
        Vector2D translatedPosition = position.subtract(new Vector2D(0.5,0));
        Vector2D residual = residuals(translatedPosition);
        Point floor = floor(translatedPosition);
        Cell c00 = getOldSafe(floor.x,floor.y);
        Cell c01 = getOldSafe(floor.x+1,floor.y);
        Cell c10 = getOldSafe(floor.x,floor.y+1);
        Cell c11 = getOldSafe(floor.x+1,floor.y+1);
        return interpolate2D(c00.yVelocity,c01.yVelocity,c10.yVelocity,c11.yVelocity,residual.getX(),residual.getY());
    }

    private double interpolate2D(double c00, double c01, double c10, double c11, double dx, double dy){
        double bottom = c01 * dx + c00 * (1.0 - dx);
        double top = c11 * dx + c10 * (1.0 - dx);
        return top * dy + bottom * (1.0 - dy);
    }

    private Vector2D residuals(Vector2D v){
        return new Vector2D(v.getX()-Math.floor(v.getX()), v.getY()-Math.floor(v.getY()));
    }

    private Point floor(Vector2D v){
        return new Point((int)Math.floor(v.getX()), (int)Math.floor(v.getY()));
    }

    //----------UTILITY METHODS------------------
    private void timestamp(String message){
        System.out.println(message+": "+(System.nanoTime()-prevTime)/1000000000.);
        prevTime = System.nanoTime();
    }
}

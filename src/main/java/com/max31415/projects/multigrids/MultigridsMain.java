package com.max31415.projects.multigrids;

import com.max31415.util.linearAlgebra.SparseMatrix;

public class MultigridsMain {
    public static final double RATE = -1;

    public static void main(String[] args) {
        System.out.println(SparseMatrix.squareMatrix(10));
//        double[][] b = new double[10][10];
//        b[3][3] = 1;
//        b[6][6] = -1;
//        System.out.println(asGrid(b,10,10));
//        double[][] answer = solve(b,10,10);

    }
    static String asGrid(double[][] input, int width, int height){
        StringBuilder output = new StringBuilder();
        for(int y = 0;y<height;y++){
            for(int x = 0;x<width;x++){
                output.append(String.format("% 06.2f",input[x][y]));
                if(x!=width) output.append("\t");
            }
            output.append("\n");
        }
        return output.toString();
    }
    static double[][] solve(double[][] b, int width, int height) {

        double[][] phi = new double[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                phi[i][j] = b[i][j];
            }
        }
        for (int i = 0; i < 100; i++) {
            cycle(b, phi, width, height);
            System.out.println(asGrid(phi,10,10));
        }
        return phi;
    }

    static void cycle(double[][] b, double[][] phi, int width, int height) {
        //central area
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                double rho = (phi[x + 1][y] + phi[x][y + 1] + phi[x - 1][y] + phi[x][y - 1]) * RATE;
                phi[x][y] = (b[x][y] - rho) / (1.0 - 4 * RATE);
            }
        }
        //x = 0 side
        for (int y = 1; y < height - 1; y++) {
            double rho = (
                    phi[1][y] * 2 +
                    phi[0][y + 1] +
                    phi[0][y - 1]
            ) * RATE;
            phi[0][y] = (b[0][y] - rho) / (1.0 - 4 * RATE);
        }
        //y = 0 side
        for (int x = 1; x < width - 1; x++) {
            double rho = (
                    phi[x + 1][0] +
                    phi[x][1] * 2 +
                    phi[x - 1][0]
            ) * RATE;
            phi[x][0] = (b[x][0] - rho) / (1.0 - 4 * RATE);
        }
        //x = width side
        for (int y = 1; y < height - 1; y++) {
            double rho = (
                    phi[width - 2][y] * 2 +
                    phi[width - 1][y + 1] +
                    phi[width - 1][y - 1]
            ) * RATE;
            phi[width - 1][y] = (b[width - 1][y] - rho) / (1.0 - 4 * RATE);
        }
        //y = height side
        for (int x = 1; x < width - 1; x++) {
            double rho = (
                    phi[x + 1][height - 1] +
                    phi[x - 1][height - 1] +
                    phi[x][height - 2] * 2
            ) * RATE;
            phi[x][height - 1] = (b[x][height - 1] - rho) / (1.0 - 4 * RATE);
        }
        double rho;
        //(0,0)
        rho = (phi[0][1] + phi[1][0]) * 2 * RATE;
        phi[0][0] = (b[0][0] - rho) / (1.0 - 4 * RATE);
        //(width,0)
        rho = (phi[width - 1][1] + phi[width-2][0]) * 2 * RATE;
        phi[width - 1][0] = (b[width - 1][0] - rho) / (1.0 - 4 * RATE);
        //(0,height)
        rho = (phi[0][height-2] + phi[1][height - 1]) * 2 * RATE;
        phi[0][height - 1] = (b[0][height - 1] - rho) / (1.0 - 4 * RATE);
        //(width,height)
        rho = (phi[width - 1][height-2] + phi[width-2][height - 1]) * 2 * RATE;
        phi[width - 1][height - 1] = (b[width - 1][height - 1] - rho) / (1.0 - 4 * RATE);
    }
}

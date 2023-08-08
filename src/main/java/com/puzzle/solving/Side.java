package com.puzzle.solving;

import com.puzzle.grid.Orientation;

import java.util.Arrays;

class Side {

    public Piece parentPiece;
    public Orientation orientation;
    public int[] averageRed;
    public int[] averageGreen;
    public int[] averageBlue;

    int blockHeight = 20;

    public Side() {}

    int compareDeviation(Side other) {

        int sumOfDeviation = 0;

        for (int i = 0; i < averageRed.length; i++) {
            sumOfDeviation += Math.abs(this.averageRed[i] - other.averageRed[i]);
        }
        for (int i = 0; i < averageGreen.length; i++) {
            sumOfDeviation += Math.abs(this.averageGreen[i] - other.averageGreen[i]);
        }
        for (int i = 0; i < averageBlue.length; i++) {
            sumOfDeviation += Math.abs(this.averageBlue[i] - other.averageBlue[i]);
        }

        return sumOfDeviation;
    }

    @Override
    public String toString() {
        return "Side{" +
                "orientation=" + orientation +
                //", averageRed=" + Arrays.toString(averageRed) +
                //", averageGreen=" + Arrays.toString(averageGreen) +
                //", averageBlue=" + Arrays.toString(averageBlue) +
                //", blockHeight=" + blockHeight +
                '}';
    }
}
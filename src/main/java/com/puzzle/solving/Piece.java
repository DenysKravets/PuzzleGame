package com.puzzle.solving;

import com.puzzle.grid.PuzzlePiece;

import java.util.ArrayList;

class Piece {

    public boolean placed = false;

    public PuzzlePiece puzzlePiece;

    public Piece topPiece;
    public Piece rightPiece;
    public Piece bottomPiece;
    public Piece leftPiece;

    public Side top;
    public Side right;
    public Side bottom;
    public Side left;

    public Piece(ArrayList<Side> sides) {
        for(Side side: sides) {
            side.parentPiece = this;
            switch (side.orientation) {
                case TOP: {
                    top = side;
                    break;
                }
                case RIGHT: {
                    right = side;
                    break;
                }
                case BOTTOM: {
                    bottom = side;
                    break;
                }
                case LEFT: {
                    left = side;
                    break;
                }
            }
        }
    }

    public ArrayList<Similarity> getSimilaritiesWith(Piece other) {

        ArrayList<Similarity> similarities = new ArrayList<>();

        Similarity similarity = new Similarity();
        similarity.side1 = this.top;
        similarity.side2 = other.bottom;
        similarity.value = similarity.side1.compareDeviation(similarity.side2);

        similarities.add(similarity);

        similarity = new Similarity();
        similarity.side1 = this.right;
        similarity.side2 = other.left;
        similarity.value = similarity.side1.compareDeviation(similarity.side2);

        similarities.add(similarity);

        similarity = new Similarity();
        similarity.side1 = this.bottom;
        similarity.side2 = other.top;
        similarity.value = similarity.side1.compareDeviation(similarity.side2);

        similarities.add(similarity);

        similarity = new Similarity();
        similarity.side1 = this.left;
        similarity.side2 = other.right;
        similarity.value = similarity.side1.compareDeviation(similarity.side2);

        similarities.add(similarity);

        return similarities;
    }

    @Override
    public String toString() {
        return "Piece{" +
                "placed=" + placed +
                ", top=" + top +
                ", right=" + right +
                ", bottom=" + bottom +
                ", left=" + left +
                '}';
    }
}

package com.puzzle.grid;

import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;

public class PuzzlePiece implements Serializable {

    private static final long serialVersionUID = 2L;

    public int id;
    public int row;
    public int originalRow;
    public int column;
    public int originalColumn;
    public Orientation orientation;
    public Orientation originalOrientation;
    public transient BufferedImage image;
    public transient ImageView imageView;
    public boolean usedInReconstruction = false;
    public String imageType;

    public PuzzlePiece() {}

    public PuzzlePiece(int id, int row, int column, Orientation orientation, BufferedImage image, String imageType) {
        this.id = id;
        this.row = row;
        this.column = column;
        this.orientation = orientation;
        this.image = image;
        this.imageType = imageType;

        this.originalRow = this.row;
        this.originalColumn = this.column;
        this.originalOrientation = this.orientation;
    }

    public PuzzlePiece(int id, int row, int column, Orientation orientation, BufferedImage image) {
        this.id = id;
        this.row = row;
        this.column = column;
        this.orientation = orientation;
        this.image = image;

        this.originalRow = this.row;
        this.originalColumn = this.column;
        this.originalOrientation = this.orientation;
    }

    public void update() {
        try {

            String id = this.imageView.idProperty().get();
            String columnRow = id.split("_")[1];
            int column = Character.getNumericValue(columnRow.charAt(0)) - 1;
            int row = Character.getNumericValue(columnRow.charAt(1)) - 4 - 1;
            this.column = column;
            this.row = row;

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(this.image, this.imageType, os);
            InputStream fis = new ByteArrayInputStream(os.toByteArray());
            this.imageView.setImage(new javafx.scene.image.Image(fis));

            switch (this.orientation) {
                case TOP: {
                    this.imageView.setRotate(0);
                    break;
                }
                case RIGHT: {
                    this.imageView.setRotate(90);
                    break;
                }
                case BOTTOM: {
                    this.imageView.setRotate(180);
                    break;
                }
                case LEFT: {
                    this.imageView.setRotate(270);
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateNoRender() {

        try {

            String id = this.imageView.idProperty().get();
            String columnRow = id.split("_")[1];
            int column = Character.getNumericValue(columnRow.charAt(0)) - 1;
            int row = Character.getNumericValue(columnRow.charAt(1)) - 4 - 1;
            this.column = column;
            this.row = row;



        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void rotate() {
        switch (this.orientation) {
            case TOP: {
                this.orientation = Orientation.RIGHT;
                break;
            }
            case RIGHT: {
                this.orientation = Orientation.BOTTOM;
                break;
            }
            case BOTTOM: {
                this.orientation = Orientation.LEFT;
                break;
            }
            case LEFT: {
                this.orientation = Orientation.TOP;
                break;
            }
        }
    }

    @Override
    public String toString() {
        return "PuzzlePiece{" +
                "id=" + id +
                ", row=" + row +
                ", originalRow=" + originalRow +
                ", column=" + column +
                ", originalColumn=" + originalColumn +
                ", orientation=" + orientation +
                ", originalOrientation=" + originalOrientation +
                ", imageView=" + imageView +
                '}';
    }
}

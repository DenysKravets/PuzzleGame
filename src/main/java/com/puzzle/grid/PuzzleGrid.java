package com.puzzle.grid;

import com.puzzle.Main;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

public class PuzzleGrid implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final int GRID_SIZE = 32;
    private static final int PUZZLE_PIECE_WIDTH = 200;
    private static final int PUZZLE_PIECE_HEIGHT = 200;

    public transient BufferedImage originalImage;
    public String imageType = null;
    public ArrayList<PuzzlePiece> puzzlePieces;

    public PuzzleGrid(BufferedImage image, String imageType) {
        this.imageType = imageType;
        originalImage = image;
        puzzlePieces = createPuzzlePieces(createSubImages(image));
        assignGridElements(puzzlePieces);

    }

    private ArrayList<BufferedImage> createSubImages(BufferedImage image) {
        ArrayList<BufferedImage> returnImages = new ArrayList<>(GRID_SIZE / 2);

        int width = 4;
        int height = 4;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                BufferedImage tempBufferedImage = image.getSubimage(j * PUZZLE_PIECE_WIDTH,
                        i * PUZZLE_PIECE_HEIGHT, PUZZLE_PIECE_WIDTH, PUZZLE_PIECE_HEIGHT);
                returnImages.add(tempBufferedImage);
            }
        }

        return returnImages;
    }

    private ArrayList<PuzzlePiece> createPuzzlePieces(ArrayList<BufferedImage> images) {
        ArrayList<PuzzlePiece> returnPuzzlePieces = new ArrayList<>();

        // Populate right side of grid with images
        int width = 4;
        int height = 4;
        Iterator<BufferedImage> imageIterator = images.iterator();
        int id = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                PuzzlePiece tempPuzzlePiece = new PuzzlePiece(id, j, i,
                        Orientation.TOP, imageIterator.next(), this.imageType);
                returnPuzzlePieces.add(tempPuzzlePiece);
                id++;
            }
        }

        return returnPuzzlePieces;
    }

    private void assignGridElements(ArrayList<PuzzlePiece> puzzlePieces) {

        puzzlePieces.forEach(puzzlePiece -> {
            Random random = new Random();
            int side = random.nextInt(4);

            switch (side) {
                case 0: {
                    puzzlePiece.orientation = Orientation.TOP;
                    break;
                }
                case 1: {
                    puzzlePiece.orientation = Orientation.RIGHT;
                    break;
                }
                case 2: {
                    puzzlePiece.orientation = Orientation.BOTTOM;
                    break;
                }
                case 3: {
                    puzzlePiece.orientation = Orientation.LEFT;
                    break;
                }
            }
        });

        ArrayList<PuzzlePiece> shuffledArray = new ArrayList<>(puzzlePieces);
        Collections.shuffle(shuffledArray);

        Iterator<PuzzlePiece> puzzlePieceIterator = shuffledArray.iterator();

        int rowOffset = 4;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {

                PuzzlePiece puzzlePiece = puzzlePieceIterator.next();
                int row = j + 1 + rowOffset;
                int column = i + 1;
                String elementName = "#grid_" + Integer.toString(column) + Integer.toString(row);
                ImageView imageView = ((ImageView) Main.mainStage.getScene().lookup(elementName));
                puzzlePiece.imageView = imageView;
            }
        }

    }

    public void restoreOriginalImages() {

        ArrayList<BufferedImage> images = createSubImages(originalImage);
        for (int i = 0; i < puzzlePieces.size(); i++) {
            PuzzlePiece puzzlePiece = puzzlePieces.get(i);
            puzzlePiece.image = images.get(puzzlePiece.id);
        }

    }

    public void restoreCurrentImageViews() {

        int rowOffset = 4;
        for (PuzzlePiece puzzlePiece : puzzlePieces) {

            int row = puzzlePiece.row + rowOffset + 1;
            int column = puzzlePiece.column + 1;
            String elementName = "#grid_" + Integer.toString(column) + Integer.toString(row);
            ImageView imageView = ((ImageView) Main.mainStage.getScene().lookup(elementName));
            puzzlePiece.imageView = imageView;

        }

    }

    public PuzzlePiece findPuzzlePieceByImageView(ImageView imageView) {
        for (int i = 0; i < puzzlePieces.size(); i++) {
            if (puzzlePieces.get(i).imageView == imageView)
                return puzzlePieces.get(i);
        }
        return null;
    }

    @Override
    public String toString() {
        return "PuzzleGrid{" +
                "originalImage=" + originalImage +
                ", puzzlePieces=" + puzzlePieces +
                '}';
    }
}

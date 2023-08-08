package com.puzzle.solving;

import com.puzzle.Main;
import com.puzzle.grid.Orientation;
import com.puzzle.grid.PuzzleGrid;
import com.puzzle.grid.PuzzlePiece;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;

public class Solver {

    public static void main(String... args) throws Exception {

        Solver solver = new Solver();

        URL url = Solver.class.getResource("/images/Cecil.jpg");
        assert url != null;
        BufferedImage image = ImageIO.read(url);

        System.out.println("Height: " + image.getHeight());
        System.out.println("Width: " + image.getWidth());

        int[] RGB = solver.parseRGB(image.getRGB(155, 140));
        solver.printRGB(RGB);

        BufferedImage image1 = image.getSubimage(200, 200, 200, 200);
        BufferedImage image2 = image.getSubimage(0, 200, 200, 200);

        Piece piece1 = new Piece(solver.createSides(image1));
        Piece piece2 = new Piece(solver.createSides(image2));

        ArrayList<Similarity> similarities = piece1.getSimilaritiesWith(piece2);

        similarities.forEach(System.out::println);

    }

    public void solve(PuzzleGrid grid, int precision) {

        ArrayList<Piece> pieces = parseGrid(grid);
        ArrayList<Similarity> similarities = calculateSimilarities(pieces);
        similarities.forEach(similarity -> {
            //System.out.println(similarity.value);
        });
        //System.out.println("Number of matches: "
        //        + similarities.stream().filter(similarity -> similarity.value < precision).count());

        similarities = (ArrayList<Similarity>) similarities.stream()
                .filter(similarity -> similarity.value < precision).collect(Collectors.toList());

        for (int i = 0; i <similarities.size(); i++) {
            Similarity similarity = similarities.get(i);

            Side side1 = similarity.side1;
            Side side2 = similarity.side2;

            Piece piece1 = side1.parentPiece;
            Piece piece2 = side2.parentPiece;

            if (piece1 == piece2)
                throw new RuntimeException("\nPiece matched with itself!\n" + piece1 + "\n");

            switch (side1.orientation) {
                case TOP: {
                    piece1.topPiece = piece2;
                    break;
                }
                case RIGHT: {
                    piece1.rightPiece = piece2;
                    break;
                }
                case BOTTOM: {
                    piece1.bottomPiece = piece2;
                    break;
                }
                case LEFT: {
                    piece1.leftPiece = piece2;
                    break;
                }
            }

            switch (side2.orientation) {
                case TOP: {
                    piece2.topPiece = piece1;
                    break;
                }
                case RIGHT: {
                    piece2.rightPiece = piece1;
                    break;
                }
                case BOTTOM: {
                    piece2.bottomPiece = piece1;
                    break;
                }
                case LEFT: {
                    piece2.leftPiece = piece1;
                    break;
                }
            }
        }

       //System.out.println(similarities);
        //System.out.println(pieces);

        constructGrid(pieces.get(0), 0, 0);
        for (Piece[] subArray: this.grid) {
            //System.out.println(Arrays.toString(subArray));
        }

        assignImagesToImageViews(grid);

    }

    private void assignImagesToImageViews(PuzzleGrid puzzleGrid) {

        ArrayList<Piece> pieces = new ArrayList<>();

        int x = 0, y = 0;

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] != null) {
                    x = j;
                    y = i;
                    pieces.add(grid[i][j]);
                }
            }
        }

        int rowOffset = 4;
        int imageRow = 3;
        int imageColumn = 0;
        Iterator<Piece> pieceIterator = pieces.iterator();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                try {
                    Piece piece = pieceIterator.next();

                    int row = imageRow + rowOffset + 1;
                    int column = imageColumn + 1;
                    //System.out.println("#grid_" + Integer.toString(column) + Integer.toString(row));
                    String elementName = "#grid_" + Integer.toString(column) + Integer.toString(row);
                    ImageView imageView = ((ImageView) Main.mainStage.getScene().lookup(elementName));
                    //System.out.println(piece);
                    piece.puzzlePiece.imageView = imageView;
                    piece.puzzlePiece.orientation = Orientation.TOP;
                    piece.puzzlePiece.usedInReconstruction = true;

                } catch (Exception e) {
                    //e.printStackTrace();
                }

                imageRow--;
            }
            imageColumn++;
            imageRow = 3;
        }



    }

    private final Piece[][] grid = new Piece[256][256];

    private void constructGrid(Piece piece, int x, int y) {


        if (!piece.placed) {

            grid[128 - y][128 - x] = piece;
            piece.placed = true;

            if (piece.topPiece != null) {
                constructGrid(piece.topPiece, x, y + 1);
            }
            if (piece.rightPiece != null) {
                constructGrid(piece.rightPiece, x + 1, y);
            }
            if (piece.bottomPiece != null) {
                constructGrid(piece.bottomPiece, x, y - 1);
            }
            if (piece.leftPiece != null) {
                constructGrid(piece.leftPiece, x - 1, y);
            }

        }

    }

    private ArrayList<Similarity> calculateSimilarities(ArrayList<Piece> pieces) {

        ArrayList<Similarity> similarities = new ArrayList<>();

        for (int i = 0; i < pieces.size(); i++) {
            Piece thisPiece = pieces.get(i);
            for (int j = 0; j < pieces.size(); j++) {
                // Don't compare with itself
                if (i != j) {
                    Piece otherPiece = pieces.get(j);
                    similarities.addAll(thisPiece.getSimilaritiesWith(otherPiece));
                }
            }
        }

        return similarities;
    }

    private ArrayList<Piece> parseGrid(PuzzleGrid grid) {
        ArrayList<Piece> pieces = new ArrayList<>();

        for (PuzzlePiece puzzlePiece: grid.puzzlePieces) {
            ArrayList<Side> sides = createSides(puzzlePiece.image);
            Piece piece = new Piece(sides);
            piece.puzzlePiece = puzzlePiece;
            pieces.add(piece);
        }

        return pieces;
    }

    private int[] parseRGB(int color) {
        int[] RGB = new int[3];
        RGB[2] = color & 0xff;
        RGB[1] = (color & 0xff00) >> 8;
        RGB[0] = (color & 0xff0000) >> 16;
        return RGB;
    }

    private void printRGB(int[] RGB) {
        System.out.println("Red: " + RGB[0]);
        System.out.println("Green: " + RGB[1]);
        System.out.println("Blue: " + RGB[2]);
    }

    public ArrayList<Side> createSides(BufferedImage image) {

        int height = 10;
        int width = 2;
        int count = 20;

        ArrayList<Side> sides = new ArrayList<>();

        // TOP
        Side side = new Side();
        int[] avgRed = new int[count];
        int[] avgGreen = new int[count];
        int[] avgBlue = new int[count];

        int redSum = 0;
        int greenSum = 0;
        int blueSum = 0;

        for (int i = 0; i < count; i++) {
            for (int j = 0; j < height; j++) {
                for (int k = 0; k < width; k++) {
                    int[] rgb = parseRGB(image.getRGB(j + height * i, k));
                    redSum += rgb[0];
                    greenSum += rgb[1];
                    blueSum += rgb[2];
                }
            }
            avgRed[i] = redSum / (height * width);
            avgGreen[i] = greenSum / (height * width);
            avgBlue[i] = blueSum / (height * width);

            redSum = 0;
            greenSum = 0;
            blueSum = 0;
        }

        side.averageRed = avgRed;
        side.averageGreen = avgGreen;
        side.averageBlue = avgBlue;

        side.orientation = Orientation.TOP;

        sides.add(side);

        // RIGHT
        side = new Side();
        avgRed = new int[count];
        avgGreen = new int[count];
        avgBlue = new int[count];

        for (int i = 0; i < count; i++) {
            for (int j = 0; j < height; j++) {
                for (int k = 0; k < width; k++) {
                    int[] rgb = parseRGB(image.getRGB(199 - k, j + height * i));
                    redSum += rgb[0];
                    greenSum += rgb[1];
                    blueSum += rgb[2];
                }
            }
            avgRed[i] = redSum / (height * width);
            avgGreen[i] = greenSum / (height * width);
            avgBlue[i] = blueSum / (height * width);

            redSum = 0;
            greenSum = 0;
            blueSum = 0;
        }

        side.averageRed = avgRed;
        side.averageGreen = avgGreen;
        side.averageBlue = avgBlue;

        side.orientation = Orientation.RIGHT;

        sides.add(side);

        // BOTTOM
        side = new Side();
        avgRed = new int[count];
        avgGreen = new int[count];
        avgBlue = new int[count];

        for (int i = 0; i < count; i++) {
            for (int j = 0; j < height; j++) {
                for (int k = 0; k < width; k++) {
                    int[] rgb = parseRGB(image.getRGB(j + height * i, 199 - k));
                    redSum += rgb[0];
                    greenSum += rgb[1];
                    blueSum += rgb[2];
                }
            }

            avgRed[i] = redSum / (height * width);
            avgGreen[i] = greenSum / (height * width);
            avgBlue[i] = blueSum / (height * width);

            redSum = 0;
            greenSum = 0;
            blueSum = 0;
        }

        side.averageRed = avgRed;
        side.averageGreen = avgGreen;
        side.averageBlue = avgBlue;

        side.orientation = Orientation.BOTTOM;

        sides.add(side);

        // LEFT
        side = new Side();
        avgRed = new int[count];
        avgGreen = new int[count];
        avgBlue = new int[count];

        for (int i = 0; i < count; i++) {
            for (int j = 0; j < height; j++) {
                for (int k = 0; k < width; k++) {
                    int[] rgb = parseRGB(image.getRGB(k, j + height * i));
                    redSum += rgb[0];
                    greenSum += rgb[1];
                    blueSum += rgb[2];
                }
            }
            avgRed[i] = redSum / (height * width);
            avgGreen[i] = greenSum / (height * width);
            avgBlue[i] = blueSum / (height * width);

            redSum = 0;
            greenSum = 0;
            blueSum = 0;
        }

        side.averageRed = avgRed;
        side.averageGreen = avgGreen;
        side.averageBlue = avgBlue;

        side.orientation = Orientation.LEFT;

        sides.add(side);

        return sides;
    }

}



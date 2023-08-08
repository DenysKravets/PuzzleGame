package com.puzzle.memory;

import com.puzzle.grid.PuzzleGrid;
import com.puzzle.grid.PuzzlePiece;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;

public class Loader {

    public Loader() {

    }

    public PuzzleGrid load(String name) {

        PuzzleGrid loadedGrid = null;

        try {

            String pathToSerializable = System.getProperty("user.dir") + "/saves/" + name + "/save.ser";
            FileInputStream fileInputStream = new FileInputStream(pathToSerializable);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            loadedGrid = (PuzzleGrid) objectInputStream.readObject();
            objectInputStream.close();

            String pathToImage = System.getProperty("user.dir") + "/saves/" + name + "/image." + loadedGrid.imageType;
            File file = new File(pathToImage);
            BufferedImage loadedImage = ImageIO.read(file);
            loadedGrid.originalImage = loadedImage;

            loadedGrid.restoreOriginalImages();
            loadedGrid.restoreCurrentImageViews();

            loadedGrid.puzzlePieces.forEach(PuzzlePiece::update);

        } catch (Exception e) {
            e.printStackTrace();
        }



        return loadedGrid;
    }

    public ArrayList<String> returnOptions() {
        ArrayList<String> options = new ArrayList<>();

        File file = new File(System.getProperty("user.dir") + "/saves/");
        Collections.addAll(options, file.list());

        return options;
    }

}

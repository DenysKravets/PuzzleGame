package com.puzzle.memory;

import com.puzzle.grid.PuzzleGrid;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.io.*;

public class Saver {

    public Saver() {

    }

    public void save(PuzzleGrid grid, String name) {



        try {

            File outputfile = new File(System.getProperty("user.dir") + "/saves/" + name + "/image." + grid.imageType);

            System.out.println(outputfile.toURI());

            File parentDir = outputfile.getParentFile();
            if(! parentDir.exists())
                parentDir.mkdirs();

            ImageIO.write(grid.originalImage, grid.imageType, outputfile);

            FileOutputStream fileOutputStream
                    = new FileOutputStream(System.getProperty("user.dir") + "/saves/" + name + "/save.ser");
            ObjectOutputStream objectOutputStream
                    = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(grid);
            objectOutputStream.flush();
            objectOutputStream.close();




        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

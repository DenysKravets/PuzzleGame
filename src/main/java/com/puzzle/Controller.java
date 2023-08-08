package com.puzzle;

import com.puzzle.grid.PuzzleGrid;
import com.puzzle.grid.PuzzlePiece;
import com.puzzle.memory.Loader;
import com.puzzle.memory.Saver;
import com.puzzle.solving.Solver;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;

public class Controller {

    private boolean clicked = false;

    private PuzzlePiece puzzlePieceBuffer;

    private static PuzzleGrid puzzleGrid;

    private static final Saver saver = new Saver();

    private static final Loader loader = new Loader();

    public void setImage(MouseEvent mouseEvent) {

        ImageView source = (ImageView) mouseEvent.getSource();

        String mouseType = mouseEvent.getButton().toString();
        switch (mouseType) {
            case "PRIMARY": {
                if (clicked) {
                    clicked = false;

                    PuzzlePiece oldPuzzlePiece = puzzlePieceBuffer;
                    PuzzlePiece newPuzzlePiece = puzzleGrid.findPuzzlePieceByImageView(source);

                    ImageView imageViewBuffer = newPuzzlePiece.imageView;
                    newPuzzlePiece.imageView = oldPuzzlePiece.imageView;
                    oldPuzzlePiece.imageView = imageViewBuffer;

                    oldPuzzlePiece.update();
                    newPuzzlePiece.update();

                } else {
                    clicked = true;

                    puzzlePieceBuffer = puzzleGrid.findPuzzlePieceByImageView(source);

                }

                break;
            }
            case "SECONDARY": {

                PuzzlePiece puzzlePiece = puzzleGrid.findPuzzlePieceByImageView(source);

                puzzlePiece.rotate();
                puzzlePiece.update();

                break;
            }

        }

    }

    public void start(ActionEvent actionEvent) {
        try {
            final FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter =
                    new FileChooser.ExtensionFilter("IMAGE files", "*.jpg", "*.png");
            fileChooser.getExtensionFilters().add(extFilter);
            fileChooser.setTitle("Choose Image");
            String currentDir = System.getProperty("user.dir") + File.separator;
            File currentDirfile = new File(currentDir);
            fileChooser.setInitialDirectory(currentDirfile);
            File file = fileChooser.showOpenDialog(Main.mainStage);
            URL url = null;
            if (file == null)
                url = this.getClass().getResource("/images/default.jpg");
            else
                url = file.toURI().toURL();
            assert url != null;
            BufferedImage image = ImageIO.read(url);
            image = stretchImageIfNeeded(image);
            String urlString = url.toString();
            String imageType = urlString.substring(urlString.length() - 3);
            puzzleGrid = new PuzzleGrid(image, imageType);
            puzzleGrid.puzzlePieces.forEach(PuzzlePiece::update);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private BufferedImage stretchImageIfNeeded(BufferedImage image) {

        if (image.getWidth() == 800 && image.getHeight() == 800) {
            return image;
        }

        return resizeImage(image, 800, 800);
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }

    private static Stage saveStage = null;

    public void openSaveMenu(ActionEvent actionEvent) {

        try {
            saveStage = new Stage();
            Parent root = FXMLLoader.load(this.getClass().getResource("/Save.fxml"));
            root.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent");
            saveStage.setTitle("Save");
            //stage.initStyle(StageStyle.UNDECORATED);
            saveStage.initModality(Modality.WINDOW_MODAL);
            saveStage.initOwner(Main.mainStage);
            saveStage.setResizable(false);
            saveStage.setScene(new Scene(root));
            saveStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void savePuzzle(ActionEvent actionEvent) {
        String name = ((javafx.scene.control.TextField) saveStage.getScene().lookup("#saveName")).getText();

        saver.save(Controller.puzzleGrid, name);

        saveStage.close();
    }

    private static Stage loadStage = null;

    public void openLoadMenu(ActionEvent actionEvent) {

        try {
            loadStage = new Stage();
            Parent root = FXMLLoader.load(this.getClass().getResource("/Load.fxml"));
            root.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent");
            loadStage.setTitle("Load");
            //stage.initStyle(StageStyle.UNDECORATED);
            loadStage.initModality(Modality.WINDOW_MODAL);
            loadStage.initOwner(Main.mainStage);
            loadStage.setResizable(false);
            loadStage.setScene(new Scene(root));
            loadStage.show();

            VBox container = (VBox) loadStage.getScene().lookup("#loadOptionsContainer");
            ArrayList<String> options = loader.returnOptions();
            container.getChildren().remove(0);
            options.forEach(option -> {
                container.getChildren().add(getLoadOptionElement(option));
                container.getChildren().add(getLoadSeparator());
            });

            // Remove last separator for visual clarity
            int separatorPosition = container.getChildren().size() - 1;
            container.getChildren().remove(separatorPosition);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private HBox getLoadOptionElementDeprecated(String name) {

        HBox hBox = new HBox();
        hBox.setPrefHeight(93.0);
        hBox.setPrefWidth(600.0);

        javafx.scene.control.Label label = new javafx.scene.control.Label(name);
        label.setId("loadName");
        label.setPrefHeight(97.0);
        label.setPrefWidth(272.0);

        javafx.scene.control.Button button = new javafx.scene.control.Button("Load");
        button.setMnemonicParsing(false);
        button.setPrefHeight(86.0);
        button.setPrefWidth(157.0);
        button.setOnAction(this::loadPuzzle);

        hBox.getChildren().addAll(label, button);

        return hBox;
    }

    private Separator getLoadSeparator() {
        Separator separator = new Separator();
        separator.setPrefWidth(200);
        return separator;
    }

    private HBox getLoadOptionElement(String name) {

        HBox hbox = new HBox();
        javafx.scene.control.Label loadName = new javafx.scene.control.Label(name);
        javafx.scene.control.Button loadButton = new javafx.scene.control.Button("Load");

        loadName.setPrefHeight(93);
        loadName.setPrefWidth(424);
        loadName.setId("loadName");
        loadName.setPadding(new javafx.geometry.Insets(20, 20, 20, 20));
        loadName.setFont(new javafx.scene.text.Font(22));

        loadButton.setPrefHeight(39);
        loadButton.setPrefWidth(109);
        HBox.setMargin(loadButton, new javafx.geometry.Insets(20, 0, 20, 20));
        loadButton.setFont(new javafx.scene.text.Font(24));
        loadButton.setOnAction(this::loadPuzzle);

        hbox.getChildren().addAll(loadName, loadButton);

        return hbox;
    }

    public void loadPuzzle(ActionEvent actionEvent) {

        String name = ((javafx.scene.control.Label) ((javafx.scene.control.Button)
                actionEvent.getSource()).getParent().lookup("#loadName")).getText();


        puzzleGrid = loader.load(name);

        loadStage.close();
    }

    public void checkCorrectness(ActionEvent actionEvent) {

        setFeedback(puzzleSolved());

    }

    public boolean puzzleSolved() {

        boolean puzzleSolved = true;
        for (PuzzlePiece puzzlePiece: puzzleGrid.puzzlePieces) {
            if (puzzlePiece.originalOrientation != puzzlePiece.orientation
                    || puzzlePiece.originalRow != puzzlePiece.row
                    || puzzlePiece.originalColumn != puzzlePiece.column) {
                puzzleSolved = false;
                break;
            }
        }

        return puzzleSolved;
    }

    private void setFeedback(boolean check) {
        if (check) {
            String style = ((Pane) Main.mainStage.getScene().lookup("#feedback")).getStyle();
            String newStyle = style + "-fx-background-color: green;";
            ((Pane) Main.mainStage.getScene().lookup("#feedback")).setStyle(newStyle);
        } else {
            String style = ((Pane) Main.mainStage.getScene().lookup("#feedback")).getStyle();
            String newStyle = style + "-fx-background-color: red;";
            ((Pane) Main.mainStage.getScene().lookup("#feedback")).setStyle(newStyle);
        }
    }

    public void attemptSolve(ActionEvent actionEvent) {
        try {
            Solver solver = new Solver();
            //Solver.main((String) null);

            boolean solvingSuccessful = false;
            String autoSaveName = "solve_autosave";

            saver.save(puzzleGrid, autoSaveName);

            for (int i = 0; i < 10000; i += 1) {
                solver.solve(puzzleGrid, i);
                puzzleGrid.puzzlePieces.forEach(PuzzlePiece::updateNoRender);
                if (puzzleSolved()) {
                    puzzleGrid.puzzlePieces.forEach(PuzzlePiece::update);
                    solvingSuccessful = true;
                    break;
                }
            }

            if (!solvingSuccessful) {
                puzzleGrid = loader.load(autoSaveName);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}

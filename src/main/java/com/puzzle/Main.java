package com.puzzle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.HashMap;

public class Main extends Application {

    public static void main(String... args) {
        Application.launch(args);
    }

    public static Stage mainStage = null;
    public static HashMap<String, Stage> mainStageChildren = new HashMap<>();

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(this.getClass().getResource("/Main.fxml"));
        root.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent");
        stage.setTitle("Puzzle Game");
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        //stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/images/meiji_logo.png")));
        stage.show();

        // Set main stage
        mainStage = stage;
        // Kill all children when main dies
        mainStage.setOnCloseRequest(windowEvent -> {
            mainStageChildren.forEach((key, value) -> {value.close();});
        });

    }
}
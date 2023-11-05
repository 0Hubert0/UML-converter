package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    public static Scene scene;
    public static Parent root = new GridPane();

    @Override
    public void start(Stage primaryStage) throws Exception {
        root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("Intro.fxml")));

        scene = new Scene(root, 900, 600);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
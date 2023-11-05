package com.example;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class IntroController {

    @FXML
    private Button exportButton;

    @FXML
    private Button importButton;

    @FXML
    private Text title;

    @FXML
    void goToExport(ActionEvent event) throws IOException {
        Main.root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("Export.fxml")));
        
        Main.scene.setRoot(Main.root);
    }

    @FXML
    void goToImport(ActionEvent event) throws IOException {
        Main.root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("ImportLayout.fxml")));

        Main.scene.setRoot(Main.root);
    }

    Timeline synchronize = new Timeline(new KeyFrame(Duration.millis(10), event -> {
        int x = (int) ((Main.scene.getWidth() - 150) / 2);
        importButton.setLayoutX(x);
        importButton.setLayoutY(Main.scene.getHeight() / 4);
        exportButton.setLayoutX(x);
        exportButton.setLayoutY(Main.scene.getHeight() * (5.0 / 8.0));
        title.setLayoutX((Main.scene.getWidth() - 337) / 2);
        title.setLayoutY(Main.scene.getHeight() / 8);
    }));

    public void initialize() {
        synchronize.setCycleCount(-1);
        synchronize.play();
    }

}

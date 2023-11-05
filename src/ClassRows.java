package com.example;

import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.List;

public class ClassRows {
    AnchorPane row;
    Text text;
    TextField textField;
    ImageView save;
    ImageView edit;
    ImageView delete;

    public ClassRows(TextField textField, Text text, AnchorPane row, List<ImageView> recs, int i, boolean imported) {
        this.edit = recs.get(1);
        this.delete = recs.get(2);
        this.save = recs.get(0);
        this.text = text;
        this.textField = textField;
        this.row = row;
        row.getChildren().addAll(text, textField, save, edit, delete);
        if (imported) {
            save.setVisible(false);
            save.setDisable(true);
            edit.setVisible(true);
            edit.setDisable(false);
            textField.setVisible(false);
            text.setVisible(true);
            if (i != 0) {
                delete.setVisible(true);
                delete.setDisable(false);
            }
        } else {
            text.setVisible(false);
        }
        textField.setFont(Font.font(8));
        textField.setPrefHeight(10);
        textField.setPrefWidth(80);
        textField.setLayoutX(2);
        text.setLayoutX(textField.getLayoutX() + 2);
        text.setLayoutY(textField.getLayoutY() + 14);
    }

    public void enter(int i, ClassDrawings classDrawing) {
        if (i == 0) {
            classDrawing.className = textField.getText();
            text.setLayoutX((save.getX()+20 - text.getText().length() * 7) / 2);
        }
        if (i != 0) {
            delete.setVisible(true);
            delete.setDisable(false);
        }
        text.setText(textField.getText());
        save.setVisible(false);
        save.setDisable(true);
        edit.setVisible(true);
        edit.setDisable(false);
        edit.setLayoutY(save.getLayoutY());
        delete.setLayoutY(save.getLayoutY());
        textField.setVisible(false);
        text.setVisible(true);
    }

}

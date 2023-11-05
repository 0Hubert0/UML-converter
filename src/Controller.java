package com.example;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    public void stretchDrawing(Rectangle nameSpace, Rectangle attributeSpace, Rectangle methodSpace, ClassDrawings classDrawing, TextField textField){
        nameSpace.setWidth(nameSpace.getWidth()+10);
        attributeSpace.setWidth(nameSpace.getWidth());
        methodSpace.setWidth(nameSpace.getWidth());
        classDrawing.images.get(0).setX((nameSpace.getWidth()-22)/2);
        classDrawing.images.get(1).setX((nameSpace.getWidth()-22)/2);
        classDrawing.images.get(2).setX(nameSpace.getWidth()-17);
        classDrawing.rectangles.get(0).setX((nameSpace.getWidth()-22)/2);
        classDrawing.rectangles.get(1).setX(nameSpace.getWidth()-5);
        classDrawing.rectangles.get(2).setX(nameSpace.getWidth()-5);
        textField.setPrefWidth(textField.getWidth()+10);
        classDrawing.name.save.setX(nameSpace.getWidth()-20);
        classDrawing.name.delete.setX(nameSpace.getWidth()-20);
        classDrawing.name.edit.setX(nameSpace.getWidth()-20);
        for (int i = 0; i <classDrawing.attributeRows.size(); i++) {
            classDrawing.attributeRows.get(i).save.setX(nameSpace.getWidth()-20);
            classDrawing.attributeRows.get(i).delete.setX(nameSpace.getWidth()-20);
            classDrawing.attributeRows.get(i).edit.setX(nameSpace.getWidth()-40);
        }
        for (int i = 0; i <classDrawing.methodRows.size(); i++) {
            classDrawing.methodRows.get(i).save.setX(nameSpace.getWidth()-20);
            classDrawing.methodRows.get(i).delete.setX(nameSpace.getWidth()-20);
            classDrawing.methodRows.get(i).edit.setX(nameSpace.getWidth()-40);
        }
    }

    public void toggleRectangles(boolean show, List<ClassDrawings> classDrawings) {
        if (show) {
            for (ClassDrawings classDrawing : classDrawings) {
                for (int j = 0; j < classDrawing.rectangles.size(); j++) {
                    if (!classDrawing.rectangles.get(j).isDisabled()) classDrawing.rectangles.get(j).setVisible(true);
                }
                for (int i = 0; i <classDrawing.images.size(); i++) {
                    if(!classDrawing.images.get(i).isDisabled()) classDrawing.images.get(i).setVisible(true);
                }
                for (int i = 0; i <classDrawing.attributeRows.size(); i++) {
                    classDrawing.attributeRows.get(i).delete.setVisible(!classDrawing.attributeRows.get(i).delete.isDisabled());
                    classDrawing.attributeRows.get(i).save.setVisible(!classDrawing.attributeRows.get(i).save.isDisabled());
                    classDrawing.attributeRows.get(i).edit.setVisible(!classDrawing.attributeRows.get(i).edit.isDisabled());
                }
                for (int i = 0; i <classDrawing.methodRows.size(); i++) {
                    classDrawing.methodRows.get(i).delete.setVisible(!classDrawing.methodRows.get(i).delete.isDisabled());
                    classDrawing.methodRows.get(i).save.setVisible(!classDrawing.methodRows.get(i).save.isDisabled());
                    classDrawing.methodRows.get(i).edit.setVisible(!classDrawing.methodRows.get(i).edit.isDisabled());
                }
                classDrawing.name.save.setVisible(!classDrawing.name.save.isDisabled());
                classDrawing.name.edit.setVisible(!classDrawing.name.edit.isDisabled());
            }
        } else {
            for (ClassDrawings classDrawing : classDrawings) {
                for (int j = 0; j < classDrawing.rectangles.size(); j++) {
                    classDrawing.rectangles.get(j).setVisible(false);
                }
                for (int i = 0; i <classDrawing.images.size(); i++) {
                    classDrawing.images.get(i).setVisible(false);
                }
                for (int i = 0; i <classDrawing.attributeRows.size(); i++) {
                    classDrawing.attributeRows.get(i).delete.setVisible(false);
                    classDrawing.attributeRows.get(i).save.setVisible(false);
                    classDrawing.attributeRows.get(i).edit.setVisible(false);
                }
                for (int i = 0; i <classDrawing.methodRows.size(); i++) {
                    classDrawing.methodRows.get(i).delete.setVisible(false);
                    classDrawing.methodRows.get(i).save.setVisible(false);
                    classDrawing.methodRows.get(i).edit.setVisible(false);
                }
                classDrawing.name.save.setVisible(false);
                classDrawing.name.edit.setVisible(false);
            }
        }
    }

    public List<ImageView> addRectangle(Rectangle nameSpace, TextField temp, Text tempText, int sideButtons, AnchorPane row, int i, ClassDrawings classDrawing, int expansion, AnchorPane classSpace, AnchorPane selectedOne, boolean imported) {
        List<ImageView> buttons = new ArrayList<>();
        ImageView save = new ImageView(String.valueOf(new File("file:tick.png")));
        ImageView edit = new ImageView(String.valueOf(new File("file:edit.png")));
        ImageView delete = new ImageView(String.valueOf(new File("file:x.png")));
        save.setX(nameSpace.getWidth()-20);
        save.setY(temp.getLayoutY());
        edit.setX(nameSpace.getWidth()-40);
        edit.setY(temp.getLayoutY());
        delete.setX(nameSpace.getWidth()-20);
        delete.setY(temp.getLayoutY());
        buttons.add(save);
        buttons.add(edit);
        buttons.add(delete);
        if (i == 0) edit.setX(nameSpace.getWidth() - 20);
        edit.setVisible(false);
        edit.setDisable(true);
        delete.setVisible(false);
        delete.setDisable(true);
        save.setOnMouseEntered(event1 -> row.setCursor(Cursor.HAND));
        save.setOnMouseExited(event1 -> row.setCursor(Cursor.DEFAULT));
        save.setOnMouseClicked(event1 -> {
            if (!imported) changeSelected(selectedOne, classSpace);
            if (i == 0) {
                classDrawing.name.text.setLayoutX((nameSpace.getWidth()-classDrawing.name.text.getText().length()*7)/2);
                classDrawing.className = temp.getText();
            }
            if (i != 0) {
                delete.setVisible(true);
                delete.setDisable(false);
            }
            tempText.setText(temp.getText());
            save.setVisible(false);
            save.setDisable(true);
            edit.setVisible(true);
            edit.setDisable(false);
            edit.setLayoutY(save.getLayoutY());
            delete.setLayoutY(save.getLayoutY());
            temp.setVisible(false);
            tempText.setVisible(true);
        });

        edit.setOnMouseEntered(event2 -> row.setCursor(Cursor.HAND));
        edit.setOnMouseExited(event2 -> row.setCursor(Cursor.DEFAULT));
        edit.setOnMouseClicked(event2 -> {
            if (!imported) changeSelected(selectedOne, classSpace);
            if (i == 0) classDrawing.className = "";
            if (i != 0) {
                delete.setVisible(false);
                delete.setDisable(true);
            }
            temp.setText(tempText.getText());
            edit.setVisible(false);
            edit.setDisable(true);
            save.setVisible(true);
            save.setDisable(false);
            edit.setLayoutY(save.getLayoutY());
            delete.setLayoutY(save.getLayoutY());
            temp.setVisible(true);
            tempText.setVisible(false);
        });

        delete.setOnMouseEntered(event2 -> row.setCursor(Cursor.HAND));
        delete.setOnMouseExited(event2 -> row.setCursor(Cursor.DEFAULT));
        delete.setOnMouseClicked(event2 -> {
            if (!imported) changeSelected(selectedOne, classSpace);
            classSpace.getChildren().remove(row);
            if (i == 1) {
                for (int j = 0; j < classDrawing.attributeRows.size(); j++) {
                    if (classDrawing.attributeRows.get(j).row == row) {
                        classDrawing.attributeRows.remove(classDrawing.attributeRows.get(j));
                        for (int k = j; k < classDrawing.attributeRows.size(); k++) {
                            AnchorPane cr = classDrawing.attributeRows.get(k).row;
                            cr.setLayoutY(cr.getLayoutY() - expansion);
                        }
                        break;
                    }
                }
            } else {
                for (int j = 0; j < classDrawing.methodRows.size(); j++) {
                    if (classDrawing.methodRows.get(j).row == row) {
                        classDrawing.methodRows.remove(classDrawing.methodRows.get(j));
                        for (int k = j; k < classDrawing.methodRows.size(); k++) {
                            AnchorPane cr = classDrawing.methodRows.get(k).row;
                            cr.setLayoutY(cr.getLayoutY() - expansion);
                        }
                        break;
                    }
                }
            }
        });
        return buttons;
    }

    public void addAddButton(int x, int side, Rectangle attributeSpace, Rectangle methodSpace, AnchorPane classSpace, ClassDrawings classDrawing, Rectangle nameSpace, AnchorPane selectedOne, boolean imported, double furthestDown) {
        ImageView add;
        final double[] furthestDownFinal = {furthestDown};
        final int[] tempFurthest = new int[1];
        int expansion = side + 2;
        if (x == 1) {
            add = new ImageView(String.valueOf(new File("file:add.png")));
            add.setX((attributeSpace.getWidth() - side) / 2);
            add.setY(attributeSpace.getHeight() - 20);
        } else {
            add = new ImageView(String.valueOf(new File("file:add.png")));
            add.setX((methodSpace.getWidth() - side) / 2);
            add.setY(methodSpace.getHeight() - 20);
        }
        add.setOnMouseEntered(event -> classSpace.setCursor(Cursor.HAND));
        add.setOnMouseExited(event -> classSpace.setCursor(Cursor.DEFAULT));
        add.setOnMouseClicked(event -> {
            classDrawing.changingParametersMethod.setY(classDrawing.changingParametersMethod.getY()+expansion);
            if (!imported) changeSelected(selectedOne, classSpace);
            AnchorPane newAnchorPane = new AnchorPane();
            TextField newTextField = new TextField();
            Text newText = new Text();
            ClassRows classRows;
            if (!imported) {
                classRows = new ClassRows(newTextField, newText, newAnchorPane, addRectangle(nameSpace, newTextField, newText,
                        18, newAnchorPane, x, classDrawing, expansion, classSpace, selectedOne, false), 1, false);
            }
            else {
                classRows = new ClassRows(newTextField, newText, newAnchorPane, addRectangle(nameSpace, newTextField, newText,
                        18, newAnchorPane, x, classDrawing, expansion, classSpace, selectedOne, true), 1, false);
            }
            if (x == 1) {
                classDrawing.changingParametersAttribute.setY(classDrawing.changingParametersAttribute.getY()+expansion);
                attributeSpace.setHeight(attributeSpace.getHeight() + expansion);
                methodSpace.setHeight(methodSpace.getHeight() + expansion);
                classDrawing.images.get(1).setY(classDrawing.images.get(1).getY()+expansion);
                for (int i = 0; i < classDrawing.methodRows.size(); i++) {
                    classDrawing.methodRows.get(i).row.setLayoutY(classDrawing.methodRows.get(i).row.getLayoutY() + expansion);
                }
                if (classDrawing.attributeRows.size() == 0) classRows.row.setLayoutY(nameSpace.getHeight() + 2);
                else
                    classRows.row.setLayoutY(classDrawing.attributeRows.get(classDrawing.attributeRows.size() - 1).row.getLayoutY() + expansion);
                classDrawing.attributeRows.add(classRows);
                add.setY(classDrawing.attributeSpace.getHeight()-20);
            } else {
                methodSpace.setHeight(methodSpace.getHeight() + expansion);
                if (classDrawing.methodRows.size() == 0) newTextField.setLayoutY(attributeSpace.getHeight() + 2);
                else
                    classRows.row.setLayoutY(classDrawing.methodRows.get(classDrawing.methodRows.size() - 1).row.getLayoutY() + expansion);
                classDrawing.methodRows.add(classRows);
                add.setY(classDrawing.methodSpace.getHeight()-20);
            }
            newTextField.setOnKeyPressed(event1 -> {
                if(event1.getCode()== KeyCode.ENTER){
                    classRows.enter(1, classDrawing);
                }
                if(newTextField.getText().length()>classDrawing.theLongestPhrase){
                    stretchDrawing(nameSpace, attributeSpace, methodSpace, classDrawing, newTextField);
                    classDrawing.theLongestPhrase+=3;
                }
                else if(newTextField.getText().length()>5)newTextField.setPrefWidth(70+(newTextField.getText().length()/3)*10);
            });
            classSpace.getChildren().add(classRows.row);
            if (methodSpace.getHeight() + methodSpace.getLayoutY() > furthestDownFinal[0])
                tempFurthest[0] = (int) (methodSpace.getHeight() + methodSpace.getLayoutY() + 10);
        });
        furthestDown = tempFurthest[0];
        classDrawing.images.add(add);
        classSpace.getChildren().add(add);
    }

    public AnchorPane changeSelected(AnchorPane selectedOne, AnchorPane classSpace) {
        Color paint = new Color(0.0177, 0.2161, 0.59, 1.0);
        DropShadow dropShadow = new DropShadow(10, paint);
        dropShadow.setSpread(0.7);
        Node rec = classSpace.getChildren().get(2);
        if (selectedOne == null) {
            rec.setEffect(dropShadow);
            selectedOne = classSpace;
            selectedOne.toFront();
        }
        if (selectedOne != classSpace) {
            Node rec0 = selectedOne.getChildren().get(2);
            rec.setEffect(dropShadow);
            rec0.setEffect(null);
            selectedOne = classSpace;
            selectedOne.toFront();
        }
        return selectedOne;
    }
}

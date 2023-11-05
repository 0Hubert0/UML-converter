package com.example;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ExportController {

    HashMap<String, String> imports = new HashMap<>();
    List<ClassDrawings> classDrawings = new ArrayList<>();
    List<AnchorPane> classSpaces = new ArrayList<>();
    Stage modalWindow;
    AnchorPane selectedOne = null;
    ClassDrawings selectedClassDrawing = null;
    double furthestDown = 0, differenceInHeight;
    boolean isPressed = false, isPressedAttribute = false, isPressedMethod = false;
    Controller controller = new Controller();

    @FXML
    private Button addClassButton;

    @FXML
    private CheckBox showRectangles;

    @FXML
    private Button downloadButton;

    @FXML
    private Button exportButton;

    @FXML
    private Button previewButton;

    @FXML
    private Button returnButton;

    @FXML
    private Rectangle toolbar;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private AnchorPane background;

    @FXML
    void addClass() {
        int spaceBetweenLines = 22, width = 160, sideButtons = 18, startingValue = 5;
        AnchorPane classSpace = new AnchorPane();
        classSpaces.add(classSpace);

        Rectangle nameSpace = new Rectangle(0, 0, width, spaceBetweenLines);
        nameSpace.setFill(Color.TRANSPARENT);
        nameSpace.setStroke(Color.BLACK);

        Rectangle attributeSpace = new Rectangle(0, 0, width, nameSpace.getHeight() + spaceBetweenLines * 2);
        attributeSpace.setFill(Color.TRANSPARENT);
        attributeSpace.setStroke(Color.BLACK);

        Rectangle methodSpace = new Rectangle(0, 0, width, attributeSpace.getHeight() + spaceBetweenLines * 2);
        methodSpace.setFill(Color.TRANSPARENT);
        methodSpace.setStroke(Color.BLACK);

        Rectangle changingParametersAttribute = new Rectangle(attributeSpace.getWidth() - 5, attributeSpace.getHeight() - 5, 5, 5);
        changingParametersAttribute.setFill(Color.TRANSPARENT);
        changingParametersAttribute.setStroke(Color.BLACK);

        Rectangle changingParametersMethod = new Rectangle(methodSpace.getWidth() - 5, methodSpace.getHeight() - 5, 5, 5);
        changingParametersMethod.setFill(Color.TRANSPARENT);
        changingParametersMethod.setStroke(Color.BLACK);

        ImageView deleteClass = new ImageView(String.valueOf(new File("file:trash.png")));
        deleteClass.setX(methodSpace.getWidth() - sideButtons + 1);
        deleteClass.setY(-sideButtons + 1);

        ClassDrawings classDrawing = new ClassDrawings(nameSpace, attributeSpace, methodSpace, classSpace, "", changingParametersAttribute, changingParametersMethod, deleteClass);

        deleteClass.setOnMouseEntered(event1 -> classSpace.setCursor(Cursor.HAND));
        deleteClass.setOnMouseExited(event1 -> classSpace.setCursor(Cursor.DEFAULT));
        deleteClass.setOnMousePressed(event1 -> {
            background.getChildren().remove(classSpace);
            classSpaces.remove(classSpace);
            classDrawings.remove(classDrawing);
            if (selectedOne == classSpace) selectedOne = null;
            if (selectedClassDrawing == classDrawing) selectedClassDrawing = null;
        });

        background.getChildren().add(classSpace);
        classSpace.setLayoutX(20);
        classSpace.setLayoutY(20);

        Rectangle movePart = new Rectangle((width - spaceBetweenLines) / 2, -5, spaceBetweenLines, 10);
        movePart.setOnMouseEntered(event1 -> classSpace.setCursor(Cursor.HAND));
        movePart.setOnMouseExited(event1 -> classSpace.setCursor(Cursor.DEFAULT));
        movePart.setOnMousePressed(event1 -> {
            isPressed = true;
            selectedClassDrawing = classDrawing;
            selectedOne = controller.changeSelected(selectedOne, classSpace);
        });
        movePart.setOnMouseReleased(event1 -> {
            classSpace.setCursor(Cursor.HAND);
            isPressed = false;
        });

        TextField textFieldName = new TextField();
        Text textName = new Text();
        AnchorPane rowName = new AnchorPane();
        ClassRows nameOfClass = new ClassRows(textFieldName, textName, rowName, controller.addRectangle(nameSpace, textFieldName,
                textName, sideButtons, rowName, 0, classDrawing, 22, classSpace, selectedOne, false), 0, false);
        textFieldName.setOnKeyPressed(event1 -> {
            if (event1.getCode() == KeyCode.ENTER) {
                textName.setText(textFieldName.getText());
                nameOfClass.enter(0, classDrawing);
            }
            if (textFieldName.getText().length() > classDrawing.theLongestPhrase) {
                controller.stretchDrawing(nameSpace, attributeSpace, methodSpace, classDrawing, textFieldName);
                classDrawing.theLongestPhrase += 3;
            } else if (textFieldName.getText().length() > startingValue)
                textFieldName.setPrefWidth(70 + (textFieldName.getText().length() / 3) * 10);
        });
        textName.setFont(Font.font("System", FontWeight.BOLD, 12));
        classDrawing.name = nameOfClass;
        AnchorPane nameRow = nameOfClass.row;
        nameRow.setPrefWidth(nameSpace.getWidth());
        nameRow.setLayoutY(2);


        TextField textFieldAttribute = new TextField();
        Text textAttribute = new Text();
        AnchorPane rowAttribute = new AnchorPane();
        ClassRows attributeRow = new ClassRows(textFieldAttribute, textAttribute, rowAttribute, controller.addRectangle(nameSpace, textFieldAttribute,
                textAttribute, sideButtons, rowAttribute, 1, classDrawing, 22, classSpace, selectedOne, false), 1, false);
        textFieldAttribute.setOnKeyPressed(event1 -> {
            if (event1.getCode() == KeyCode.ENTER) {
                attributeRow.enter(1, classDrawing);
            }
            if (textFieldAttribute.getText().length() >= classDrawing.theLongestPhrase) {
                controller.stretchDrawing(nameSpace, attributeSpace, methodSpace, classDrawing, textFieldAttribute);
                classDrawing.theLongestPhrase += 3;
            } else if (textFieldAttribute.getText().length() > startingValue)
                textFieldAttribute.setPrefWidth(70 + (textFieldAttribute.getText().length() / 3) * 10);
        });
        classDrawing.attributeRows.add(attributeRow);
        AnchorPane attributeRowParent = attributeRow.row;
        attributeRowParent.setPrefWidth(nameSpace.getWidth());
        attributeRowParent.setLayoutY(nameSpace.getHeight() + 2);

        changingParametersAttribute.setOnMouseEntered(event1 -> classSpace.setCursor(Cursor.HAND));
        changingParametersAttribute.setOnMouseExited(event1 -> classSpace.setCursor(Cursor.DEFAULT));
        changingParametersAttribute.setOnMousePressed(event1 -> {
            classSpace.setCursor(Cursor.CLOSED_HAND);
            selectedClassDrawing = classDrawing;
            differenceInHeight = selectedClassDrawing.methodSpace.getHeight() - selectedClassDrawing.attributeSpace.getHeight();
            isPressedAttribute = true;
            selectedOne = controller.changeSelected(selectedOne, classSpace);
        });
        changingParametersAttribute.setOnMouseReleased(event1 -> {
            classSpace.setCursor(Cursor.HAND);
            isPressedAttribute = false;
        });

        TextField textFieldMethod = new TextField();
        Text textMethod = new Text();
        AnchorPane rowMethod = new AnchorPane();
        ClassRows methodRow = new ClassRows(textFieldMethod, textMethod, rowMethod, controller.addRectangle(nameSpace, textFieldMethod,
                textMethod, sideButtons, rowMethod, 2, classDrawing, 22, classSpace, selectedOne, false), 2, false);
        textFieldMethod.setOnKeyPressed(event1 -> {
            if (event1.getCode() == KeyCode.ENTER) {
                methodRow.enter(2, classDrawing);
            }
            if (textFieldMethod.getText().length() >= classDrawing.theLongestPhrase) {
                controller.stretchDrawing(nameSpace, attributeSpace, methodSpace, classDrawing, textFieldMethod);
                classDrawing.theLongestPhrase += 3;
            } else if (textFieldMethod.getText().length() > startingValue)
                textFieldMethod.setPrefWidth(70 + (textFieldMethod.getText().length() / 3) * 10);
        });
        classDrawing.methodRows.add(methodRow);
        AnchorPane methodRowParent = methodRow.row;
        methodRowParent.setPrefWidth(nameSpace.getWidth());
        methodRowParent.setLayoutY(attributeSpace.getHeight() + 2);

        changingParametersMethod.setOnMouseEntered(event1 -> classSpace.setCursor(Cursor.HAND));
        changingParametersMethod.setOnMouseExited(event1 -> classSpace.setCursor(Cursor.DEFAULT));
        changingParametersMethod.setOnMousePressed(event1 -> {
            classSpace.setCursor(Cursor.CLOSED_HAND);
            isPressedMethod = true;
            selectedClassDrawing = classDrawing;
            selectedOne = controller.changeSelected(selectedOne, classSpace);
        });
        changingParametersMethod.setOnMouseReleased(event1 -> {
            classSpace.setCursor(Cursor.HAND);
            isPressedMethod = false;
        });

        classSpace.getChildren().addAll(nameSpace, attributeSpace, methodSpace,
                nameOfClass.row, attributeRow.row, methodRow.row, movePart,
                changingParametersAttribute, changingParametersMethod, deleteClass);
        methodSpace.setOnMouseClicked(event1 -> selectedOne = controller.changeSelected(selectedOne, classSpace));

        for (int i = 1; i <= 2; i++) {
            controller.addAddButton(i, sideButtons, attributeSpace, methodSpace, classSpace, classDrawing, nameSpace, selectedOne, false, furthestDown);
        }

        classDrawing.rectangles.add(movePart);
        classDrawing.rectangles.add(changingParametersAttribute);
        classDrawing.rectangles.add(changingParametersMethod);
        classDrawing.images.add(deleteClass);

        if (classDrawings.size() == 0) {
            classSpace.setLayoutX(20);
            classSpace.setLayoutY(20);
        } else {
            AnchorPane temp = classDrawings.get(classDrawings.size() - 1).root;
            if (background.getWidth() - temp.getLayoutX() - nameSpace.getWidth() - 10 > methodSpace.getWidth()) {
                classSpace.setLayoutX(temp.getLayoutX() + nameSpace.getWidth() + 10);
                classSpace.setLayoutY(temp.getLayoutY());
            } else {
                classSpace.setLayoutX(20);
                classSpace.setLayoutY(furthestDown + 20);
            }
        }
        classDrawings.add(classDrawing);

        if (classSpace.getLayoutY() + methodSpace.getHeight() > furthestDown)
            furthestDown = (int) (classSpace.getLayoutY() + methodSpace.getHeight());
        if (furthestDown > background.getHeight()) background.setPrefHeight(furthestDown + 200);
    }

    @FXML
    void download() {
        int indexEffect = 0;
        Effect effect = new DropShadow();
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File("C:\\"));
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("PNG file", ".png");
        FileChooser.ExtensionFilter extensionFilter1 = new FileChooser.ExtensionFilter("JPG file", ".jpg");
        FileChooser.ExtensionFilter extensionFilter2 = new FileChooser.ExtensionFilter("JPEG file", ".jpeg");
        fc.getExtensionFilters().add(extensionFilter);
        fc.getExtensionFilters().add(extensionFilter1);
        fc.getExtensionFilters().add(extensionFilter2);
        fc.setTitle("Download image");
        File location = fc.showSaveDialog(new Stage());
        boolean selected = showRectangles.isSelected();
        controller.toggleRectangles(false, classDrawings);
        if (selectedOne != null) {
            for (int i = 0; i < classSpaces.size(); i++) {
                if (classSpaces.get(i) == selectedOne) {
                    indexEffect = i;
                    effect = classSpaces.get(i).getChildren().get(2).getEffect();
                    classSpaces.get(i).getChildren().get(2).setEffect(null);
                    break;
                }
            }
        }
        try {
            File file = new File(location + "");
            int index = location.toString().lastIndexOf('.');
            String format = location.toString().substring(index + 1);
            WritableImage umlDiagram = background.snapshot(new SnapshotParameters(), null);
            ImageIO.write(SwingFXUtils.fromFXImage(umlDiagram, null), format, file);
        } catch (Exception e) {
            System.out.println(e);
        }
        controller.toggleRectangles(selected, classDrawings);
        if (selectedOne != null) classSpaces.get(indexEffect).getChildren().get(2).setEffect(effect);
    }

    @FXML
    void export(ActionEvent event) {
        DirectoryChooser dir = new DirectoryChooser();
        dir.setInitialDirectory(new File("C:\\"));
        dir.setTitle("export");
        File directory = dir.showDialog(new Stage());
        try {
            for (int i = 0; i < classDrawings.size(); i++) {
                File file = new File(directory + "//" + classDrawings.get(i).className + ".java");
                FileWriter fw = new FileWriter(file);
                fw.write(String.valueOf(createContent(i)));
                fw.flush();
                fw.close();
            }
        } catch (Exception ignored) {
        }
    }

    @FXML
    void goToIntro() throws IOException {
        Main.root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("Intro.fxml")));

        Main.scene.setRoot(Main.root);
    }

    @FXML
    void showPreview() {
        if (classDrawings.size() != 1) {
            AnchorPane root = new AnchorPane();
            Scene scene = new Scene(root, 350, 200);
            Text information = new Text();
            root.getChildren().add(information);
            if (classDrawings.size() > 1) {
                information.setWrappingWidth(290);
                information.setTextAlignment(TextAlignment.CENTER);
                information.setText("There are multiple classes. \n" +
                        "Would you like to preview them all or only the selected one?");
                information.setLayoutX(30);
                information.setLayoutY(50);
                Button selected = new Button("Only \nselected one");
                Button all = new Button("All");
                all.setOnMouseEntered(event -> scene.setCursor(Cursor.HAND));
                selected.setPrefWidth(95);
                selected.setPrefHeight(45);
                all.setTextAlignment(TextAlignment.CENTER);
                all.setPrefWidth(selected.getPrefWidth());
                all.setPrefHeight(selected.getPrefHeight());
                //System.out.println(all.getMinWidth());
                all.setLayoutY(140);
                all.setLayoutX((scene.getWidth() - all.getPrefWidth() * 2) / 3);
                all.setOnAction(event1 -> {
                    modalWindow.close();
                    constructClasses(classDrawings.size(), null);
                });
                selected.setOnMouseEntered(event -> scene.setCursor(Cursor.HAND));
                selected.setTextAlignment(TextAlignment.CENTER);
                selected.setLayoutY(140);
                selected.setLayoutX(all.getLayoutX() * 2 + all.getPrefWidth());
                selected.setOnAction(event1 -> {
                    modalWindow.close();
                    constructClasses(1, selectedOne);
                });
                root.getChildren().addAll(all, selected);
            } else {
                information.setText("There is no class to preview");
                information.setWrappingWidth(scene.getWidth());
                information.setTextAlignment(TextAlignment.CENTER);
                information.setLayoutY(50);
                //information.setLayoutX(20);
                Button cancel = new Button("Cancel");
                cancel.setOnMouseEntered(event -> scene.setCursor(Cursor.HAND));
                cancel.setLayoutX((300 - cancel.getWidth()) / 2);
                cancel.setLayoutY(150);
                cancel.setOnAction(event1 -> modalWindow.close());
                root.getChildren().add(cancel);
            }
            modalWindow = new Stage();
            modalWindow.setScene(scene);
            modalWindow.setResizable(false);
            modalWindow.show();
        } else constructClasses(1, null);
    }

    public void constructClasses(int nr, AnchorPane selectedOne) {
        int index = 0;
        StringBuilder text = new StringBuilder();
        ScrollPane root = new ScrollPane();
        AnchorPane space = new AnchorPane();
        root.setContent(space);
        if (selectedOne != null) {
            for (int i = 0; i < classSpaces.size(); i++) {
                if (classSpaces.get(i) == selectedOne) {
                    index = i;
                    break;
                }
            }
        }
        if (selectedOne == null) {
            for (int i = 0; i < nr; i++) {
                if (i > 0) text.append("\n\n------------------------------------------------------\n\n");
                text.append(createContent(i));
            }
        } else {
            text.append(createContent(index));
        }
        Text content = new Text(text + "");
        content.setLayoutY(10);
        content.setLayoutX(5);
        modalWindow = new Stage();
        Scene scene = new Scene(root, 600, 400);
        space.getChildren().add(content);
        modalWindow.setScene(scene);
        modalWindow.show();
    }

    public StringBuilder createContent(int i) {
        StringBuilder text = new StringBuilder();
        List<String> importsToBeWritten = new ArrayList<>();
        text.append("package sample/com.company\n\n");

        StringBuilder attributes = new StringBuilder();
        StringBuilder methods = new StringBuilder();

        List<Text> texts = new ArrayList<>();

        for (int j = 0; j < classDrawings.get(i).attributeRows.size(); j++) {
            texts.add(classDrawings.get(i).attributeRows.get(j).text);
        }

        for (Text item : texts) {
            String attribute = item.getText();
            if (!attribute.isEmpty()) {
                String textToAdd = "";
                attribute = attribute.trim();
                String accessModifier = String.valueOf(attribute.charAt(0));
                if (accessModifier.equals("+")) accessModifier = "\\+";
                attribute = attribute.replaceFirst(accessModifier, "");
                switch (accessModifier) {
                    case "\\+":
                        textToAdd += "public";
                        break;
                    case "-":
                        textToAdd += "private";
                        break;
                    case "/":
                        textToAdd += "protected";
                        break;
                }
                attribute = attribute.trim();
                if (attribute.contains(":")) {
                    String[] parts = attribute.split(":");
                    parts[1] = parts[1].trim();
                    if (imports.containsKey(parts[1]) && checkIfImportOccurs(imports.get(parts[1]), importsToBeWritten))
                        importsToBeWritten.add(imports.get(parts[1]));
                    textToAdd += parts[1] + " " + parts[0] + ";";
                } else {
                    String[] parts = attribute.split(" ");
                    if (imports.containsKey(parts[0]) && checkIfImportOccurs(imports.get(parts[0]), importsToBeWritten))
                        importsToBeWritten.add(imports.get(parts[0]));
                    textToAdd += " " + attribute + ";";
                }
                attributes.append("    ").append(textToAdd).append("\n");
            }
        }

        texts.clear();

        for (int j = 0; j < classDrawings.get(i).methodRows.size(); j++) {
            texts.add(classDrawings.get(i).methodRows.get(j).text);
        }

        for (Text value : texts) {
            String method = value.getText();
            if (!method.isEmpty()) {
                String textToAdd = "";
                method = method.trim();
                String accessModifier = String.valueOf(method.charAt(0));
                if (accessModifier.equals("+")) accessModifier = "\\+";
                method = method.replaceFirst(accessModifier, "");
                switch (accessModifier) {
                    case "\\+":
                        textToAdd += "public";
                        break;
                    case "-":
                        textToAdd += "private";
                        break;
                    case "/":
                        textToAdd += "protected";
                        break;
                }
                if (method.contains(":")) {
                    String[] parts = method.split(":");
                    parts[1] = parts[1].trim();
                    textToAdd += parts[1] + " " + parts[0];
                } else {
                    textToAdd += method;
                }
                methods.append("\n    ").append(textToAdd).append("{\n\n    }\n");
            }
        }
        for (String s : importsToBeWritten) {
            text.append(s).append("\n");
        }
        text.append("\npublic class ").append(classDrawings.get(i).className).append(" {\n\n").append(attributes).append(methods).append("}");
        return text;
    }

    public boolean checkIfImportOccurs(String import1, List<String> importsToBeWritten) {
        for (String s : importsToBeWritten) {
            if (s.equals(import1)) return false;
        }
        return true;
    }

    Timeline synchronize = new Timeline(new KeyFrame(Duration.millis(10), event -> {
        double sceneWidth = Main.scene.getWidth();
        double gapBetweenButtons = (Main.scene.getWidth() * 0.66667 - 320) / 6;
        double y = Main.scene.getHeight() / 4;
        double buttonHeight = (y - 26) / 2;
        toolbar.setWidth(sceneWidth);
        toolbar.setHeight(y);
        scrollPane.setPrefSize(sceneWidth, Main.scene.getHeight() * 0.75);
        scrollPane.setLayoutY(y);
        returnButton.setLayoutY((y - 50) / 2);
        showRectangles.setLayoutX(toolbar.getWidth() / 7 + gapBetweenButtons);
        showRectangles.setLayoutY(buttonHeight+3);
        addClassButton.setLayoutX(showRectangles.getLayoutX() + 134 + gapBetweenButtons);
        addClassButton.setLayoutY(buttonHeight);
        previewButton.setLayoutX(addClassButton.getLayoutX() + addClassButton.getWidth() + gapBetweenButtons);
        previewButton.setLayoutY(buttonHeight);
        downloadButton.setLayoutX(previewButton.getLayoutX() + previewButton.getWidth() + gapBetweenButtons);
        downloadButton.setLayoutY(buttonHeight);
        exportButton.setLayoutX(downloadButton.getLayoutX() + downloadButton.getWidth() + gapBetweenButtons);
        exportButton.setLayoutY(buttonHeight);
    }));

    public void initialize() {
        background.setPrefSize(Main.scene.getWidth() - 20, Main.scene.getHeight() * 0.75 - 20);
        synchronize.setCycleCount(-1);
        synchronize.play();
        Scanner scan = null;
        try {
            scan = new Scanner(new File("imports.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (Objects.requireNonNull(scan).hasNext()) {
            String line = scan.nextLine();
            String[] importsParts = line.split(";");
            imports.put(importsParts[0], importsParts[1]);
        }
        scan.close();
        showRectangles.setOnAction(event -> controller.toggleRectangles(showRectangles.isSelected(), classDrawings));
        background.setOnMouseDragged(event -> {
            if (isPressed) {
                background.setCursor(Cursor.CLOSED_HAND);
                selectedOne.setLayoutX(event.getX() - selectedOne.getWidth() / 2);
                selectedOne.setLayoutY(event.getY());
            } else if (isPressedAttribute || isPressedMethod) {
                if(selectedClassDrawing.name.textField.getWidth()+50>event.getX()-selectedClassDrawing.root.getLayoutX())
                    selectedClassDrawing.nameSpace.setWidth(selectedClassDrawing.name.textField.getWidth()+50);
                else {
                    if (selectedClassDrawing.theLongestPhrase * 7 + 60 > event.getX() - selectedClassDrawing.root.getLayoutX())
                        selectedClassDrawing.nameSpace.setWidth(selectedClassDrawing.theLongestPhrase * 7 + 60);
                    else selectedClassDrawing.nameSpace.setWidth(event.getX() - selectedClassDrawing.root.getLayoutX());
                }
                //selectedClassDrawing.nameSpace.setWidth(event.getX() - selectedClassDrawing.root.getLayoutX());
                selectedClassDrawing.attributeSpace.setWidth(selectedClassDrawing.nameSpace.getWidth());
                selectedClassDrawing.methodSpace.setWidth(selectedClassDrawing.nameSpace.getWidth());
                selectedClassDrawing.name.text.setLayoutX((selectedClassDrawing.nameSpace.getWidth() - selectedClassDrawing.name.text.getText().length() * 7) / 2);
                selectedClassDrawing.changingParametersAttribute.setX(selectedClassDrawing.methodSpace.getWidth() - 5);
                selectedClassDrawing.changingParametersMethod.setX(selectedClassDrawing.methodSpace.getWidth() - 5);
                selectedClassDrawing.deleteClass.setX(selectedClassDrawing.methodSpace.getWidth() - 17);
                selectedClassDrawing.name.save.setX(selectedClassDrawing.methodSpace.getWidth() - 20);
                selectedClassDrawing.name.edit.setX(selectedClassDrawing.methodSpace.getWidth() - 20);
                for (int i = 0; i < selectedClassDrawing.attributeRows.size(); i++) {
                    selectedClassDrawing.attributeRows.get(i).save.setX(selectedClassDrawing.methodSpace.getWidth() - 20);
                    selectedClassDrawing.attributeRows.get(i).delete.setX(selectedClassDrawing.methodSpace.getWidth() - 20);
                    selectedClassDrawing.attributeRows.get(i).edit.setX(selectedClassDrawing.methodSpace.getWidth() - 40);
                }
                for (int i = 0; i < selectedClassDrawing.methodRows.size(); i++) {
                    selectedClassDrawing.methodRows.get(i).save.setX(selectedClassDrawing.methodSpace.getWidth() - 20);
                    selectedClassDrawing.methodRows.get(i).delete.setX(selectedClassDrawing.methodSpace.getWidth() - 20);
                    selectedClassDrawing.methodRows.get(i).edit.setX(selectedClassDrawing.methodSpace.getWidth() - 40);
                }
                selectedClassDrawing.images.get(0).setX((selectedClassDrawing.methodSpace.getWidth() - 22) / 2);
                selectedClassDrawing.images.get(1).setX((selectedClassDrawing.methodSpace.getWidth() - 22) / 2);
                selectedClassDrawing.rectangles.get(0).setX((selectedClassDrawing.methodSpace.getWidth() - 22) / 2);
                selectedClassDrawing.changingParametersMethod.setY(selectedClassDrawing.methodSpace.getHeight() - 5);
                if (isPressedAttribute) {
                    selectedClassDrawing.attributeSpace.setHeight(Math.max(event.getY() - selectedClassDrawing.root.getLayoutY(), selectedClassDrawing.nameSpace.getHeight() + selectedClassDrawing.attributeRows.size() * 15 + 30));
                    selectedClassDrawing.methodSpace.setHeight(selectedClassDrawing.attributeSpace.getHeight() + differenceInHeight);
                    selectedClassDrawing.changingParametersAttribute.setY(selectedClassDrawing.attributeSpace.getHeight() - 5);
                    for (int i = 0; i < selectedClassDrawing.methodRows.size(); i++) {
                        selectedClassDrawing.methodRows.get(i).row.setLayoutY(selectedClassDrawing.attributeSpace.getHeight() + i * (selectedClassDrawing.methodRows.get(i).row.getHeight() + 2) + 2);
                    }
                    selectedClassDrawing.images.get(0).setY(selectedClassDrawing.attributeSpace.getHeight() - 20);
                    selectedClassDrawing.images.get(1).setY(selectedClassDrawing.methodSpace.getHeight() - 20);
                } else if (isPressedMethod) {
                    selectedClassDrawing.methodSpace.setHeight(Math.max(event.getY() - selectedClassDrawing.root.getLayoutY(), selectedClassDrawing.attributeSpace.getHeight() + selectedClassDrawing.methodRows.size() * 15 + 30));
                    selectedClassDrawing.images.get(1).setY(selectedClassDrawing.methodSpace.getHeight() - 20);
                }
            }
        });
    }
}


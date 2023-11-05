package com.example;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class ImportLayoutController {

    List<ClassDrawings> classDrawings = new ArrayList<>();
    List<AnchorPane> classSpaces = new ArrayList<>();
    double furthestDown = 0, differenceInHeight;
    boolean isPressed=false, isPressedAttribute=false, isPressedMethod=false, imported=false;
    RectangleClass rec=null;
    ClassDrawings selectedClassDrawing = null;
    AnchorPane selectedOne = null;
    Controller controller = new Controller();

    @FXML
    private Button addClassButton;

    @FXML
    private AnchorPane background;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private CheckBox showRectanglesCheckbox;

    @FXML
    private Canvas canvas;

    GraphicsContext gr;

    @FXML
    private Button downloadButton;

    @FXML
    private Button returnButton;

    @FXML
    private Text text;

    @FXML
    private Text text2;

    @FXML
    private Rectangle toolbar;

    @FXML
    void browse() throws IOException {
        FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(new Stage());
        if(file!=null) enterDirectory(file);
    }

    @FXML
    void addClass() {
        int spaceBetweenLines = 22, width = 130, sideButtons = 18,
                theLongestPhraseImported = 0, startingValue=5;
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

        Rectangle changingParametersAttribute = new Rectangle(5, 5);
        changingParametersAttribute.setFill(Color.TRANSPARENT);
        changingParametersAttribute.setStroke(Color.BLACK);

        Rectangle changingParametersMethod = new Rectangle(5, 5);
        changingParametersMethod.setFill(Color.TRANSPARENT);
        changingParametersMethod.setStroke(Color.BLACK);

        ImageView deleteClass = new ImageView(String.valueOf(new File("file:trash.png")));
        deleteClass.setX(methodSpace.getWidth()-sideButtons+1);
        deleteClass.setY(-sideButtons+1);

        ClassDrawings classDrawing = new ClassDrawings(nameSpace, attributeSpace, methodSpace,
                classSpace, "", changingParametersAttribute, changingParametersMethod, deleteClass);

        deleteClass.setOnMouseEntered(event1 -> classSpace.setCursor(Cursor.HAND));
        deleteClass.setOnMouseExited(event1 -> classSpace.setCursor(Cursor.DEFAULT));
        deleteClass.setOnMousePressed(event1 -> {
            background.getChildren().remove(classSpace);
            classSpaces.remove(classSpace);
            classDrawings.remove(classDrawing);
            if(selectedOne==classSpace)selectedOne=null;
            if(selectedClassDrawing==classDrawing)selectedClassDrawing=null;
        });

        background.getChildren().add(classSpace);
        classSpace.setLayoutX(20);
        classSpace.setLayoutY(20);

        Rectangle movePart = new Rectangle((width-spaceBetweenLines)/2, -5, spaceBetweenLines, 10);
        movePart.setOnMouseEntered(event1 -> classSpace.setCursor(Cursor.HAND));
        movePart.setOnMouseExited(event1 -> classSpace.setCursor(Cursor.DEFAULT));
        movePart.setOnMousePressed(event1 -> {
            selectedOne = classSpace;
            isPressed = true;
        });
        movePart.setOnMouseReleased(event1 -> {
            classSpace.setCursor(Cursor.HAND);
            isPressed=false;
        });

        changingParametersAttribute.setOnMouseEntered(event1 -> classSpace.setCursor(Cursor.HAND));
        changingParametersAttribute.setOnMouseExited(event1 -> classSpace.setCursor(Cursor.DEFAULT));
        changingParametersAttribute.setOnMousePressed(event1 -> {
            classSpace.setCursor(Cursor.CLOSED_HAND);
            selectedClassDrawing = classDrawing;
            differenceInHeight = selectedClassDrawing.methodSpace.getHeight()-selectedClassDrawing.attributeSpace.getHeight();
            isPressedAttribute=true;
        });
        changingParametersAttribute.setOnMouseReleased(event1 -> {
            classSpace.setCursor(Cursor.HAND);
            isPressedAttribute=false;
        });

        changingParametersMethod.setOnMouseEntered(event1 -> classSpace.setCursor(Cursor.HAND));
        changingParametersMethod.setOnMouseExited(event1 -> classSpace.setCursor(Cursor.DEFAULT));
        changingParametersMethod.setOnMousePressed(event1 -> {
            classSpace.setCursor(Cursor.CLOSED_HAND);
            isPressedMethod=true;
            selectedClassDrawing = classDrawing;
        });
        changingParametersMethod.setOnMouseReleased(event1 -> {
            classSpace.setCursor(Cursor.HAND);
            isPressedMethod=false;
        });

        classSpace.getChildren().addAll(nameSpace, attributeSpace, methodSpace, movePart, changingParametersAttribute, changingParametersMethod, deleteClass);

        if(!imported) {
            TextField textFieldName = new TextField();
            Text textName = new Text();
            textName.setFont(Font.font("System", FontWeight.BOLD, 12));
            AnchorPane rowName = new AnchorPane();
            ClassRows nameOfClass = new ClassRows(textFieldName, textName, rowName, controller.addRectangle(nameSpace, textFieldName,
                    textName, sideButtons, rowName, 0, classDrawing, 22, classSpace, selectedOne, true), 0, false);
            textFieldName.setOnKeyPressed(event1 -> {
                if(event1.getCode()== KeyCode.ENTER){
                    textName.setText(textFieldName.getText());
                    nameOfClass.enter(0, classDrawing);
                }
                if(textFieldName.getText().length()>classDrawing.theLongestPhrase){
                    controller.stretchDrawing(nameSpace, attributeSpace, methodSpace, classDrawing, textFieldName);
                    classDrawing.theLongestPhrase+=3;
                }
                else if(textFieldName.getText().length()>startingValue)textFieldName.setPrefWidth(70+(textFieldName.getText().length()/3)*10);
            });
            classDrawing.name = nameOfClass;
            AnchorPane nameRow = nameOfClass.row;
            nameRow.setLayoutY(2);
            nameRow.setPrefWidth(nameSpace.getWidth());

            TextField textFieldAttribute = new TextField();
            Text textAttribute = new Text();
            AnchorPane rowAttribute = new AnchorPane();
            ClassRows attributeRow = new ClassRows(textFieldAttribute, textAttribute, rowAttribute, controller.addRectangle(nameSpace, textFieldAttribute,
                    textAttribute, sideButtons, rowAttribute, 1, classDrawing, 22, classSpace, selectedOne, true), 1, false);
            textFieldAttribute.setOnKeyPressed(event1 -> {
                if(event1.getCode()== KeyCode.ENTER){
                    attributeRow.enter(1, classDrawing);
                }
                if(textFieldAttribute.getText().length()>=classDrawing.theLongestPhrase){
                    controller.stretchDrawing(nameSpace, attributeSpace, methodSpace, classDrawing, textFieldAttribute);
                    classDrawing.theLongestPhrase+=3;
                }
                else if(textFieldAttribute.getText().length()>startingValue)textFieldAttribute.setPrefWidth(70+(textFieldAttribute.getText().length()/3)*10);
            });
            classDrawing.attributeRows.add(attributeRow);
            AnchorPane attributeRowParent = attributeRow.row;
            attributeRowParent.setPrefWidth(nameSpace.getWidth());
            attributeRowParent.setLayoutY(nameSpace.getHeight() + 2);

            TextField textFieldMethod = new TextField();
            Text textMethod = new Text();
            AnchorPane rowMethod = new AnchorPane();
            ClassRows methodRow = new ClassRows(textFieldMethod, textMethod, rowMethod, controller.addRectangle(nameSpace, textFieldMethod,
                    textMethod, sideButtons, rowMethod, 2, classDrawing, 22, classSpace, selectedOne, true), 2, false);
            textFieldMethod.setOnKeyPressed(event1 -> {
                if(event1.getCode()== KeyCode.ENTER){
                    methodRow.enter(2, classDrawing);
                }
                if(textFieldMethod.getText().length()>=classDrawing.theLongestPhrase){
                    controller.stretchDrawing(nameSpace, attributeSpace, methodSpace, classDrawing, textFieldMethod);
                    classDrawing.theLongestPhrase+=3;
                }
                else if(textFieldMethod.getText().length()>startingValue)textFieldMethod.setPrefWidth(70+(textFieldMethod.getText().length()/3)*10);
            });
            classDrawing.methodRows.add(methodRow);
            AnchorPane methodRowParent = methodRow.row;
            methodRowParent.setPrefWidth(nameSpace.getWidth());
            methodRowParent.setLayoutY(attributeSpace.getHeight() + 2);

            classSpace.getChildren().addAll(nameOfClass.row, attributeRow.row, methodRow.row);
        }
        else{
            if (rec.getName().length() > theLongestPhraseImported) theLongestPhraseImported = rec.getName().length();
            TextField textFieldName = new TextField(rec.getName());
            textFieldName.setPrefWidth(70+(rec.getName().length()/3)*10);
            textFieldName.setMinWidth(70+(rec.getName().length()/3)*10);
            textFieldName.setMaxWidth(70+(rec.getName().length()/3)*10);
            Text textName = new Text(rec.getName());
            AnchorPane rowName = new AnchorPane();
            ClassRows nameOfClass = new ClassRows(textFieldName, textName, rowName, controller.addRectangle(nameSpace, textFieldName,
                    textName, sideButtons, rowName, 0, classDrawing, 22, classSpace, selectedOne, true), 0, true);
            textFieldName.setOnKeyPressed(event1 -> {
                if(event1.getCode()== KeyCode.ENTER){
                    nameOfClass.enter(0, classDrawing);
                }
                if(textFieldName.getText().length()>classDrawing.theLongestPhrase){
                    controller.stretchDrawing(nameSpace, attributeSpace, methodSpace, classDrawing, textFieldName);
                    classDrawing.theLongestPhrase+=3;
                }
                else if(textFieldName.getText().length()>startingValue)textFieldName.setPrefWidth(70+(textFieldName.getText().length()/3)*10);
            });
            textName.setFont(Font.font("System", FontWeight.BOLD, 12));
            classDrawing.name = nameOfClass;
            AnchorPane nameRow = nameOfClass.row;
            nameRow.setLayoutY(2);
            nameRow.setPrefWidth(nameSpace.getWidth());
            classSpace.getChildren().add(nameRow);

            attributeSpace.setHeight(nameSpace.getHeight()+rec.attributes.size()*22+30);
            methodSpace.setHeight(attributeSpace.getHeight()+rec.methods.size()*22+30);

            for (int i = 0; i < rec.getAttributes().size(); i++) {
                String attribute = rec.getAttributes().get(i);
                Text attributeText = new Text();
                if (attribute.contains(";")) {
                    attribute = attribute.replace(";static", "");
                    attributeText.setUnderline(true);
                }
                if (attribute.length() > theLongestPhraseImported) theLongestPhraseImported = attribute.length();
                attributeText.setText(attribute);
                TextField textFieldAttribute = new TextField(attribute);
                textFieldAttribute.setPrefWidth(70+(attribute.length()/3)*10);
                textFieldAttribute.setMinWidth(70+(attribute.length()/3)*10);
                textFieldAttribute.setMaxWidth(70+(attribute.length()/3)*10);
                AnchorPane rowAttribute = new AnchorPane();
                ClassRows attributeRow = new ClassRows(textFieldAttribute, attributeText, rowAttribute,
                        controller.addRectangle(nameSpace, textFieldAttribute, attributeText, sideButtons, rowAttribute, 1, classDrawing,
                                22, classSpace, selectedOne, true), 1, true);
                textFieldAttribute.setOnKeyPressed(event1 -> {
                    if(event1.getCode()== KeyCode.ENTER){
                        attributeRow.enter(1, classDrawing);
                    }
                    if(textFieldAttribute.getText().length()>=classDrawing.theLongestPhrase){
                        controller.stretchDrawing(nameSpace, attributeSpace, methodSpace, classDrawing, textFieldAttribute);
                        classDrawing.theLongestPhrase+=3;
                    }
                    else if(textFieldAttribute.getText().length()>startingValue)textFieldAttribute.setPrefWidth(70+(textFieldAttribute.getText().length()/3)*10);
                });
                classDrawing.attributeRows.add(attributeRow);
                rowAttribute.setPrefWidth(nameSpace.getWidth());
                rowAttribute.setLayoutY(nameSpace.getHeight()+i*22+2);
                classSpace.getChildren().add(rowAttribute);
            }
            for (int i = 0; i < rec.getMethods().size(); i++) {
                String method = rec.getMethods().get(i);
                Text methodText = new Text();
                if (method.contains(";")) {
                    method = method.replace(";static", "");
                    methodText.setUnderline(true);
                }
                if (method.length() > theLongestPhraseImported) theLongestPhraseImported = method.length();
                TextField textFieldMethod = new TextField(method);
                textFieldMethod.setPrefWidth(70+(method.length()/3)*10);
                textFieldMethod.setMinWidth(70+(method.length()/3)*10);
                textFieldMethod.setMaxHeight(70+(method.length()/3)*10);
                AnchorPane rowMethod = new AnchorPane();
                ClassRows methodRow = new ClassRows(textFieldMethod, methodText, rowMethod,
                        controller.addRectangle(nameSpace, textFieldMethod, methodText, sideButtons, rowMethod, 1, classDrawing,
                                22, classSpace, selectedOne, true), 1, true);
                textFieldMethod.setOnKeyPressed(event1 -> {
                    if(event1.getCode()== KeyCode.ENTER){
                        methodRow.enter(2, classDrawing);
                    }
                    if(textFieldMethod.getText().length()>=classDrawing.theLongestPhrase){
                        controller.stretchDrawing(nameSpace, attributeSpace, methodSpace, classDrawing, textFieldMethod);
                        classDrawing.theLongestPhrase+=3;
                    }
                    else if(textFieldMethod.getText().length()>startingValue)textFieldMethod.setPrefWidth(70+(textFieldMethod.getText().length()/3)*10);
                });
                methodText.setText(method);
                classDrawing.methodRows.add(methodRow);
                rowMethod.setPrefWidth(nameSpace.getWidth());
                rowMethod.setLayoutY(attributeSpace.getHeight()+i*20+2);
                classSpace.getChildren().add(rowMethod);
            }
        }
        if(theLongestPhraseImported*6+40> nameSpace.getWidth()){
            nameSpace.setWidth(theLongestPhraseImported*6+40);
            attributeSpace.setWidth(theLongestPhraseImported*6+40);
            methodSpace.setWidth(theLongestPhraseImported*6+40);
            classDrawing.name.save.setX(nameSpace.getWidth()-20);
            classDrawing.name.edit.setX(nameSpace.getWidth()-20);
            deleteClass.setX(nameSpace.getWidth()-sideButtons+1);
            movePart.setX((nameSpace.getWidth()-spaceBetweenLines)/2);
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
        classDrawing.name.text.setLayoutX((nameSpace.getWidth()-classDrawing.name.text.getText().length()*7)/2);
        classDrawing.theLongestPhrase=theLongestPhraseImported;
        imported=false;
        rec = null;

        changingParametersAttribute.setY(attributeSpace.getHeight()-5);
        changingParametersAttribute.setX(attributeSpace.getWidth()-5);
        changingParametersMethod.setY(methodSpace.getHeight()-5);
        changingParametersMethod.setX(methodSpace.getWidth()-5);

        for (int i = 1; i <= 2; i++) {
            controller.addAddButton(i, sideButtons, attributeSpace, methodSpace, classSpace, classDrawing, nameSpace, selectedOne, true, furthestDown);
        }

        classDrawing.rectangles.add(movePart);
        classDrawing.images.add(deleteClass);
        classDrawing.rectangles.add(changingParametersAttribute);
        classDrawing.rectangles.add(changingParametersMethod);

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
        controller.toggleRectangles(false, classDrawings);
        try {
            File file = new File(location+"");
            int index = location.toString().lastIndexOf('.');
            String format = location.toString().substring(index+1);
            WritableImage umlDiagram = background.snapshot(new SnapshotParameters(), null);
            ImageIO.write(SwingFXUtils.fromFXImage(umlDiagram, null), format, file);
        }catch(Exception e){
            System.out.println(e);
        }
        controller.toggleRectangles(showRectanglesCheckbox.isSelected(), classDrawings);
    }

    @FXML
    void dragBoard(DragEvent event) throws FileNotFoundException {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            String location = db.getFiles().toString().substring(1, db.getFiles().toString().length() - 1);
            enterDirectory(new File(location));
        }
    }

    public void enterDirectory(File file) throws FileNotFoundException {
        if (file.isDirectory()) {
            File[] content = file.listFiles();
            assert content != null;
            for (File value : content) {
                enterDirectory(value);
            }
        } else if (file.toString().contains(".java")) {
            createClass(file);
        }
    }

    @FXML
    void transfer(DragEvent event) {
        if (event.getGestureSource() != canvas && event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    @FXML
    void goToIntro() throws IOException {
        Main.root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("Intro.fxml")));

        Main.scene.setRoot(Main.root);
    }

    Timeline synchronize = new Timeline(new KeyFrame(Duration.millis(10), event -> {
        double y = Main.scene.getHeight() / 4;
        double buttonHeight = (y - 50) / 2;
        double gapBetweenButtons = (Main.scene.getWidth() * 0.66667 - 300) / 4;

        toolbar.setWidth(Main.scene.getWidth() + 5);
        toolbar.setHeight(y);
        scrollPane.setPrefSize(Main.scene.getWidth(), Main.scene.getHeight() * 0.75);
        scrollPane.setLayoutY(y);
        canvas.setWidth(Main.scene.getWidth() / 3);
        canvas.setHeight(y);
        gr.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        text.setLayoutX((canvas.getWidth() - 152) / 2);
        text.setLayoutY((canvas.getHeight() - 50) / 2);
        text2.setLayoutX((canvas.getWidth() - 125) / 2);
        text2.setLayoutY((canvas.getHeight() - 25) / 2 + 45);
        addClassButton.setLayoutX(Main.scene.getWidth() / 3 + gapBetweenButtons);
        addClassButton.setLayoutY(buttonHeight);
        downloadButton.setLayoutX(addClassButton.getLayoutX() + 100 + gapBetweenButtons);
        downloadButton.setLayoutY(buttonHeight);
        returnButton.setLayoutX(downloadButton.getLayoutX() + 100 + gapBetweenButtons);
        returnButton.setLayoutY(buttonHeight);
        showRectanglesCheckbox.setLayoutX(addClassButton.getLayoutX()-10);
        showRectanglesCheckbox.setLayoutY(addClassButton.getLayoutY()+addClassButton.getHeight()+10);
    }));

    public void initialize() {
        text2.setWrappingWidth(200);
        background.setPrefSize(Main.scene.getWidth() - 20, Main.scene.getHeight() * 0.75 - 20);
        gr = canvas.getGraphicsContext2D();
        gr.setFill(Color.LIGHTBLUE);
        synchronize.setCycleCount(-1);
        synchronize.play();
        showRectanglesCheckbox.setOnAction(event -> controller.toggleRectangles(showRectanglesCheckbox.isSelected(), classDrawings));
        background.setOnMouseDragged(event -> {
            if(isPressed){
                background.setCursor(Cursor.CLOSED_HAND);
                selectedOne.setLayoutX(event.getX()-selectedOne.getWidth()/2);
                selectedOne.setLayoutY(event.getY());
            }
            else if(isPressedAttribute || isPressedMethod)
            {
                if(selectedClassDrawing.name.textField.getWidth()+50>event.getX()-selectedClassDrawing.root.getLayoutX())
                    selectedClassDrawing.nameSpace.setWidth(selectedClassDrawing.name.textField.getWidth()+50);
                else {
                    if (selectedClassDrawing.theLongestPhrase * 7 + 60 > event.getX() - selectedClassDrawing.root.getLayoutX())
                        selectedClassDrawing.nameSpace.setWidth(selectedClassDrawing.theLongestPhrase * 7 + 60);
                    else selectedClassDrawing.nameSpace.setWidth(event.getX() - selectedClassDrawing.root.getLayoutX());
                }
                selectedClassDrawing.attributeSpace.setWidth(selectedClassDrawing.nameSpace.getWidth());
                selectedClassDrawing.name.text.setLayoutX((selectedClassDrawing.nameSpace.getWidth()-selectedClassDrawing.name.text.getText().length()*7)/2);
                selectedClassDrawing.methodSpace.setWidth(selectedClassDrawing.nameSpace.getWidth());
                selectedClassDrawing.changingParametersAttribute.setX(selectedClassDrawing.methodSpace.getWidth()-5);
                selectedClassDrawing.changingParametersMethod.setX(selectedClassDrawing.methodSpace.getWidth()-5);
                selectedClassDrawing.deleteClass.setX(selectedClassDrawing.methodSpace.getWidth()-17);
                selectedClassDrawing.name.save.setX(selectedClassDrawing.methodSpace.getWidth()-20);
                selectedClassDrawing.name.edit.setX(selectedClassDrawing.methodSpace.getWidth()-20);
                for (int i = 0; i <selectedClassDrawing.attributeRows.size(); i++) {
                    selectedClassDrawing.attributeRows.get(i).save.setX(selectedClassDrawing.methodSpace.getWidth()-20);
                    selectedClassDrawing.attributeRows.get(i).delete.setX(selectedClassDrawing.methodSpace.getWidth()-20);
                    selectedClassDrawing.attributeRows.get(i).edit.setX(selectedClassDrawing.methodSpace.getWidth()-40);
                }
                for (int i = 0; i <selectedClassDrawing.methodRows.size(); i++) {
                    selectedClassDrawing.methodRows.get(i).save.setX(selectedClassDrawing.methodSpace.getWidth()-20);
                    selectedClassDrawing.methodRows.get(i).delete.setX(selectedClassDrawing.methodSpace.getWidth()-20);
                    selectedClassDrawing.methodRows.get(i).edit.setX(selectedClassDrawing.methodSpace.getWidth()-40);
                }
                selectedClassDrawing.images.get(0).setX((selectedClassDrawing.methodSpace.getWidth()-22)/2);
                selectedClassDrawing.images.get(1).setX((selectedClassDrawing.methodSpace.getWidth()-22)/2);
                selectedClassDrawing.rectangles.get(0).setX((selectedClassDrawing.methodSpace.getWidth()-22)/2);

                selectedClassDrawing.changingParametersMethod.setY(selectedClassDrawing.methodSpace.getHeight()-5);
                if(isPressedAttribute){
                    selectedClassDrawing.attributeSpace.setHeight(Math.max(event.getY() - selectedClassDrawing.root.getLayoutY(), selectedClassDrawing.nameSpace.getHeight() + selectedClassDrawing.attributeRows.size() * 15 + 30));
                    selectedClassDrawing.methodSpace.setHeight(selectedClassDrawing.attributeSpace.getHeight()+differenceInHeight);
                    selectedClassDrawing.changingParametersAttribute.setY(selectedClassDrawing.attributeSpace.getHeight()-5);
                    for (int i = 0; i <selectedClassDrawing.methodRows.size(); i++) {
                        selectedClassDrawing.methodRows.get(i).row.setLayoutY(selectedClassDrawing.attributeSpace.getHeight()+i*(selectedClassDrawing.methodRows.get(i).row.getHeight()+2)+2);
                    }
                    selectedClassDrawing.images.get(0).setY(selectedClassDrawing.attributeSpace.getHeight()-20);
                    selectedClassDrawing.images.get(1).setY(selectedClassDrawing.methodSpace.getHeight()-20);
                }
                else if(isPressedMethod){
                    selectedClassDrawing.methodSpace.setHeight(Math.max(event.getY() - selectedClassDrawing.root.getLayoutY(), selectedClassDrawing.attributeSpace.getHeight() + selectedClassDrawing.methodRows.size() * 15 + 30));
                    selectedClassDrawing.images.get(1).setY(selectedClassDrawing.methodSpace.getHeight()-20);
                }
            }
        });
    }

    public void createClass(File file) throws FileNotFoundException {
        Scanner scan = new Scanner(file);
        int depth = 0;
        String name = "";
        List<String> attributes = new ArrayList<>();
        List<String> methods = new ArrayList<>();
        while (scan.hasNext()) {
            String line = scan.nextLine();
            if (depth == 0 && line.contains("class") && !line.contains(";")) {
                String[] parts = line.split(" ");
                name = parts[parts.length - 2];
                depth++;
            } else if (depth == 1 && line.contains(";")) {
                String text = line;
                StringBuilder newText;
                if (line.contains("=")) {
                    String[] parts = line.split("=");
                    text = parts[0];
                }
                String[] fragments = text.split(" ");
                if (fragments[fragments.length - 1].contains(";"))
                    newText = new StringBuilder(fragments[fragments.length - 1].replace(";", ""));
                else newText = new StringBuilder(fragments[fragments.length - 1]);
                List<String> fragmentsList = new ArrayList<>();
                for (int i = 0; i < fragments.length - 1; i++) {
                    if (fragments[i].equals("static")) newText.append(";static");
                    else if (!fragments[i].equals("")) fragmentsList.add(fragments[i]);
                }
                String accessModifier = "";
                if (fragmentsList.size() == 1) newText.insert(0, "- " + fragmentsList.get(0) + " ");
                else {
                    for (int i = 0; i < fragmentsList.size(); i++) {
                        String temp = fragmentsList.get(i);
                        if (temp.equals("private") || temp.equals("public") || temp.equals("protected")) {
                            switch (temp) {
                                case "private":
                                    accessModifier = "-";
                                    break;
                                case "public":
                                    accessModifier = "+";
                                    break;
                                case "protected":
                                    accessModifier = "#";
                                    break;
                            }
                            fragmentsList.remove(i);
                            break;
                        }
                    }
                    newText.insert(0, accessModifier + " " + fragmentsList.get(0) + " ");
                }
                attributes.add(newText.toString());
            } else if (depth == 1 && line.contains("{") && line.contains("Timeline")) {
                depth++;
            } else if (depth == 1 && line.contains("{")) {
                StringBuilder newText;
                boolean isStatic = false;
                String[] parts = line.split("\\)");
                String[] nameAndParameters = parts[0].split("\\(");
                String[] names = nameAndParameters[0].split(" ");
                newText = new StringBuilder(names[names.length - 1] + "(");
                List<String> namesList = new ArrayList<>();
                for (int i = 0; i < names.length - 1; i++) {
                    if (names[i].equals("static")) isStatic = true;
                    else if (!names[i].equals("")) namesList.add(names[i]);
                }
                String accessModifier = "";

                if (namesList.size() == 1) newText.insert(0, "- " + namesList.get(0) + " ");
                else {
                    for (int i = 0; i < namesList.size(); i++) {
                        String temp = namesList.get(i);
                        if (temp.equals("private") || temp.equals("public") || temp.equals("protected")) {
                            switch (temp) {
                                case "private":
                                    accessModifier = "-";
                                    break;
                                case "public":
                                    accessModifier = "+";
                                    break;
                                case "protected":
                                    accessModifier = "#";
                                    break;
                            }
                            namesList.remove(i);
                            break;
                        }
                    }
                    newText.insert(0, accessModifier + " " + namesList.get(0) + " ");
                }
                if (nameAndParameters.length == 1) newText.append(")");
                else newText.append(nameAndParameters[1]).append(")");
                if (isStatic) newText.append(";static");
                methods.add(newText.toString());
                depth++;
            } else if (line.contains("{")) depth++;
            if (line.contains("}")) depth--;
        }
        scan.close();
        sort(attributes);
        sort(methods);
        rec = new com.example.RectangleClass(name, attributes, methods);
        imported=true;
        addClass();
    }

    public void sort(List<String> arr) {
        for (int i = 0; i < arr.size(); i++) {
            String smallest = arr.get(i);
            int index = i;
            for (int j = i; j < arr.size(); j++) {
                if (smallest.compareTo(arr.get(j)) < 0) {
                    smallest = arr.get(j);
                    index = j;
                }
            }
            String temp = arr.get(i);
            arr.set(i, smallest);
            arr.set(index, temp);
        }
    }
}


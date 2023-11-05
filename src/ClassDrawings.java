package com.example;

import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

class ClassDrawings{
    int theLongestPhrase=5;
    ClassRows name;
    List<Rectangle> rectangles;
    List<ImageView> images;
    List<ClassRows> attributeRows;
    List<ClassRows> methodRows;
    Rectangle nameSpace;
    Rectangle attributeSpace;
    Rectangle methodSpace;
    Rectangle changingParametersAttribute;
    Rectangle changingParametersMethod;
    ImageView deleteClass;
    String className;
    AnchorPane root;

    public ClassDrawings(Rectangle nameSpace, Rectangle attributeSpace, Rectangle methodSpace, AnchorPane root, String className, Rectangle changingParametersAttribute, Rectangle changingParametersMethod, ImageView deleteClass) {
        this.nameSpace = nameSpace;
        this.attributeSpace = attributeSpace;
        this.methodSpace = methodSpace;
        this.className = className;
        this.root = root;
        this.rectangles = new ArrayList<>();
        this.images = new ArrayList<>();
        this.attributeRows = new ArrayList<>();
        this.methodRows = new ArrayList<>();
        this.changingParametersAttribute = changingParametersAttribute;
        this.changingParametersMethod = changingParametersMethod;
        this.deleteClass = deleteClass;
    }
}

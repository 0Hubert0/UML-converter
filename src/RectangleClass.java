package com.example;

import java.util.List;

public class RectangleClass {
    String name;
    List<String> attributes;
    List<String> methods;

    public RectangleClass(String name, List<String> attributes, List<String> methods) {
        this.name = name;
        this.attributes = attributes;
        this.methods = methods;
    }

    public String getName() {
        return name;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public List<String> getMethods() {
        return methods;
    }
}

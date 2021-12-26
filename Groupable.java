package com.example.a4basics;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;

public interface Groupable {
    boolean hasChildren();
    ArrayList<Groupable> getChildren();
    boolean contains(double x, double y);
    boolean isContained(double x1, double y1, double x2, double y2);

    void drawGroup(GraphicsContext gc);
    void moveGroup(double dx, double dy);

    double getLeft(); // bounding box of the group
    double getRight();
    double getTop();
    double getBottom();
    double[] getDisplayXs();
    double[] getDisplayYs();
    void setZ(int newZ); // Z-order for the group
    int getZ();
    void setSize(int newSize); //size of the groupable
    int getSize();

    void rotate(double a);
    void rotate(double a, double x, double y);
}

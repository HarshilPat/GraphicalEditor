package com.example.a4basics;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.DoubleStream;

public class Ship implements Groupable{
    double translateX, translateY;
    double[] xs = {0,20,0,-20,0};
    double[] ys = {24,-20,-12,-20,24};
    double shipWidth, shipHeight;
    double[] displayXs, displayYs;
    WritableImage buffer;
    PixelReader reader;
    double clickX, clickY;
    double clickX1, clickY1,clickX2, clickY2;
    int z,size;


    public Ship(double newX, double newY) {
        Canvas shipCanvas;
        GraphicsContext gc;
        size=1;

        translateX = newX;
        translateY = newY;
        double minVal = DoubleStream.of(xs).min().getAsDouble();
        double maxVal = DoubleStream.of(xs).max().getAsDouble();
        shipWidth = maxVal - minVal;
        minVal = DoubleStream.of(ys).min().getAsDouble();
        maxVal = DoubleStream.of(ys).max().getAsDouble();
        shipHeight = maxVal - minVal;
        displayXs = new double[xs.length];
        displayYs = new double[ys.length];
        for (int i = 0; i < displayXs.length; i++) {
            displayXs[i] = xs[i] + shipWidth/2;
            displayYs[i] = ys[i] + shipHeight/2;
        }

        shipCanvas = new Canvas(shipWidth,shipHeight);
        gc = shipCanvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillPolygon(displayXs, displayYs, displayXs.length);
        buffer = shipCanvas.snapshot(null,null);
        reader = buffer.getPixelReader();

        for (int i = 0; i < displayXs.length; i++) {
            displayXs[i] = xs[i] + translateX;
            displayYs[i] = ys[i] + translateY;
        }
    }

    public double[] getDisplayXs(){
        return displayXs;
    }
    public double[] getDisplayYs(){
        return displayYs;
    }
    public Ship(Groupable aShip){
        Canvas shipCanvas;
        GraphicsContext gc;
        size=1;

        translateX = (aShip.getLeft()+aShip.getRight())/2;
        translateY = (aShip.getTop()+aShip.getBottom())/2;

        double minVal = DoubleStream.of(xs).min().getAsDouble();
        double maxVal = DoubleStream.of(xs).max().getAsDouble();
        shipWidth = maxVal - minVal;
        minVal = DoubleStream.of(ys).min().getAsDouble();
        maxVal = DoubleStream.of(ys).max().getAsDouble();
        shipHeight = maxVal - minVal;
        displayXs = new double[xs.length];
        displayYs = new double[ys.length];
        for (int i = 0; i < displayXs.length; i++) {
            displayXs[i] = xs[i] + shipWidth/2;
            displayYs[i] = ys[i] + shipHeight/2;
        }

        shipCanvas = new Canvas(shipWidth,shipHeight);
        gc = shipCanvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillPolygon(displayXs, displayYs, displayXs.length);
        buffer = shipCanvas.snapshot(null,null);
        reader = buffer.getPixelReader();

        for (int i = 0; i < displayXs.length; i++) {
            displayXs[i] = xs[i] + translateX;
            displayYs[i] = ys[i] + translateY;
        }
    }

    public boolean hasChildren() {
        return false;
    }

    public ArrayList<Groupable> getChildren() {
        return null;
    }

    public boolean contains(double x, double y) {
        clickX = x - translateX + shipWidth/2;
        clickY = y - translateY + shipHeight/2;
        // check bounding box first, then bitmap
        boolean inside = false;
        if (clickX >= 0 && clickX <= shipWidth && clickY >= 0 && clickY <= shipHeight) {
            if (reader.getColor((int) clickX, (int) clickY).equals(Color.BLACK)) inside = true;
        }
        return inside;
    }

    public boolean isContained(double x1, double y1, double x2, double y2){
        clickX1 = Arrays.stream(displayXs).min().getAsDouble();
        clickY1 = Arrays.stream(displayYs).min().getAsDouble();
        clickX2 = Arrays.stream(displayXs).max().getAsDouble();
        clickY2 = Arrays.stream(displayYs).max().getAsDouble();
        boolean inside;
        if (clickX1 >= x1 && clickY1 >= y1 && clickX2 <= x2 && clickY2 <= y2){
            return true;
        }else {
            return false;
        }
    }

    public void drawGroup(GraphicsContext gc) {
        gc.fillPolygon(displayXs,displayYs, displayXs.length);
        gc.setStroke(Color.YELLOW);
        gc.strokePolygon(displayXs,displayYs, displayXs.length);
    }

    @Override
    public void moveGroup(double dx, double dy) {

        for (int i = 0; i < displayXs.length; i++) {
            displayXs[i] += dx;
            displayYs[i] += dy;
        }
        translateX += dx;
        translateY += dy;
    }

    @Override
    public double getLeft() {
        return Arrays.stream(displayXs).min().getAsDouble();
    }

    @Override
    public double getRight() {
        return Arrays.stream(displayXs).max().getAsDouble();
    }

    @Override
    public double getTop() {
        return Arrays.stream(displayYs).min().getAsDouble();
    }

    @Override
    public double getBottom() {
        return Arrays.stream(displayYs).max().getAsDouble();
    }

    @Override
    public void setZ(int newZ) {
        z=newZ;
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public void setSize(int newSize) {
        size=newSize;
    }

    @Override
    public int getSize() {
        return size;
    }

    public void moveShip(double dx, double dy) {
        for (int i = 0; i < displayXs.length; i++) {
            displayXs[i] += dx;
            displayYs[i] += dy;
        }
        translateX += dx;
        translateY += dy;
    }

    public void rotate(double a) {
        rotate(a,translateX,translateY);
    }

    public void rotate(double a, double cx, double cy) {
        double x, y;
        double radians = a * Math.PI / 180;
        for (int i = 0; i < displayXs.length; i++) {
            x = displayXs[i] - cx;
            y = displayYs[i] - cy;
            displayXs[i] = rotateX(x, y, radians) + cx;
            displayYs[i] = rotateY(x, y, radians) + cy;
        }
    }

    private double rotateX(double x, double y, double thetaR) {
        return Math.cos(thetaR) * x - Math.sin(thetaR) * y;
    }

    private double rotateY(double x, double y, double thetaR) {
        return Math.sin(thetaR) * x + Math.cos(thetaR) * y;
    }
}

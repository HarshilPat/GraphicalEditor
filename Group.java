package com.example.a4basics;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public class Group implements Groupable, Serializable {
    ArrayList<Groupable> groups;
    double left,right,top,bottom;

    public Group(){
        groups=new ArrayList<>();
        left=Double.MAX_VALUE;
        top = Double.MAX_VALUE;
        right = 0;
        bottom = 0;
    }

    public Group(Groupable agroUp){
        this();
        if (agroUp.hasChildren()){
            for (Groupable g: agroUp.getChildren()){
                Group ng = new Group(g);
                add(ng);
            }
        }else {
            Ship nS = new Ship(agroUp);
            add(nS);
        }
    }
    public void add(Groupable g){
        groups.add(g);
        left = Math.min(g.getLeft(), left);
        top = Math.min(g.getTop(), top);
        right = Math.max(g.getRight(), right);
        bottom = Math.max(g.getBottom(), bottom);
    }

    public boolean hasChildren() {
        return !groups.isEmpty();
    }

    public ArrayList<Groupable> getChildren() {
        return groups;
    }


    public boolean contains(double x, double y) {
        boolean found = false;
        for (Groupable grp : groups) {
            if (grp.contains(x, y)) {
                found = true;
            }
        }
        return found;
    }

    public boolean isContained(double x1, double y1, double x2, double y2) {
        boolean allIn = true;
        for (Groupable grp : groups) {
            if (!grp.isContained(x1, y1, x2, y2)) {
                allIn = false;
            }
        }
        return allIn;
    }

    public void drawGroup(GraphicsContext gc) {
        for (Groupable grp : groups) {
            grp.drawGroup(gc);
        }
        gc.setStroke(Color.WHITE);
        if(this.groups.size() > 1) {
            gc.strokeRect(left, top, right - left, bottom - top);
        }
    }

    public void moveGroup(double dx, double dy) {
        for (Groupable g : groups) {
            g.moveGroup(dx, dy);
        }
        left += dx;
        top += dy;
        right += dx;
        bottom += dy;
    }

    public double getLeft() {
        left=groups.stream().min(Comparator.comparing(Groupable::getLeft)).get().getLeft();
        return left;
    }


    public double getRight() {
        right = groups.stream().max(Comparator.comparing(Groupable::getRight)).get().getRight();
        return right;
    }


    public double getTop() {
        top=groups.stream().min(Comparator.comparing(Groupable::getTop)).get().getTop();
        return top;
    }


    public double getBottom() {
        bottom=groups.stream().max(Comparator.comparing(Groupable::getBottom)).get().getBottom();
        return bottom;
    }

    @Override
    public double[] getDisplayXs() {
        return new double[0];
    }

    @Override
    public double[] getDisplayYs() {
        return new double[0];
    }

    public void setZ(int newZ) {
        System.out.println("SetZ for group");
    }

    public int getZ() {
        System.out.println("getZ for group");
        return 0;
    }

    public void setSize(int newSize) {
        System.out.println("Size for group");
    }

    public int getSize() {
        return groups.size();
    }
    public void rotate(double a) {
        for (Groupable g: groups){
            g.rotate(a);
        }
    }

    public void rotate(double a, double x, double y) {
        for (Groupable g: groups){
            g.rotate(a, x, y);
        }
    }
}

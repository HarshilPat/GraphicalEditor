package com.example.a4basics;

public class RubberRectangle {
    double left,top,width,height;

    public RubberRectangle(double x1, double y1){
        left = x1;
        top = y1;
    }

    public void updateCoOrdinates(double x2, double y2){
        left = Math.min(left, x2);
        top = Math.min(top, y2);
        width = Math.max(left, x2) - left;
        height = Math.max(top, y2) - top;
    }
}

package com.example.a4basics;

import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class ShipView extends StackPane implements ShipModelSubscriber {
    Canvas myCanvas;
    GraphicsContext gc;
    ShipModel model;
    InteractionModel iModel;
    Slider rotationSlider;

    public ShipView() {
        myCanvas = new Canvas(1000,700);
        gc = myCanvas.getGraphicsContext2D();
        rotationSlider=new Slider(-180,180,0);
        rotationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            rotaTion(oldValue.doubleValue(), newValue.doubleValue());
        });
        this.getChildren().add(myCanvas);
        StackPane.setAlignment(rotationSlider, Pos.TOP_CENTER);
        this.getChildren().add(rotationSlider);
        this.setStyle("-fx-background-color: black");
    }

    public void setModel(ShipModel newModel) {
        model = newModel;
    }

    public void setInteractionModel(InteractionModel newIModel) {
        iModel = newIModel;
    }

    public void setController(ShipController controller) {
        myCanvas.setOnMousePressed(e -> controller.handlePressed(e.getX(),e.getY(), e));
        myCanvas.setOnMouseDragged(e -> controller.handleDragged(e.getX(),e.getY(), e));
        myCanvas.setOnMouseReleased(e -> controller.handleReleased(e.getX(),e.getY(), e));
        myCanvas.setOnKeyPressed(controller::handleKeyPressed);
        myCanvas.setOnKeyReleased(controller::handleKeyReleased);
    }

    private void rotaTion(double old, double nValue){
        for (Groupable g: iModel.selectionSet){
            if (g.hasChildren()){
                g.rotate(nValue-old, g.getLeft() + (g.getRight()-g.getLeft())/2, g.getTop() + (g.getBottom()-g.getTop())/2);
            } else {
                g.rotate(nValue-old);
            }modelChanged();
        }
    }

    public void draw() {
        gc.clearRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());
        if (iModel.hasRubberBand()){
            gc.setFill(Color.YELLOW);
            gc.fillRect(iModel.rubberRect.left,iModel.rubberRect.top,iModel.rubberRect.width,iModel.rubberRect.height);
        }
//        model.ships.forEach(ship -> {
        for (Groupable g: model.ships){
            if (g.getSize()==1){
                drawShip(g);
            }
            if (g.getSize()>1){
                if (iModel.isSelected(g)){
                    gc.setFill(Color.YELLOW);
                    gc.setStroke(Color.CORAL);
                }else {
                    gc.setStroke(Color.YELLOW);
                    gc.setFill(Color.CORAL);
                }
                drawGroup(g);
                drawRect(g.getLeft(), g.getTop(),g.getRight(),g.getBottom());
            }
        }
    }

    private void drawRect(double x1, double y1, double x2, double y2){
        gc.setStroke(Color.WHITE);
        gc.strokeOval((x1+x2)/2,(y1+y2)/2,7,7);
        gc.strokeRect(x1,y1,x2-x1,y2-y1);

    }
    private void drawGroup(Groupable g){
        if (g.hasChildren()){
            g.getChildren().forEach(this::drawGroup);
        }else {
            drawShip(g);
        }
    }
    private void drawShip(Groupable s){
        if (iModel.selectionSet.contains(s)) {
//             == s
            gc.setFill(Color.YELLOW);
            gc.setStroke(Color.CORAL);
        } else {
            gc.setStroke(Color.YELLOW);
            gc.setFill(Color.CORAL);
        }
        gc.fillPolygon(s.getDisplayXs(), s.getDisplayYs(), s.getDisplayXs().length);
        gc.strokePolygon(s.getDisplayXs(), s.getDisplayYs(), s.getDisplayXs().length);
    }

    @Override
    public void modelChanged() {
        draw();
    }
}

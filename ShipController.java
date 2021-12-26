package com.example.a4basics;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShipController {
    InteractionModel iModel;
    ShipModel model;
    ShipClipboard clipboard;
    double prevX, prevY, dragStartX, dragStartY;
    double dX, dY;

    protected enum State {
        READY, DRAGGING, CONTROL_READY, CONTROL_DRAGGING, CONTROL_BACKGROUND,
        CONTROL_RUBBERBAND, RUBBERBAND, BACKGROUND
    }

    protected State currentState;

    public ShipController() {
        currentState = State.READY;
        clipboard= new ShipClipboard();
    }

    public void setInteractionModel(InteractionModel newModel) {
        iModel = newModel;
    }

    public void setModel(ShipModel newModel) {
        model = newModel;
    }

    public void handlePressed(double x, double y, MouseEvent event) {
        prevX = x;
        prevY = y;
//        Optional<Groupable> hit = model.detectHit(x, y);
        switch (currentState) {
            case READY -> {
                // context: on a ship?
                Optional<Groupable> hit = model.detectHit(x, y);
                if (hit.isPresent()) {
                    // on ship, so select
                    Groupable s = hit.get();
                    model.raiseShip(s);
                    if (event.isControlDown()) {
                        iModel.addSubSelection(s);
                    } else if (iModel.isSelected(s)) {
                        dragStartX = x;
                        dragStartY = y;
                        currentState = State.DRAGGING;
                    } else {
                        iModel.clearSelection();
                        iModel.addSubSelection(s);
//                        dragStartX=x;
//                        dragStartY=y;
//                        currentState=State.DRAGGING;
                    }
                } else {
                    // on background - is Shift down?
                    if (event.isShiftDown()) {
                        // create ship
                        Ship newShip = model.createShip(x, y);
                        iModel.addSubSelection(newShip);
                        currentState = State.DRAGGING;
                    } else {
                        // clear selection
                        iModel.clearSelection();
                        currentState = State.BACKGROUND;
                    }
                }
                break;
            }
            case CONTROL_READY -> {
                // context: on a ship?
                Optional<Groupable> hit = model.detectHit(x, y);
                if (hit.isPresent()) {
                    Groupable s = hit.get();
                    model.raiseShip(s);
                    // on ship, so select
//                    Ship s = hit.get();
                    iModel.addSubSelection(s);
//                    iModel.setSelected(s);
                    if (iModel.isSelected(s)) {
                        dragStartX = event.getX();
                        dragStartY = event.getY();
                        currentState = State.CONTROL_DRAGGING;
                    }
                } else {
                    currentState = State.CONTROL_BACKGROUND;
                }
                break;
            }
        }
    }

    public void handleDragged(double x, double y, MouseEvent event) {

        switch (currentState) {
            case DRAGGING, CONTROL_DRAGGING -> {
                dX = x - prevX;
                dY = y - prevY;
                model.moveShips(iModel.selectionSet, dX, dY);
                prevX = x;
                prevY = y;
                break;
            }
            case BACKGROUND, CONTROL_BACKGROUND -> {
                iModel.setRubberStart(event.getX(), event.getY());
                currentState = State.RUBBERBAND;
                break;
            }
            case RUBBERBAND -> {
                iModel.setRubberEnd(event.getX(), event.getY());
                break;
            }
//            case CONTROL_BACKGROUND -> {
//                iModel.setRubberStart(x,y);
//                currentState = State.CONTROL_RUBBERBAND;
//                break;
//            }
            case CONTROL_RUBBERBAND -> {
                iModel.setRubberEnd(x, y);
                break;
            }
        }
    }

    public void handleReleased(double x, double y, MouseEvent event) {
        ArrayList<Groupable> rubberSet;
        switch (currentState) {
            case DRAGGING, CONTROL_DRAGGING -> {
                dX = event.getX() - dragStartX;
                dY = event.getY() - dragStartY;
                currentState = State.READY;
                break;
            }
//            case CONTROL_DRAGGING -> {
//                dX=event.getX()-dragStartX;
//                dY=event.getY()-dragStartY;
//                currentState = State.CONTROL_READY;
//                break;
//            }
            case BACKGROUND -> {
                iModel.clearSelection();
                currentState = State.READY;
                break;
            }
            case RUBBERBAND -> {
                rubberSet = model.detectRubber(iModel.rubberRect.left, iModel.rubberRect.top, iModel.rubberRect.left + iModel.rubberRect.width,
                        iModel.rubberRect.top + iModel.rubberRect.height);
                rubberSet.forEach(s -> {
                    if (event.isControlDown()) {
                        addRemSelection(s);
                    } else {
                        iModel.addSubSelection(s);
                    }
                });
//                iModel.addSubSelection(rubberSet);
                iModel.deleteRubberBand();
                currentState = State.READY;
                break;
            }
            case CONTROL_RUBBERBAND -> {
                rubberSet = model.detectRubber(iModel.rubberRect.left, iModel.rubberRect.top, iModel.rubberRect.left + iModel.rubberRect.width,
                        iModel.rubberRect.top + iModel.rubberRect.height);
//                iModel.addSubSelection(rubberSet);
                rubberSet.forEach(s -> {
                    if (event.isControlDown()) {
                        addRemSelection(s);
                    } else {
                        iModel.addSubSelection(s);
                    }
                });
                iModel.deleteRubberBand();
                currentState = State.CONTROL_READY;
                break;
            }
        }
    }

    private void addRemSelection(Groupable sh) {
        if (iModel.selectionSet.contains(sh)) {
            iModel.selectionSet.remove(sh);
        } else {
            iModel.selectionSet.add(sh);
        }
    }

    private void addRemSelection(ArrayList<Groupable> ships) {
        for (Groupable s : ships) {
            addRemSelection(s);
        }
    }

    public void handleKeyPressed(KeyEvent keyEvent) {
        System.out.println(keyEvent.getCode());
        if (keyEvent.getCode() == KeyCode.G) {
            Groupable temp = model.createGroup(iModel.selectionSet);
            iModel.clearSelection();
            iModel.selectionSet.add(temp);
            model.modelChanged();
        } else if (keyEvent.getCode() == KeyCode.U) {
            if (iModel.selectionSet.size() == 1 && iModel.selectionSet.get(0).hasChildren()) {
                ArrayList<Groupable> items = model.unGroup(iModel.selectionSet.get(0));
                iModel.selectionSet.clear();
                addRemSelection(items);
                model.modelChanged();
            }
        } else if (keyEvent.getCode()==KeyCode.C && keyEvent.isControlDown()){
            if (iModel.selectionSet.size()>0){
                clipboard.copy(iModel.selectionSet);
            }
        }else if (keyEvent.getCode()==KeyCode.V && keyEvent.isControlDown()){
            if (clipboard.group.size()>0){
                iModel.selectionSet.clear();
                iModel.selectionSet=clipboard.paste();
                model.addGroup(iModel.selectionSet);
            }
        }else if (keyEvent.getCode()==KeyCode.X && keyEvent.isControlDown()){
            if (iModel.selectionSet.size()>0){
                clipboard.copy(iModel.selectionSet);
                model.deleteGroups(iModel.selectionSet);
            }
        }
    }

    public void handleKeyReleased(KeyEvent keyEvent) {
//        if (keyEvent.getCode() == KeyCode.CONTROL) {
//            switch (currentState) {
//                case CONTROL_READY -> {
//                    iModel.setControlDown(false);
//                    currentState = State.READY;
//                    break;
//                }
//                case CONTROL_DRAGGING -> {
//                    iModel.setControlDown(false);
//                    currentState = State.DRAGGING;
//                    break;
//                }
//                case CONTROL_RUBBERBAND -> {
//                    iModel.setControlDown(false);
//                    currentState = State.RUBBERBAND;
//                    break;
//                }
//            }
//        }else if (keyEvent.getCode()==KeyCode.G){
//            if (iModel.selectionSet.size()>1){
//                Groupable temporary = model.createGroup(iModel.selectionSet);
//                iModel.selectionSet.clear();
//                iModel.selectionSet.add(temporary);
//                model.modelChanged();
//            }
//        }else if (keyEvent.getCode()==KeyCode.U){
//            if (iModel.selectionSet.size()==1 && iModel.selectionSet.get(0).hasChildren()){
//                ArrayList<Groupable> items = model.unGroup(iModel.selectionSet.get(0));
//                iModel.selectionSet.clear();
//                addRemSelection(items);
//                model.modelChanged();
//            }
//        }else if (keyEvent.getCode()==KeyCode.C && keyEvent.isControlDown()){
//            if (iModel.selectionSet.size()>0){
//                clipboard.copy(iModel.selectionSet);
//            }
//        }else if (keyEvent.getCode()==KeyCode.V && keyEvent.isControlDown()){
//            if (clipboard.group.size()>0){
//                iModel.selectionSet.clear();
//                iModel.selectionSet=clipboard.paste();
//                model.addGroup(iModel.selectionSet);
//            }
//        }else if (keyEvent.getCode()==KeyCode.X && keyEvent.isControlDown()){
//            if (iModel.selectionSet.size()>0){
//                clipboard.copy(iModel.selectionSet);
//                model.deleteGroups(iModel.selectionSet);
//            }
//        }
    }
}

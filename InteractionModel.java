package com.example.a4basics;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InteractionModel {
    ArrayList<ShipModelSubscriber> subscribers;
    Ship selectedShip;
    ArrayList<Groupable> selectionSet;
    boolean controlDown;
    RubberRectangle rubberRect;

    public InteractionModel() {
        subscribers = new ArrayList<>();
        selectionSet = new ArrayList<>();
        controlDown = false;
        rubberRect=null;
    }

    public void setControlDown(boolean isIT){
        controlDown = isIT;
        notifySubscribers();
    }

    public boolean isSelected(Groupable sh){
        return selectionSet.contains(sh);
    }

    public void addSubSelection(Groupable sh){
        if (selectionSet.contains(sh)){
            selectionSet.remove(sh);
        }else {
            selectionSet.add(sh);
            selectionSet=(ArrayList<Groupable>) selectionSet.stream().distinct().collect(Collectors.toList());
        }
        notifySubscribers();
    }

    public void addSubSelection(List<Groupable> set){
        set.forEach(sh -> addSubSelection(sh));
    }

    public void clearSelection() {
        selectedShip = null;
        selectionSet.clear();
        notifySubscribers();
    }

    public boolean hasRubberBand(){
        return (rubberRect != null);
    }

    public void deleteRubberBand(){
        rubberRect = null;
        notifySubscribers();
    }

    public void setRubberStart(double x1, double y1){
        rubberRect = new RubberRectangle(x1, y1);
    }

    public void setRubberEnd(double x2, double y2){
        rubberRect.updateCoOrdinates(x2,y2);
        notifySubscribers();
    }

//    public void setSelected(Ship newSelection) {
//        selectedShip = newSelection;
//        addSubSelection(newSelection);
//        notifySubscribers();
//    }

    public void addSubscriber(ShipModelSubscriber aSub) {
        subscribers.add(aSub);
    }

    private void notifySubscribers() {
        subscribers.forEach(sub -> sub.modelChanged());
    }
}

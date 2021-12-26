package com.example.a4basics;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ShipModel {
    public ArrayList<Groupable> ships;
    ArrayList<ShipModelSubscriber> subscribers;
    int nextZ;

    public ShipModel() {
        subscribers = new ArrayList<>();
        ships = new ArrayList<>();
        nextZ=0;
    }

    public void addShip(Groupable g){
        ships.add(g);
        notifySubscribers();
    }

    public Ship createShip(double x, double y) {
        Ship s = new Ship(x,y);
        ships.add(s);
        notifySubscribers();
        return s;
    }
    public Groupable createGroup(ArrayList<Groupable> newg){
        Group newG = new Group();
        for(Groupable g : newg){
            ships.remove(g);
            newG.add(g);
        }
        ships.add(newG);
        notifySubscribers();
        return newG;
    }

    public void addGroup(ArrayList<Groupable> aGroup){
        for (Groupable g: aGroup){
            ships.add(g);
        }
        notifySubscribers();
    }

    public ArrayList<Groupable> unGroup(Groupable unG){
        ArrayList<Groupable> unGroupItem = new ArrayList<>();
        for(Groupable g: unG.getChildren()){
            unGroupItem.add(g);
            ships.add(g);
        }
        ships.remove(unG);
        notifySubscribers();
        return unGroupItem;
    }
    public List<Groupable> getShips(){return ships;}

    public Optional<Groupable> detectHit(double x, double y) {
        return ships.stream().filter(s -> s.contains(x, y)).reduce((first, second) -> second);
    }

    public ArrayList<Groupable> detectRubber(double x1, double y1, double x2, double y2){
        return (ArrayList<Groupable>) ships.stream().filter(ship -> ship.isContained(x1,y1,x2,y2)).collect(Collectors.toList());
    }

    public void moveShip(Ship b, double dX, double dY) {
        b.moveShip(dX,dY);
        notifySubscribers();
    }

    public void moveShips(List<Groupable> ships, double dx, double dy){
        ships.forEach(ship -> ship.moveGroup(dx, dy));
        notifySubscribers();
    }

    public void raiseShip(Groupable s){
        int newZ = nextZ++;
        s.setZ(newZ);
        ships.sort((s1,s2)->s1.getZ()-s2.getZ());
        notifySubscribers();
    }

    public void deleteGroups(ArrayList<Groupable> delete){
        for (Groupable g: delete){
            deleteGroup(g);
        }
        notifySubscribers();
    }

    public void deleteGroup(Groupable toDelete){
        if (ships.contains(toDelete)){
            ships.remove(toDelete);
        } else {
            System.out.println("ERROR");
        }
        notifySubscribers();
    }

    public void addSubscriber (ShipModelSubscriber aSub) {
        subscribers.add(aSub);
    }
    public void modelChanged(){notifySubscribers();}

    private void notifySubscribers() {
        subscribers.forEach(sub -> sub.modelChanged());
    }
}

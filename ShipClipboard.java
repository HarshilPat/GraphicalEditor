package com.example.a4basics;

import java.util.ArrayList;

public class ShipClipboard {
    ArrayList<Groupable> group;
    public ShipClipboard(){group=new ArrayList<>();}

    public void copy(ArrayList<Groupable> g){
        group.clear();
        for (Groupable grp: g){
            if (grp.hasChildren()){
                group.add(new Group(grp));
            }else {
                group.add(new Ship(grp));
            }
        }
    }

    public void cut(ArrayList<Groupable> g){
        group.clear();
        group= (ArrayList<Groupable>) g.clone();
    }

    public ArrayList<Groupable> paste() {
        ArrayList<Groupable> copy = new ArrayList<>();
        for (Groupable g : group) {
            if (g.hasChildren()) {
                copy.add(new Group(g));
            } else {
                copy.add(new Ship(g));
            }
        }
        return copy;
    }
}

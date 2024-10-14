package com.example.application.Domain;

public class Location {
    private int id;
    private String Loc;

    public Location() {
    }

    @Override
    public String toString() {
        return Loc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLoc() {
        return Loc;
    }

    public void setLoc(String loc) {
        Loc = loc;
    }
}

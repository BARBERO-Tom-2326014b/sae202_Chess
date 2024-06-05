package com.example.sae202;

public abstract class Pieces {
    protected String type;
    protected String color;

    public Pieces(String type, String color) {
        this.type = type;
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public String getColor() {
        return color;
    }
}

package com.anwarpro.libgdxpractise.entity;

import com.badlogic.gdx.graphics.Color;

public class TutorMessage {
    private String message; // string objects can not be tweened
    private float x;
    private float y;
    private Color color;
    private float scale;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}

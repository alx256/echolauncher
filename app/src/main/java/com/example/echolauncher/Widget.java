package com.example.echolauncher;

import android.graphics.drawable.Drawable;

import java.util.Dictionary;
import java.util.Hashtable;

public class Widget {
    Widget() {
        textPositions = new Hashtable<>();
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getColor() { return color; }

    public String getPosition(char position) {
        return textPositions.get(position);
    }

    public void Tick() {}

    protected String identifier;
    protected int color;
    protected Dictionary<Character, String> textPositions;
    protected Dictionary<Character, Drawable> drawablePositions;
}

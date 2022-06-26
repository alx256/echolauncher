package com.example.echolauncher.widget_drawer;

import java.util.Hashtable;
import java.util.Map;

/**
 * Abstract widget class
 */

public class Widget {
    public Widget() {
        textPositions = new Hashtable<>();
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getColor() {
        return color;
    }

    public String getPosition(char position) {
        return textPositions.get(position);
    }

    // Needs to be overridden by subclass
    public void tick() {}

    // Needs to be overridden by subclass
    public void onTap() {}

    protected String identifier;
    protected int color;
    protected Map<Character, String> textPositions;
}

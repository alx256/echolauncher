package com.example.echolauncher;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Dictionary;
import java.util.Hashtable;

public class Widget {
    Widget() {
        textPositions = new Hashtable<>();
    }

    public String getIdentifier() {
        return identifier;
    }

    public void Tick() {}

    protected String identifier;
    protected int color;
    protected Dictionary<Character, String> textPositions;
    protected Dictionary<Character, Drawable> drawablePositions;
}

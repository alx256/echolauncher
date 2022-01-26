package com.example.echolauncher;

import android.util.Log;

import androidx.annotation.NonNull;

public class Widget {
    public String getIdentifier() {
        return identifier;
    }

    public void Tick() {
    }

    protected String identifier;
    private Thread tickThread;
}

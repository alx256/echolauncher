package com.example.echolauncher;

import android.graphics.drawable.Drawable;

public class AppItem {
    private String name, packageName;
    private int iconIndex;

    public AppItem() {
        empty = true;
    }

    public AppItem(String name, String packageName, int iconIndex) {
        this.name = name;
        this.packageName = packageName;
        this.iconIndex = iconIndex;

        empty = false;
    }

    public String getName() { return name; }
    public String getPackageName() { return packageName; }
    public int getIconIndex() { return iconIndex; }

    public boolean empty;
}
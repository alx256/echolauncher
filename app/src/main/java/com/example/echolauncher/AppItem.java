package com.example.echolauncher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class AppItem extends PinItem {
    public AppItem() {
        setEmpty();
    }

    public AppItem(String name, String packageName, int iconIndex) {
        super.name = name;
        super.identifier = packageName;
        super.iconIndex = iconIndex;
        setSuper();

        empty = false;
    }

    private void setEmpty() {
        empty = true;
        super.iconIndex = -1;
        setSuper();
    }

    private void setSuper() {
        super.imageHeight = Globals.appIconHeight;
        super.imageWidth = Globals.appIconWidth;
        super.textHeight = Globals.appTextHeight;
        super.textSize = Globals.appTextSize;
        super.isWidget = false;
    }

    public View toView(Context context) {
        View finalView;
        super.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        finalView = inflater.inflate(R.layout.item_app, null, false);
        finalView = super.toView(finalView);
        return finalView;
    }

    public View copyView() {
        return toView(context);
    }
}
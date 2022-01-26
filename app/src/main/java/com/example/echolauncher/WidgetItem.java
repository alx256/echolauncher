package com.example.echolauncher;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

public class WidgetItem extends PinItem {
    public WidgetItem(String name, Widget widget) {
        super.name = name;
        super.identifier = widget.getIdentifier();
        super.iconIndex = 0;
        this.widget = widget;
        setSuper();

        empty = false;
    }

    private void setSuper() {
        super.imageHeight = imageHeight;
        super.imageWidth = imageWidth;
        super.textHeight = textHeight;
        super.textSize = textSize;
        super.isWidget = true;
    }

    public View toView(Context context) {
        View finalView;
        super.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        finalView = inflater.inflate(R.layout.item_widget, null, false);
        finalView = super.toView(finalView);
        return finalView;
    }

    public Widget getWidget() {
        return widget;
    }

    private int imageHeight = 150, imageWidth = 150 * 4, textSize = 12,
            textHeight = 55;
    private String name, packageName;
    private int iconIndex;
    private Widget widget;
}
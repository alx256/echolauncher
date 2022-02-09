package com.example.echolauncher;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WidgetItem extends PinItem {
    public WidgetItem(String name, Widget widget) {
        super.name = name;
        super.identifier = widget.getIdentifier();
        super.iconIndex = -1;
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

        ImageView icon = finalView.findViewById(R.id.appIcon);
        icon.setColorFilter(widget.color);

        for (char c : new char[]{'L', 'R', 'U', 'D'})
            populate(finalView, c);

        return finalView;
    }

    public Widget getWidget() {
        return widget;
    }

    private void populate(View finalView, char character) {
        switch (character) {
            case 'L':
                textID = R.id.leftText;
                break;
            case 'R':
                textID = R.id.rightText;
                break;
            case 'U':
                textID = R.id.upText;
                break;
            case 'D':
                textID = R.id.downText;
                break;
            default:
                textID = -1;
        }

        if (widget.textPositions.get(character) != null) {
            textView = finalView.findViewById(textID);
            textView.setText(widget.textPositions.get(character));
        } else if (textID != -1) {
            textView = finalView.findViewById(textID);
            textView.setText("");
        }

//        if (widget.drawablePositions.get(character) != null) {
//            // will do
//        }
    }

    private int imageHeight = 150, imageWidth = 150 * 4, textSize = 12,
            textHeight = 55;
    private Widget widget;
    private TextView textView;
    private int textID;
    private Runnable tick;
}
package com.example.echolauncher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class WidgetItem extends PinItem {
    public WidgetItem(Name name, Widget widget) {
        super.name = name;
        super.identifier = widget.getIdentifier();
        super.iconIndex = -1;
        this.widget = widget;

        imageHeight = IMAGE_HEIGHT;
        imageWidth = IMAGE_WIDTH;
        textHeight = TEXT_HEIGHT;
        textSize = TEXT_SIZE;
        isWidget = true;

        empty = false;
    }

    public View toView(Context context) {
        View finalView;
        super.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        finalView = inflater.inflate(R.layout.item, null, false);
        super.initView(finalView);

        ImageView icon = finalView.findViewById(R.id.appIcon);
        icon.setColorFilter(widget.getColor());

        for (char c : new char[]{'L', 'R', 'U', 'D'})
            populate(finalView, c);

        TextView textView = finalView.findViewById(R.id.textView);
        textView.setVisibility(View.INVISIBLE);

        return finalView;
    }

    protected void populate(View finalView, char character) {
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

        textView = finalView.findViewById(textID);

        if (widget.getPosition(character) != null) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(widget.getPosition(character));
        } else if (textID != - 1) {
            if (textView.getVisibility() != View.INVISIBLE)
                textView.setVisibility(View.INVISIBLE);
        }

//        if (widget.drawablePositions.get(character) != null) {
//            // will do
//        }
    }

    public Widget getWidget() {
        return widget;
    }

    private final int IMAGE_HEIGHT = 150, IMAGE_WIDTH = 150 * 4, TEXT_SIZE = 12,
            TEXT_HEIGHT = 55;
    private Widget widget;
    private TextView textView;
}

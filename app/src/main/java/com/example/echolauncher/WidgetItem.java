package com.example.echolauncher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Item that represents a widget
 */

public class WidgetItem extends Item {
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

        isEmpty = false;
        references = new ArrayList<>();
    }

    @Override
    public View toView(Context context) {
        super.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        finalView = inflater.inflate(R.layout.item, null, false);
        super.initView(finalView);

        ImageView icon = finalView.findViewById(R.id.appIcon);
        icon.setColorFilter(widget.getColor());

        TextView textView = finalView.findViewById(R.id.textView);
        textView.setVisibility(View.INVISIBLE);

        return finalView;
    }

    public Widget getWidget() {
        return widget;
    }

    public void addReferenceView(View view) {
        // New view added that needs
        // to be updated with every
        // tick
        references.add(view);
    }

    public void update() {
        for (char c : new char[]{'L', 'R', 'U', 'D'})
            populate(c);
    }

    protected void populate(char character) {
        if (finalView == null) {
            // toView has not been called yet
            return;
        }

        character = Character.toUpperCase(character);

        // Finds the textView that we are trying to update
        // L = Left textView
        // R = Right textView
        // U = Up textView
        // Down = Down textView
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

        // Get all the textViews that need to be updated,
        // the textViews belonging to the widget in the
        // widget drawer along with any other instances
        // of pinned widgets
        List<TextView> textViews = new ArrayList<>();
        textViews.add(finalView.findViewById(textID));

        for (View view : references)
            textViews.add(view.findViewById(textID));

        for (TextView textView : textViews) {
            if (widget.getPosition(character) != null) {
                textView.setVisibility(View.VISIBLE);
                textView.setText(widget.getPosition(character));
            } else if (textID != -1) {
                if (textView.getVisibility() != View.INVISIBLE)
                    textView.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected void onTap() {
        widget.onTap();
    }

    private final int IMAGE_HEIGHT = 162, IMAGE_WIDTH = 150 * 4, TEXT_SIZE = 12,
            TEXT_HEIGHT = 55;
    private Widget widget;
    private List<View> references;
    private View finalView;
}

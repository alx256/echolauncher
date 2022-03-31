package com.example.echolauncher;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.View;
import android.widget.TextView;

import androidx.gridlayout.widget.GridLayout;

/**
 * This class is a custom version of Android's GridLayout.
 * It allows the user to pass in a listener that is called
 * when an app is dropped on the view and pins apps to it.
 * This is primarily used for the study mode interface in
 * order to allow the user to pin apps that can override
 * study mode
 */

// Inherit GridLayout class
public class DropTarget extends GridLayout {
    public interface OnDropListener {
        void onDrop(Item item);
    }

    public DropTarget(Context context) {
        super(context);
        init();
    }

    public DropTarget(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public void setOnDropListener(OnDropListener listener) {
        this.listener = listener;
    }

    public void setBackgroundText(String text) {
        TextView infoTextView = new TextView(getContext());
        LayoutParams params = new LayoutParams();
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.MATCH_PARENT;

        infoTextView.setText(text);
        infoTextView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "KumbhSans-Light.ttf");
        infoTextView.setTypeface(font);
        infoTextView.setLayoutParams(params);
        infoTextView.setTextSize(20.0f);
        addView(infoTextView);
    }

    public void removeBackgroundText() {
        View text = findViewWithTag("infoTextView");

        // Remove background text if this has not already been done
        if (text != null)
            removeView(text);
    }

    private void init() {
        setBackgroundColor(Color.GRAY);

        // Darken or lighten the view depending on if an app is being dragged
        // over it and call the drop listener when the app is dropped on it
        super.setOnDragListener((view, event) -> {
            if (event.getAction() == DragEvent.ACTION_DRAG_ENTERED)
                setBackgroundColor(Color.DKGRAY);

            if (event.getAction() == DragEvent.ACTION_DRAG_EXITED)
                setBackgroundColor(Color.GRAY);

            if (event.getAction() == DragEvent.ACTION_DROP) {
                setBackgroundColor(Color.GRAY);
                listener.onDrop(Library.getDragging());
            }

            return true;
        });
    }

    private OnDropListener listener;
}

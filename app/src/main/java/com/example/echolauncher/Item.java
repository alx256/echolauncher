package com.example.echolauncher;

import android.content.ClipData;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;

/**
 * The item class
 * Represents generic item that can
 * be pinned to the home screen
 */

public class Item {
    // Contains full and shortened app name
    public static class Name {
        public Name(String name) {
            this.name = name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String full() {
            return name;
        }

        public String shortened() {
            // Maximum number of characters before shortening the text
            String shortenedString;

            if (name.length() >= MAX_CHARS) {
                shortenedString = name.substring(0, MAX_CHARS);
                shortenedString += "...";
                return shortenedString;
            }

            return name;
        }

        private String name;
        private final int MAX_CHARS = 6;
    }

    // Runnable implementation that is used for determining
    // when the user has done a long press so that an item
    // can be dragged
    public class LongPressRunnable implements Runnable {
        public LongPressRunnable(View view, Item item) {
            VIEW = view;
            ITEM = item;
        }

        @Override
        public void run() {
            // Vibrate phone
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(55);

            // Start dragging the app
            ClipData data = ClipData.newPlainText(identifier,
                    identifier);
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(VIEW);
            VIEW.startDrag(data, shadowBuilder, VIEW, 0);
            Scroll.scrollTo(Scroll.HOME_SCREEN);
            stationary = false;
            Library.setDragging(ITEM.identifier);
        }

        private final View VIEW;
        private final Item ITEM;
    }

    public Name getName() { return name; }
    public String getIdentifier() { return identifier; }
    public int getGridIndex() { return gridIndex; }

    // The Grid Index is the index of
    // the item in the home screen grid
    public void setGridIndex(int gridIndex) {
        this.gridIndex = gridIndex;
    }

    public View.OnTouchListener getOnTouchListener() {
        Item temp = this;

        Handler handler = new Handler();

        return (view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (isMovable) {
                        hold = new LongPressRunnable(view, temp);
                        // Begin checking for long press
                        handler.postDelayed(hold, LONG_PRESS_DELAY);
                    }
                    break;
                case MotionEvent.ACTION_SCROLL:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    // User has lifted their finger- abort long press
                    handler.removeCallbacks(hold);

                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        view.performClick();
                        onTap();
                    }
                    break;
            }

            return true;
        };
    }

    public View.OnDragListener getOnDragListener() {
        // Handle dragging events
        return (view, dragEvent) -> {
            if (stationary)
                return false;

            if (!isMovable)
                return false;

            if (dragEvent.getAction() == DragEvent.ACTION_DRAG_ENDED) {
                Scroll.scrollBack();
                stationary = true;
            }

            return true;
        };
    }

    protected void initView(View view) {
        imageView = view.findViewById(R.id.appIcon);
        textView = view.findViewById(R.id.textView);

        imageView.getLayoutParams().height = imageHeight;
        imageView.getLayoutParams().width = imageWidth;
        textView.setTextSize(textSize);
        textView.getLayoutParams().height = textHeight;

        if (drawable != null)
            imageView.setImageDrawable(drawable);
        else
            drawable = imageView.getDrawable();

        textView.setText(name.shortened());

        view.setOnTouchListener(getOnTouchListener());
        view.setOnDragListener(getOnDragListener());
    }

    // Method that can be overridden to return a usable
    // View component for the Item
    public View toView(Context context) {
        return null;
    }

    // Method that can be overridden to perform custom tap
    // functionality
    protected void onTap() {}

    protected boolean isEmpty = false, isHomeScreen = false, isMovable = true;
    protected int textID;
    protected int imageHeight, imageWidth, textSize, textHeight, iconIndex;
    protected Context context;
    protected String identifier;
    protected Name name;
    protected Drawable drawable;
    protected boolean isWidget = false;

    private ImageView imageView;
    private TextView textView;
    private boolean stationary;
    private int gridIndex = -1;
    private Runnable hold = null;
    private final int LONG_PRESS_DELAY = 400;
}

package com.example.echolauncher;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;

public class PinItem {
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

    public class LongPressRunnable implements Runnable {
        public LongPressRunnable(View view, PinItem item) {
            VIEW = view;
            ITEM = item;
        }

        @Override
        public void run() {
            // Vibrate phone
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(55);

            ClipData data = ClipData.newPlainText(identifier,
                    identifier);
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(VIEW);
            VIEW.startDrag(data, shadowBuilder, VIEW, 0);
            Scroll.scrollTo(Scroll.HOME_SCREEN);
            stationary = false;
            Library.setDragging(ITEM.identifier);
        }

        private final View VIEW;
        private final PinItem ITEM;
    }

    public Name getName() { return name; }
    public String getIdentifier() { return identifier; }
    public Drawable getDrawable() { return drawable; }
    public int getGridIndex() { return gridIndex; }

    public void setGridIndex(int gridIndex) {
        this.gridIndex = gridIndex;
    }

    public View.OnTouchListener getOnTouchListener() {
        PinItem temp = this;

        Handler handler = new Handler();

        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (isMovable) {
                            hold = new LongPressRunnable(view, temp);
                            handler.postDelayed(hold, LONG_PRESS_DELAY);
                        }
                        break;
                    case MotionEvent.ACTION_SCROLL:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        handler.removeCallbacks(hold);

                        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                            onTap();
                        }
                        break;
                }

                return true;
            }
        };
    }

    public View.OnDragListener getOnDragListener() {
        // Handle dragging events
        return new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                if (stationary)
                    return false;

                if (!isMovable)
                    return false;

                if (dragEvent.getAction() == DragEvent.ACTION_DRAG_ENDED) {
                    Scroll.scrollBack();
                    stationary = true;
                }

                return true;
            }
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
        // Handle dragging events
        view.setOnDragListener(getOnDragListener());
    }

    protected void onTap() {}

    protected boolean isEmpty = false, isHomeScreen = false, isMovable = true;
    protected int textID;
    protected int imageHeight, imageWidth, textSize, textHeight, iconIndex;
    protected Context context;
    // Uniquely identifies this item
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

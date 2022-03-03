package com.example.echolauncher;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
            int maxChars = 6;
            String shortenedString;

            if (name.length() >= maxChars) {
                shortenedString = name.substring(0, maxChars);
                shortenedString += "...";
                return shortenedString;
            }

            return name;
        }

        private String name;
    }

    public Name getName() { return name; }
    public String getIdentifier() { return identifier; }
    public Drawable getDrawable() { return drawable; }
    public int getGridIndex() { return gridIndex; }

    public void setGridIndex(int gridIndex) {
        this.gridIndex = gridIndex;
    }

    public View.OnClickListener getOnClickListener() {
        // User has done one short tap. Open the app that they have tapped on
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageManager packageManager = Library.getPackageManager();
                Log.d("AppAdapter", "Opening " + identifier + "...");
                Intent intent = packageManager.getLaunchIntentForPackage(identifier);

                if (intent == null) {
                    Log.d("AppAdapter", "Intent was null");
                    return;
                }

                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                // Open the app with a custom animation
                activity = (Activity) context;
                activity.startActivity(intent);
                activity.finish();
                activity.overridePendingTransition(R.transition.open_app, 0);
            }
        };
    }

    public View.OnTouchListener getOnTouchListener() {
        PinItem temp = this;

        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // User is moving the app. Start dragging the app that they have tapped on
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    ClipData data = ClipData.newPlainText(identifier,
                            identifier);
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    view.startDrag(data, shadowBuilder, view, 0);
                    Scroll.scrollTo(Scroll.HOME_SCREEN);
                    stationary = false;
                    Library.setDragging(temp.identifier);

                    return true;
                }

                return false;
            }
        };
    }

    public View.OnDragListener getOnDragListener() {
        // Handle dragging events
        return new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                if (stationary)
                    return true;

                if (dragEvent.getAction() == DragEvent.ACTION_DRAG_ENDED) {
                    Scroll.scrollBack();
                    stationary = true;
                }

                return true;
            }
        };
    }

    protected void initView(View view) {
        image = view.findViewById(R.id.appIcon);
        textView = view.findViewById(R.id.textView);

        image.getLayoutParams().height = imageHeight;
        image.getLayoutParams().width = imageWidth;
        textView.setTextSize(textSize);
        textView.getLayoutParams().height = textHeight;

        if (drawable != null)
            image.setImageDrawable(drawable);
        textView.setText(name.shortened());

        view.setOnClickListener(getOnClickListener());

        if (moveable) {
            view.setOnTouchListener(getOnTouchListener());

            // Handle dragging events
            view.setOnDragListener(getOnDragListener());
        }
    }

    public boolean empty = false, isHomeScreen = false, moveable = true;

    private Activity activity;
    private ImageView image;
    private TextView textView;
    private boolean stationary;
    private int gridIndex = -1;

    protected int textID;
    protected int imageHeight, imageWidth, textSize, textHeight, iconIndex;
    protected Context context;
    // Uniquely identifies this item
    protected String identifier;
    protected Name name;
    protected Drawable drawable;
    protected boolean isWidget = false;
}

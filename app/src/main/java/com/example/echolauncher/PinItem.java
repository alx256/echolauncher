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

import java.util.ArrayList;

public class PinItem {
    public int getHeight() {
        return imageHeight + textHeight;
    }

    public View toView(View finalView) {
        image = finalView.findViewById(R.id.appIcon);
        textView = finalView.findViewById(R.id.textView);

        if (iconIndex >= 0)
            drawable = InstalledAppsManager.availableIcons.get(iconIndex);

        image.getLayoutParams().height = imageHeight;
        image.getLayoutParams().width = imageWidth;
        textView.setTextSize(textSize);
        textView.getLayoutParams().height = textHeight;

        if (!empty)
            setItem(this, finalView);
        else
            setEmpty(finalView);

        return finalView;
    }

    public String getName() { return name; }
    public String getIdentifier() { return identifier; }
    public Drawable getDrawable() { return drawable; }

    protected void setTemp(PinItem pinItem, View view) {
        image = view.findViewById(R.id.appIcon);
        textView = view.findViewById(R.id.textView);
        image.setImageDrawable(pinItem.drawable);
        textView.setText(shortened(pinItem.name));

        name = pinItem.getName();
        identifier = pinItem.getIdentifier();
    }

    protected void clear(View view) {
        image = view.findViewById(R.id.appIcon);
        textView = view.findViewById(R.id.textView);
        image.setImageDrawable(null);
        textView.setText("");
    }

    protected void setItem(PinItem pinItem, View view) {
        setTemp(pinItem, view);

        // User has done one short tap. Open the app that they have tapped on
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageManager packageManager = InstalledAppsManager.getPackageManager();
                Log.d("AppAdapter", "Opening " + pinItem.getIdentifier() + "...");
                Intent intent = packageManager.getLaunchIntentForPackage(pinItem.identifier);

                if (intent == null) {
                    Log.d("AppAdapter", "Intent was null");
                    return;
                }

                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                activity = (Activity) context;
                activity.startActivity(intent);
                //activity.finish();
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        PinItem instance = this;

        if (moveable) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    // User is moving the app. Start dragging the app that they have tapped on
                    if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                        ClipData data = ClipData.newPlainText(identifier,
                                identifier);
                        InstalledAppsManager.shadowBuilder = new AppShadowBuilder(view);
                        view.startDrag(data, InstalledAppsManager.shadowBuilder, view, 0);
                        ScrollManager.scrollTo(ScrollManager.HOME_SCREEN);
                        stationary = false;
                        InstalledAppsManager.dragging = instance;

                        if (isHomeScreen)
                            setEmpty(view);

                        return true;
                    }

                    return false;
                }
            });

            // Handle dragging events
            view.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View view, DragEvent dragEvent) {
                    if (stationary)
                        return true;

                    if (dragEvent.getAction() == DragEvent.ACTION_DRAG_ENDED) {
                        ScrollManager.scrollBack();
                        stationary = true;
                    }

                    return true;
                }
            });
        }
    }

    protected void setEmpty(View view) {
        clear(view);

        // Do nothing when touched
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        view.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                PinItem item = InstalledAppsManager.dragging;

                if (dragEvent.getAction() == DragEvent.ACTION_DRAG_ENTERED)
                    setTemp(item, view);
                else if (dragEvent.getAction() == DragEvent.ACTION_DRAG_EXITED)
                    clear(view);

                if (dragEvent.getAction() == DragEvent.ACTION_DROP)
                    setItem(item, view);

                return true;
            }
        });
    }

    private String shortened(String name) {
        // Maximum number of characters before shortening the text
        int maxChars = (isWidget) ? 25 : 6;
        String shortenedString;

        if (name.length() >= maxChars) {
            shortenedString = name.substring(0, maxChars);
            shortenedString += "...";
            return shortenedString;
        }

        return name;
    }

    public boolean empty, isHomeScreen = false, moveable = true;

    private Activity activity;
    private ImageView image;
    private TextView textView;
    private boolean stationary;

    protected int imageHeight, imageWidth, textSize, textHeight, iconIndex;
    protected Context context;
    // Uniquely identifies this item
    protected String identifier, name;
    protected Drawable drawable;
    protected boolean isWidget = false;
}

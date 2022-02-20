package com.example.echolauncher;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Pair;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import okhttp3.CertificatePinner;

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
                PackageManager packageManager = InstalledAppsManager.getPackageManager();
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
        return new View.OnTouchListener() {
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
                    InstalledAppsManager.dragging = identifier;

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
                    ScrollManager.scrollBack();
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

        if (iconIndex >= 0)
            drawable = InstalledAppsManager.availableIcons.get(iconIndex);
        else
            drawable = image.getDrawable();

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
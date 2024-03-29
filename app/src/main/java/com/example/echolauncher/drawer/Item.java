package com.example.echolauncher.drawer;

import static com.example.echolauncher.home_screen.MainScrollView.Focus.HOME_SCREEN;

import android.content.ClipData;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.example.echolauncher.R;
import com.example.echolauncher.apps.Library;
import com.example.echolauncher.home_screen.HomeDestination;

/**
 * The item class
 * Represents generic item that can
 * be pinned to the home screen
 */

public class Item implements Cloneable {
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
            Vibrator vibrator = (Vibrator) VIEW.getContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(55);

            // Start dragging the app
            ClipData data = ClipData.newPlainText(identifier,
                    identifier);
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(VIEW);
            VIEW.startDrag(data, shadowBuilder, VIEW, 0);
            HomeDestination.getScrollView()
                    .scrollTo(HOME_SCREEN);
            stationary = false;
            Library.setDragging(ITEM);

            // Custom move actions
            onMove();
        }

        private final View VIEW;
        private final Item ITEM;
    }

    public Name getName() {
        return name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getGridIndex() {
        return gridIndex;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    // The Grid Index is the index of
    // the item in the home screen grid
    public void setGridIndex(int gridIndex) {
        this.gridIndex = gridIndex;
    }

    // The page number is the index for the
    // page holding the item
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
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
                //MainScrollView.scrollBack();
                HomeDestination.getScrollView()
                        .scrollBack();
                stationary = true;
            }

            return true;
        };
    }

    @NonNull
    public Item clone() {
        Item c = null;

        try {
            c = (Item) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        assert c != null;
        return c;
    }

    public void setMovable(boolean isMovable) {
        this.isMovable = isMovable;
    }

    public void setDuplicable(boolean isDuplicable) {
        this.isDuplicable = isDuplicable;
    }

    // Return the item's context
    public Context getContext() {
        return context;
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

    protected View getGenericView() {
        if (genericView == null) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            genericView = inflater.inflate(R.layout.item, null, false);
            ImageView imageView = genericView.findViewById(R.id.appIcon);
            TextView textView = genericView.findViewById(R.id.textView);

            imageView.getLayoutParams().height = imageHeight;
            imageView.getLayoutParams().width = imageWidth;
            textView.setTextSize(textSize);
            textView.getLayoutParams().height = textHeight;
        }

        return genericView;
    }

    protected void measureGenericView() {
        genericView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
    }

    // Method that can be overridden to return a usable
    // View component for the Item
    public View toView(Context context) {
        return null;
    }

    // Return the constant width
    public static int getWidth() {
        return WIDTH;
    }

    // Return the constant height
    public static int getHeight() {
        return HEIGHT;
    }

    // Method that can be overridden to perform custom tap
    // functionality
    protected void onTap() {}

    // Method that can be overridden to perform custom functionality
    // when the item is first moved
    protected void onMove() {}

    protected boolean isEmpty = false, isDuplicable = true, isMovable = true;
    protected int textID;
    protected int imageHeight, imageWidth, textSize, textHeight, iconIndex;
    protected Context context;
    protected String identifier;
    protected Name name;
    protected Drawable drawable;
    protected boolean isWidget = false;

    private static View genericView;
    private ImageView imageView;
    private TextView textView;
    private boolean stationary;
    private int gridIndex = -1, pageNumber = -1;
    private Runnable hold = null;
    private final int LONG_PRESS_DELAY = 400;
    private final static int WIDTH = 448, HEIGHT = 256;
}

package com.example.echolauncher;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class AppDrawerGridView extends GridView {
    public AppDrawerGridView(Context context) {
        super(context);
    }

    public AppDrawerGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AppDrawerGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Allow the view to be as long as it needs initially
        int newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, newHeightMeasureSpec);

        if (getMeasuredHeight() < Globals.metrics.heightPixels) {
            // The height of the GridView is less than the height
            // of the screen, so make the GridView fill the whole
            // screen
            newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(Globals.metrics.heightPixels,
                    MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, newHeightMeasureSpec);
        }
    }
}

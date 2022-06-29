package com.example.echolauncher.app_drawer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

import com.example.echolauncher.utilities.Globals;

public class AppDrawerGridView extends GridView {
    public AppDrawerGridView(Context context) {
        super(context);
        retainedHeightMeasureSpec = -1;
    }

    public AppDrawerGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        retainedHeightMeasureSpec = -1;
    }

    public AppDrawerGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        retainedHeightMeasureSpec = -1;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (retainedHeightMeasureSpec != -1) {
            super.onMeasure(widthMeasureSpec, retainedHeightMeasureSpec);
            return;
        }

        // Allow the view to be as long as it needs initially
        retainedHeightMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, retainedHeightMeasureSpec);

        if (getMeasuredHeight() < Globals.metrics.heightPixels) {
            // The height of the GridView is less than the height
            // of the screen, so make the GridView fill the whole
            // screen
            retainedHeightMeasureSpec = MeasureSpec.makeMeasureSpec(Globals.metrics.heightPixels,
                    MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, retainedHeightMeasureSpec);
        }
    }

    private int retainedHeightMeasureSpec;
}

package com.example.echolauncher;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

public class AppShadowBuilder extends View.DragShadowBuilder {
    public AppShadowBuilder(View view) {
        super(view);
    }

    public void update() {
        onDrawShadow(canvas);
        Log.d("", Boolean.toString(InstalledAppsManager.hidden));
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        this.canvas = canvas;
        Log.d("", Boolean.toString(InstalledAppsManager.hidden));

        if (!InstalledAppsManager.hidden)
            super.onDrawShadow(this.canvas);
    }

    private Canvas canvas;
}

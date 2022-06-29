package com.example.echolauncher.home_screen;

import android.app.Activity;

import com.example.echolauncher.R;

/**
 * This class initialises the home screen when it is
 * opened by the Dispatcher class
 */

public class HomeDestination {
    public static void setup(Activity activity) {
        scrollView = activity.findViewById(R.id.mainScrollView);
        scrollView.adjust();
    }

    public static MainScrollView getScrollView() {
        return scrollView;
    }

    private static MainScrollView scrollView;
}

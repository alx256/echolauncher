package com.example.echolauncher.utilities;

import android.util.DisplayMetrics;

/**
 * This class contains global information about
 * the phone, such as its screen width and height.
 * These values are either constants or are set
 * when the app is first opened in the Dispatcher
 * class
 * **/

public class Globals {
    public static DisplayMetrics metrics;
    public static int statusBarHeight, navigationBarHeight,
            layoutWidthApps, layoutWidthWidgets;
    public static final int APP_ICON_HEIGHT = 150,
            APP_ICON_WIDTH = 150,
            APP_TEXT_SIZE = 12,
            APP_TEXT_HEIGHT = 55,
            APP_HEIGHT = APP_ICON_HEIGHT + APP_TEXT_HEIGHT,
            NUM_APPS_PER_ROW = 4,
            NUM_WIDGETS_PER_ROW = 2;
}

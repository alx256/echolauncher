package com.example.echolauncher;

import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

import androidx.fragment.app.FragmentContainerView;

public class ScrollManager {
    public static void Init(View view) {
        masterScrollView = view.findViewById(R.id.masterScrollView);
        homeScreen = view.findViewById(R.id.homeScreenFragment);
        appDrawer = view.findViewById(R.id.appDrawerFragment);

        x = 0;
        y = 0;
        lastX = 0;
        lastY = 0;
    }

    public static void scrollTo(int destination) {
        switch (destination) {
            case WIDGET_DRAWER:
                // Not implemented yet
                break;
            case HOME_SCREEN:
                x = (int) homeScreen.getX();
                y = (int) homeScreen.getY();
                break;
            case APP_DRAWER:
                x = (int) appDrawer.getX();
                y = (int) appDrawer.getY();
                break;
        }

        doScroll(x, y);
    }

    public static void scrollBack() {
        doScroll(lastX, lastY);
    }

    private static void doScroll(int x, int y) {
        lastX = masterScrollView.getScrollX();
        lastY = masterScrollView.getScrollY();

        masterScrollView.post(new Runnable() {
            @Override
            public void run() {
                masterScrollView.smoothScrollTo(x, y);
            }
        });
    }

    public static final int WIDGET_DRAWER = 0,
        HOME_SCREEN = 1,
        APP_DRAWER = 2;

    private static ScrollView masterScrollView;
    private static FragmentContainerView homeScreen, appDrawer;
    private static int x, y, lastX, lastY;
}

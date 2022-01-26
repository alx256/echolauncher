package com.example.echolauncher;

import android.graphics.Rect;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.ScrollView;

import androidx.fragment.app.FragmentContainerView;
import androidx.viewpager2.widget.ViewPager2;

public class ScrollManager {
    public static void Init(View view) {
        masterScrollView = view.findViewById(R.id.masterScrollView);
        widgetDrawer = view.findViewById(R.id.widgetDrawerFragment);
        homeScreen = view.findViewById(R.id.homeScreenFragment);
        appDrawer = view.findViewById(R.id.appDrawerFragment);

        x = 0;
        y = 0;
        lastX = 0;
        lastY = 0;

        masterScrollView.post(new Runnable() {
            @Override
            public void run() {
                do {
                    masterScrollView.scrollTo((int)homeScreen.getX(), (int)homeScreen.getY());
                } while (homeScreen.getY() == 0.0f);
            }
        });
    }

    public static void scrollTo(int destination) {
        switch (destination) {
            case WIDGET_DRAWER:
                x = (int) widgetDrawer.getX();
                y = (int) widgetDrawer.getY();
                break;
            case HOME_SCREEN:
                x = (int) homeScreen.getX();
                y = (int) homeScreen.getY();
                break;
            case APP_DRAWER:
                x = (int) appDrawer.getX();
                y = (int) appDrawer.getY() - Globals.statusBarHeight;
                break;
        }

        doScroll(x, y);
    }

    public static void scrollBack() {
        doScroll(lastX, lastY);
    }

    private static void doScroll(int x, int y) {
        if (!canScroll)
            return;

        canScroll = false;

        masterScrollView.post(new Runnable() {
            @Override
            public void run() {
                lastX = masterScrollView.getScrollX();
                lastY = masterScrollView.getScrollY();
                masterScrollView.smoothScrollTo(x, y);
                canScroll = true;
            }
        });
    }

    public static final int WIDGET_DRAWER = 0,
        HOME_SCREEN = 1,
        APP_DRAWER = 2;

    public static boolean canScroll = true;

    private static ScrollView masterScrollView;
    private static FragmentContainerView widgetDrawer, homeScreen, appDrawer;
    private static int x, y, lastX, lastY;
}

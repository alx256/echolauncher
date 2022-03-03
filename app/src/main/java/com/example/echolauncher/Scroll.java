package com.example.echolauncher;

import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import androidx.fragment.app.FragmentContainerView;

public class Scroll {
    public static void init(View view) {
        master = view.findViewById(R.id.masterScrollView);
        appDrawer = view.findViewById(R.id.appDrawerFragment);
        widgetDrawer = view.findViewById(R.id.widgetDrawerFragment);
        homeScreen = view.findViewById(R.id.homeScreenFragment);

        x = 0;
        y = 0;
        lastX = 0;
        lastY = 0;

        master.setVerticalScrollBarEnabled(false);

        ViewTreeObserver observer = master.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                master.scrollTo((int) homeScreen.getX(), (int) homeScreen.getY());
                ViewTreeObserver temp = master.getViewTreeObserver();
                temp.removeOnPreDrawListener(this);
                return true;
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

        master.post(() -> {
            lastX = master.getScrollX();
            lastY = master.getScrollY();
            master.smoothScrollTo(x, y);
            canScroll = true;
        });
    }

    public static final int WIDGET_DRAWER = 0,
        HOME_SCREEN = 1,
        APP_DRAWER = 2;

    private static ScrollView master;
    private static FragmentContainerView appDrawer, widgetDrawer, homeScreen;
    private static int x, y, lastX, lastY;
    private static boolean canScroll = true;
}

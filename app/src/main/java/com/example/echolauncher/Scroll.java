package com.example.echolauncher;

import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import androidx.fragment.app.FragmentContainerView;
import androidx.viewpager2.widget.ViewPager2;

/**
 * Handles scrolling, used to snap the app and widget
 * drawers into place
 */

public class Scroll {
    public static void init(View view) {
        mainScrollView = view.findViewById(R.id.mainScrollView);
        appDrawer = view.findViewById(R.id.appDrawerFragment);
        widgetDrawer = view.findViewById(R.id.widgetDrawerFragment);
        homeScreen = view.findViewById(R.id.homeScreenFragment);

        x = 0;
        y = 0;
        lastX = 0;
        lastY = 0;
        focus = HOME_SCREEN;

        // Disable unnecessary scrolling effects
        mainScrollView.setVerticalScrollBarEnabled(false);
        mainScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        // By default the scrollView will be on the widget drawer.
        // Scroll to the home screen whenever scrolling becomes
        // available
        ViewTreeObserver observer = mainScrollView.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mainScrollView.scrollTo((int) homeScreen.getX(), (int) homeScreen.getY());
                isUserScroll = true;

                ViewTreeObserver temp = mainScrollView.getViewTreeObserver();
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
                focus = WIDGET_DRAWER;
                break;
            case HOME_SCREEN:
                x = (int) homeScreen.getX();
                y = (int) homeScreen.getY();
                focus = HOME_SCREEN;
                break;
            case APP_DRAWER:
                x = (int) appDrawer.getX();
                y = (int) appDrawer.getY() - Globals.statusBarHeight;
                focus = APP_DRAWER;
                break;
        }

        doScroll(x, y);
    }

    public static void scrollBack() {
        // Scroll back to the last position, used
        // for when an item is dropped onto the
        // home screen to return to the app
        // or the widget drawer
        doScroll(lastX, lastY);
    }

    public static void setPagerAlphaAnimation(ViewPager2 pager) {
        // This animation will only work if the app is being run
        // on an android version newer than Marshmallow (Android 6.0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mainScrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                float alpha = pager.getAlpha(),
                        difference = scrollY - oldScrollY,
                        adjust = Math.abs(difference * 0.001f);

                if (!isUserScroll)
                    return;

                // Get direction of the scroll
                direction = (short) ((scrollY > (mainScrollView.getBottom()) ? -1 : 1));

                switch (focus) {
                    case WIDGET_DRAWER:
                        // Fade items in
                        if (difference > 0.0f && alpha < 1.0f)
                            pager.setAlpha(alpha + adjust);
                        break;
                    case HOME_SCREEN:
                        if (direction == -1) {
                            if (difference > 0.0f && alpha > 0.0f)
                                // Fade items out
                                pager.setAlpha(alpha - adjust);
                            if (difference < 0.0f && alpha < 1.0f)
                                // Fade items in
                                pager.setAlpha(alpha + adjust);
                        } else {
                            if (difference > 0.0f && alpha < 1.0f)
                                // Fade items in
                                pager.setAlpha(alpha + adjust);
                            if (difference < 0.0f && alpha > 0.0f)
                                // Fade items out
                                pager.setAlpha(alpha - adjust);
                        }

                        break;
                    case APP_DRAWER:
                        // Fade items in
                        if (difference < 0.0f && alpha < 1.0f)
                            pager.setAlpha(pager.getAlpha() + adjust);
                        break;
                }
            });
        }
    }

    public static void snap() {
        switch (focus) {
            case WIDGET_DRAWER:
                scrollTo(WIDGET_DRAWER);
                break;
            case HOME_SCREEN:
                scrollTo(HOME_SCREEN);
                break;
            case APP_DRAWER:
                scrollTo(APP_DRAWER);
                break;
        }
    }

    public static int getFocus() {
        return focus;
    }

    private static void doScroll(int x, int y) {
        if (!canScroll)
            return;

        canScroll = false;

        // When possible, scroll the ScrollView
        mainScrollView.post(() -> {
            lastX = mainScrollView.getScrollX();
            lastY = mainScrollView.getScrollY();
            mainScrollView.smoothScrollTo(x, y);
            canScroll = true;
        });
    }

    public static final int WIDGET_DRAWER = 0,
        HOME_SCREEN = 1,
        APP_DRAWER = 2;

    private static ScrollView mainScrollView;
    private static FragmentContainerView appDrawer, widgetDrawer, homeScreen;
    private static int x, y, lastX, lastY, focus;
    private static short direction;
    private static boolean canScroll = true, isUserScroll = false;
}

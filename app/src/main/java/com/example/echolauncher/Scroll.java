package com.example.echolauncher;

import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import androidx.fragment.app.FragmentContainerView;
import androidx.viewpager2.widget.ViewPager2;

public class Scroll {
    public static void init(View view) {
        master = view.findViewById(R.id.mainScrollView);
        appDrawer = view.findViewById(R.id.appDrawerFragment);
        widgetDrawer = view.findViewById(R.id.widgetDrawerFragment);
        homeScreen = view.findViewById(R.id.homeScreenFragment);

        x = 0;
        y = 0;
        lastX = 0;
        lastY = 0;
        focus = HOME_SCREEN;

        master.setVerticalScrollBarEnabled(false);
        master.setOverScrollMode(View.OVER_SCROLL_NEVER);

        ViewTreeObserver observer = master.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                master.scrollTo((int) homeScreen.getX(), (int) homeScreen.getY());
                isUserScroll = true;
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
        doScroll(lastX, lastY);
    }

    public static void setPagerAlphaAnimation(ViewPager2 pager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            master.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    float alpha = pager.getAlpha(),
                            difference = scrollY - oldScrollY,
                            adjust = Math.abs(difference * 0.001f);

                    if (!isUserScroll)
                        return;

                    direction = (short) ((scrollY > (master.getBottom()) ? -1 : 1));

                    switch (focus) {
                        case WIDGET_DRAWER:
                            if (difference > 0.0f && alpha < 1.0f)
                                pager.setAlpha(alpha + adjust);
                            break;
                        case HOME_SCREEN:
                            if (direction == -1) {
                                if (difference > 0.0f && alpha > 0.0f)
                                    pager.setAlpha(alpha - adjust);
                                if (difference < 0.0f && alpha < 1.0f)
                                    pager.setAlpha(alpha + adjust);
                            } else {
                                if (difference > 0.0f && alpha < 1.0f)
                                    pager.setAlpha(alpha + adjust);
                                if (difference < 0.0f && alpha > 0.0f)
                                    pager.setAlpha(alpha - adjust);
                            }

                            break;
                        case APP_DRAWER:
                            if (difference < 0.0f && alpha < 1.0f)
                                pager.setAlpha(pager.getAlpha() + adjust);
                            break;
                    }
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
    private static int x, y, lastX, lastY, focus;
    private static short direction;
    private static boolean canScroll = true, isUserScroll = false;
}

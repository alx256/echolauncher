package com.example.echolauncher;

import androidx.fragment.app.FragmentContainerView;

import android.app.Activity;
import android.content.res.Resources;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class HomeDestination {

    /*
    * This class is the entry point of the app, containing the home screen pages,
    * the app and widget drawers and the study mode screen. It is primarily used
    * to initialise the app
    * */

    public static void setup(Activity activity) {
        // Set fragments to sizes which means that they can be scrolled by ScrollView
        FragmentContainerView widgetDrawer = activity.findViewById(R.id.widgetDrawerFragment),
            homeScreen = activity.findViewById(R.id.homeScreenFragment),
            appDrawer = activity.findViewById(R.id.appDrawerFragment);
        widgetDrawer.getLayoutParams().height = Globals.metrics.heightPixels;
        homeScreen.getLayoutParams().height = Globals.metrics.heightPixels;
        appDrawer.getLayoutParams().height = Globals.metrics.heightPixels;

        // 0 = Widgets, 1 = Home Screen, 2 = App Drawer
        focus = 1;

        // Add snapping behaviour to scroll view
        ScrollView scrollView = activity.findViewById(R.id.mainScrollView);
        Scroll.init(scrollView);

        // Get status bar height
        Resources resources = activity.getResources();
        int id = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (id > 0)
            Globals.statusBarHeight = resources.getDimensionPixelSize(id);
        else
            Globals.statusBarHeight = 0;

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int[] appDrawerPosition = new int[2],
                        homeScreenPosition = new int[2];
                appDrawer.getLocationOnScreen(appDrawerPosition);
                homeScreen.getLocationOnScreen(homeScreenPosition);

                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    // Don't allow snapping if the app drawer or widgets drawer has been reached
                    if (focus == 2 && appDrawerPosition[1] < 0)
                        return false;

                    int yPosHomeScreen = homeScreenPosition[1];
                    int yPosAppDrawer = appDrawerPosition[1];

                    // Lower yPos means that it is closer to the top of the screen
                    double mid = Globals.metrics.heightPixels / 2,
                            offset = Globals.metrics.heightPixels / 2.5,
                            lower = mid + offset,
                            higher = mid - offset;

                    switch (focus) {
                        case 0:
                            if (yPosHomeScreen < lower) {
                                Scroll.scrollTo(Scroll.HOME_SCREEN);
                                focus = 1;
                                return true;
                            }
                            break;
                        case 1:
                            if (yPosHomeScreen > higher) {
                                Scroll.scrollTo(Scroll.WIDGET_DRAWER);
                                focus = 0;
                                return true;
                            }

                            if (yPosAppDrawer < lower) {
                                Scroll.scrollTo(Scroll.APP_DRAWER);
                                focus = 2;
                                return true;
                            }
                            break;
                        case 2:
                            if (yPosAppDrawer > higher) {
                                Scroll.scrollTo(Scroll.HOME_SCREEN);
                                focus = 1;
                                return true;
                            }
                            break;
                    }

                    if (focus == 0) Scroll.scrollTo(Scroll.WIDGET_DRAWER);
                    if (focus == 1) Scroll.scrollTo(Scroll.HOME_SCREEN);
                    if (focus == 2) Scroll.scrollTo(Scroll.APP_DRAWER);

                    return true;
                }

                return false;
            }
        });
    }

    private static int focus;
}

package com.example.echolauncher.home_screen;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import androidx.fragment.app.FragmentContainerView;

import com.example.echolauncher.R;
import com.example.echolauncher.utilities.Globals;

public class MainScrollView extends ScrollView {
    public enum Focus {
        WIDGET_DRAWER,
        HOME_SCREEN,
        APP_DRAWER
    }

    public MainScrollView(Context context) {
        super(context);
        origin = -1;
    }

    public MainScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        origin = -1;
    }

    public MainScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        origin = -1;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        appDrawer.getLocationOnScreen(appDrawerPosition);
        homeScreen.getLocationOnScreen(homeScreenPosition);

        if (event.getAction() == MotionEvent.ACTION_UP) {
            // Don't allow snapping if the app drawer has been reached
            if (focus == Focus.APP_DRAWER && appDrawerPosition[1] < 0)
                return false;

            int yPosHomeScreen = homeScreenPosition[1];
            int yPosAppDrawer = appDrawerPosition[1];

            // Lower yPos means that it is closer to the top of the screen
            double mid = Globals.metrics.heightPixels / 2,
                    offset = Globals.metrics.heightPixels / 2.5,
                    lower = mid + offset,
                    higher = mid - offset;

            switch (focus) {
                case WIDGET_DRAWER:
                    if (yPosHomeScreen < lower) {
                        scrollTo(Focus.HOME_SCREEN);
                        return true;
                    }
                    break;
                case HOME_SCREEN:
                    if (yPosHomeScreen > higher) {
                        scrollTo(Focus.WIDGET_DRAWER);
                        return true;
                    }

                    if (yPosAppDrawer < lower) {
                        scrollTo(Focus.APP_DRAWER);
                        return true;
                    }
                    break;
                case APP_DRAWER:
                    if (yPosAppDrawer > higher) {
                        scrollTo(Focus.HOME_SCREEN);
                        return true;
                    }
                    break;
            }

            snap();
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN)
            performClick();

        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    public void adjust() {
        // Set fragments to sizes which means that they can be scrolled by ScrollView
        widgetDrawer = findViewById(R.id.widgetDrawerFragment);
        homeScreen = findViewById(R.id.homeScreenFragment);
        appDrawer = findViewById(R.id.appDrawerFragment);

        widgetDrawer.getLayoutParams().height = Globals.metrics.heightPixels;
        homeScreen.getLayoutParams().height = Globals.metrics.heightPixels;

        appDrawerPosition = new int[2];
        homeScreenPosition = new int[2];

        // Disable unnecessary scrolling effects
        setVerticalScrollBarEnabled(false);
        setOverScrollMode(View.OVER_SCROLL_NEVER);

        x = 0;
        y = 0;
        lastX = 0;
        lastY = 0;
        focus = Focus.HOME_SCREEN;

        // Scroll to home screen initially
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                scrollTo((int) homeScreen.getX(), (int) homeScreen.getY());
                origin = getScrollY();

                getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
    }

    public void scrollTo(Focus destination) {
        switch (destination) {
            case WIDGET_DRAWER:
                x = (int) widgetDrawer.getX();
                y = (int) widgetDrawer.getY();
                focus = Focus.WIDGET_DRAWER;
                break;
            case HOME_SCREEN:
                x = (int) homeScreen.getX();
                y = (int) homeScreen.getY();
                focus = Focus.HOME_SCREEN;
                break;
            case APP_DRAWER:
                x = (int) appDrawer.getX();
                y = (int) appDrawer.getY() - Globals.statusBarHeight;
                focus = Focus.APP_DRAWER;
                break;
        }

        doScroll(x, y);
    }

    public void scrollBack() {
        // Scroll back to the last position, used
        // for when an item is dropped onto the
        // home screen to return to the app
        // or the widget drawer
        doScroll(lastX, lastY);
    }

    public void snap() {
        switch (focus) {
            case WIDGET_DRAWER:
                scrollTo(Focus.WIDGET_DRAWER);
                break;
            case HOME_SCREEN:
                scrollTo(Focus.HOME_SCREEN);
                break;
            case APP_DRAWER:
                scrollTo(Focus.APP_DRAWER);
                break;
        }
    }

    @Override
    protected void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        // When the view is scrolled, gradually fade the homeScreen away, until
        // it is completely invisible
        // An absolute value is calculated by subtracting the current scrollY from
        // the origin point which is the view's scrollY when the home screen is being
        // focused upon and this is multiplied by 0.001f to make it a smaller value
        // This is subtracted from 1.0f as 1.0f means that there is no alpha effect
        // and 0.4f is added so that the home screen does not fade away as soon as
        // the user begins to scroll, instead there is a slight delay
        if (origin != -1)
            homeScreen.setAlpha((1.0f - (Math.abs(origin - scrollY) * 0.001f)) + 0.4f);

        super.onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY);
    }

    private void doScroll(int x, int y) {
        if (!canScroll)
            return;

        canScroll = false;

        // When possible, scroll the ScrollView
        post(() -> {
            lastX = getScrollX();
            lastY = getScrollY();
            smoothScrollTo(x, y);
            canScroll = true;
        });
    }

    private FragmentContainerView widgetDrawer, homeScreen, appDrawer;
    private int[] appDrawerPosition, homeScreenPosition;
    private int x, y, lastX, lastY, origin;
    private Focus focus;
    private boolean canScroll = true;
}

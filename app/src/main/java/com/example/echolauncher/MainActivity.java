package com.example.echolauncher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentContainerView;

import android.Manifest;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;

public class MainActivity extends AppCompatActivity {

    /*
    * This class is the entry point of the app, containing the home screen pages,
    * the app and widget drawers and the study mode screen. It is primarily used
    * to initialise the app
    * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove the app's title from always being shown
        getSupportActionBar().hide();

        final Window WINDOW = getWindow();
        // Make app fullscreen
        WINDOW.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        // Use entire screen area
        WINDOW.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        // Hide status bar
        WINDOW.setStatusBarColor(Color.TRANSPARENT);

        // Set the layout to the layout stored in activity_main.xml
        setContentView(R.layout.activity_main);

        Library.init(getApplicationContext());
        HomeScreenStorage.init(getApplicationContext());

        // Request READ_EXTERNAL_STORAGE permission (needed for accessing wallpaper)
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        // Get screen size
        Globals.metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(Globals.metrics);

        // Set fragments to sizes which means that they can be scrolled by ScrollView
        FragmentContainerView widgetDrawer = findViewById(R.id.widgetDrawerFragment),
            homeScreen = findViewById(R.id.homeScreenFragment),
            appDrawer = findViewById(R.id.appDrawerFragment);
        widgetDrawer.getLayoutParams().height = Globals.metrics.heightPixels;
        homeScreen.getLayoutParams().height = Globals.metrics.heightPixels;
        appDrawer.getLayoutParams().height = Globals.metrics.heightPixels;

        // 0 = Widgets, 1 = Home Screen, 2 = App Drawer
        focus = 1;

        // Add snapping behaviour to scroll view
        ScrollView scrollView = findViewById(R.id.mainScrollView);
        Scroll.init(scrollView);

        // Get status bar height
        Resources resources = getResources();
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

    // Deselect textviews when the user taps outside of them
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view instanceof EditText) {
                Rect outRect = new Rect();
                view.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    view.clearFocus();
                    InputMethodManager i = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    i.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }

        return super.dispatchTouchEvent(event);
    }

    private int focus;
}

package com.example.echolauncher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        final Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        window.setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_main);

        InstalledAppsManager.Init(getApplicationContext());

        // Request READ_EXTERNAL_STORAGE permission (needed for accessing wallpaper)
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        // Get screen size
        Globals.metricsFit = new DisplayMetrics();
        Globals.metricsFull = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(Globals.metricsFull);
        getWindowManager().getDefaultDisplay().getMetrics(Globals.metricsFit);

        // Set fragments to sizes which means that they can be scrolled by ScrollView
        FragmentContainerView widgetDrawer = findViewById(R.id.widgetDrawerFragment),
            homeScreen = findViewById(R.id.homeScreenFragment),
            appDrawer = findViewById(R.id.appDrawerFragment);

        widgetDrawer.getLayoutParams().height = Globals.metricsFull.heightPixels;
        homeScreen.getLayoutParams().height = Globals.metricsFull.heightPixels;
        appDrawer.getLayoutParams().height = Globals.metricsFull.heightPixels;

        // 0 = Widgets, 1 = Home Screen, 2 = App Drawer
        focus = 1;

        // Add snapping behaviour to scroll view
        ScrollView scrollView = findViewById(R.id.masterScrollView);
        ScrollManager.Init(scrollView);

        Resources resources = getResources();
        int id = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (id > 0)
            Globals.statusBarHeight = resources.getDimensionPixelSize(id);
        else
            Globals.statusBarHeight = 0;

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int appDrawerPosition[] = new int[2],
                        homeScreenPosition[] = new int[2],
                        offset = Globals.metricsFull.heightPixels / 4;
                appDrawer.getLocationOnScreen(appDrawerPosition);
                homeScreen.getLocationOnScreen(homeScreenPosition);

                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    // Don't allow snapping if the app drawer or widgets drawer has been reached
                    if (focus == 2 && appDrawerPosition[1] < 0)
                        return false;

                    int yPosHomeScreen = homeScreenPosition[1];
                    int yPosAppDrawer = appDrawerPosition[1];

                    // Lower yPos means that it is closer to the top of the screen
                    float mid = Globals.metricsFull.heightPixels / 2,
                            lower = mid + offset,
                            higher = mid - offset;

                    switch (focus) {
                        case 0:
                            if (yPosHomeScreen < lower) {
                                ScrollManager.scrollTo(ScrollManager.HOME_SCREEN);
                                focus = 1;
                                return true;
                            }
                            break;
                        case 1:
                            if (yPosHomeScreen > higher) {
                                ScrollManager.scrollTo(ScrollManager.WIDGET_DRAWER);
                                focus = 0;
                                return true;
                            }

                            if (yPosAppDrawer < lower) {
                                ScrollManager.scrollTo(ScrollManager.APP_DRAWER);
                                focus = 2;
                                return true;
                            }
                            break;
                        case 2:
                            if (yPosAppDrawer > higher) {
                                ScrollManager.scrollTo(ScrollManager.HOME_SCREEN);
                                focus = 1;
                                return true;
                            }
                            break;
                    }

                    if (focus == 0) ScrollManager.scrollTo(ScrollManager.WIDGET_DRAWER);
                    if (focus == 1) ScrollManager.scrollTo(ScrollManager.HOME_SCREEN);
                    if (focus == 2) ScrollManager.scrollTo(ScrollManager.APP_DRAWER);

                    return true;
                }

                return false;
            }
        });
    }

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
package com.example.echolauncher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentContainerView;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.ScrollView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        InstalledAppsManager.Init(getApplicationContext());

        // Request READ_EXTERNAL_STORAGE permission (needed for accessing wallpaper)
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        // Get screen size
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        // Set app drawer to a size that means it can be scrolled by ScrollView
        FragmentContainerView appDrawer = findViewById(R.id.appDrawerFragment);
        appDrawer.getLayoutParams().height = metrics.heightPixels;

        // Set home screen to fill screen
        FragmentContainerView homeScreen = findViewById(R.id.homeScreenFragment);
        homeScreen.getLayoutParams().height = metrics.heightPixels;

        // 0 = Widgets, 1 = Home Screen, 2 = App Drawer
        focus = 1;

        // Add snapping behaviour to scroll view
        ScrollView scrollView = findViewById(R.id.masterScrollView);
        ScrollManager.Init(scrollView);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int appDrawerPosition[] = new int[2], offset = metrics.heightPixels / 4;
                appDrawer.getLocationOnScreen(appDrawerPosition);

                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    // Don't allow scrolling if the app drawer has been reached
                    if (focus == 2 && appDrawerPosition[1] < 0)
                        return false;

                    int yPos = appDrawerPosition[1];
                    // Lower yPos means that it is closer to the top of the screen
                    float mid = metrics.heightPixels / 2,
                            lower = mid + offset,
                            higher = mid - offset;

                    switch (focus) {
                        case 1:
                            if (yPos < lower) {
                                ScrollManager.scrollTo(ScrollManager.APP_DRAWER);
                                focus = 2;
                                return true;
                            }
                            break;
                        case 2:
                            if (yPos > higher) {
                                ScrollManager.scrollTo(ScrollManager.HOME_SCREEN);
                                focus = 1;
                                return true;
                            }
                            break;
                    }

                    if (focus == 1) ScrollManager.scrollTo(ScrollManager.HOME_SCREEN);
                    if (focus == 2) ScrollManager.scrollTo(ScrollManager.APP_DRAWER);

                    return true;
                }

                return false;
            }
        });
    }

    private int focus;
}
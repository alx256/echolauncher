package com.example.echolauncher;

import android.Manifest;
import android.content.Context;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class Dispatcher extends AppCompatActivity {
    @Override
    protected void onStart() {
        super.onStart();

        android.util.Log.d("test", "test");

        // Set the layout to the layout stored in activity_main.xml
        if (StudyMode.isEnabled()) {
            setContentView(R.layout.study_mode);
            StudyModeDestination.setup(this);
        } else {
            setContentView(R.layout.activity_main);
            HomeDestination.setup(this);
        }
    }

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

        Library.init(getApplicationContext());
        HomeScreenStorage.init(getApplicationContext());

        // Request READ_EXTERNAL_STORAGE permission (needed for accessing wallpaper)
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        // Get screen size
        Globals.metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(Globals.metrics);
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

    @Override
    public void onBackPressed() {
        // Disable pressing back
        if (StudyMode.isEnabled())
            return;

        super.onBackPressed();
    }
}

package com.example.echolauncher;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.example.echolauncher.apps.Library;
import com.example.echolauncher.home_screen.HomeDestination;
import com.example.echolauncher.study_mode.StudyMode;
import com.example.echolauncher.study_mode.StudyModeDestination;
import com.example.echolauncher.utilities.Globals;
import com.example.echolauncher.utilities.Sort;

/**
 * This is the main entry point of the application.
 * It configures basic features of the app and opens
 * the home screen or study mode depending on if
 * study mode is enabled or not.
 */

public class Dispatcher extends AppCompatActivity {
    @Override
    protected void onStart() {
        super.onStart();

        // Check if study mode is enabled and open it if necessary
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

        // Initialise the app and widget library
        Library.init(getApplicationContext());

        // Make sure sorting is properly initialised
        Sort.init();

        // Get screen size
        Globals.metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(Globals.metrics);

        Globals.layoutWidthApps = Globals.metrics.widthPixels / Globals.NUM_APPS_PER_ROW;
        Globals.layoutWidthWidgets = Globals.metrics.widthPixels / Globals.NUM_WIDGETS_PER_ROW;

        // Required for opening widget dialogs
        fragmentManager = getSupportFragmentManager();

        // Get status bar height
        Resources resources = getResources();
        int id = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (id > 0)
            Globals.statusBarHeight = resources.getDimensionPixelSize(id);
        else
            Globals.statusBarHeight = 0;

        // Get navigation bar height
        id = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (id > 0)
            Globals.navigationBarHeight = resources.getDimensionPixelSize(id);
        else
            Globals.navigationBarHeight = 0;
    }

    // Deselect text input boxes when the user taps outside of them
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view instanceof EditText) {
                Rect outRect = new Rect();
                view.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    // Deselect text input box
                    view.clearFocus();
                    // Hide keyboard
                    InputMethodManager methodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    methodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        // Disable pressing back if study mode is enabled
        if (StudyMode.isEnabled())
            return;

        super.onBackPressed();
    }

    public static FragmentManager getSavedFragmentManager() {
        return fragmentManager;
    }

    private static FragmentManager fragmentManager;
}

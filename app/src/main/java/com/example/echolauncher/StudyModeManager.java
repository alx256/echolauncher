package com.example.echolauncher;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.gridlayout.widget.GridLayout;

import java.util.ArrayList;
import java.util.List;

public class StudyModeManager {
    public static boolean isEnabled() {
        return enabled;
    }

    public static void enable(int minutes, int seconds, Context context) {
        enabled = true;
        StudyModeManager.context = context;

        waitThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (System.currentTimeMillis() >= endTime) {
                        disable();
                        return;
                    }
                }
            }
        };

        waitThread.start();

        Intent intent = new Intent(context, StudyModeActivity.class);
        endTime = System.currentTimeMillis() + ((long) minutes * 60 * 1000) + ((long) seconds * 1000);
        StudyModeActivity.enabled = true;
        context.startActivity(intent);
    }

    public static void disable() {
        enabled = false;
        Intent intent = new Intent(context, MainActivity.class);
        StudyModeActivity.enabled = false;
        context.startActivity(intent);
    }

    public static long remainingTime() {
        return endTime - System.currentTimeMillis();
    }

    public static void addAllowedApp(AppItem allowedApp) {
        initAllowedApps();

        if (allowedApps.size() < MAX_APPS && !allowedApps.contains(allowedApp))
            allowedApps.add(allowedApp);
    }

    public static void updateGrid(GridLayout grid, boolean moveable) {
        initAllowedApps();
        grid.removeAllViews();

        for (AppItem item : StudyModeManager.allowedApps) {
            AppItem itemCopy = item;
            itemCopy.moveable = moveable;
            grid.addView(itemCopy.copyView());
        }
    }

    public static void updateDropTarget() {
        initAllowedApps();

        updateGrid(dropTarget, true);

        if (allowedApps.size() == 0)
            expandTarget();
        else
            minimiseTarget();
    }

    public static void setDropTarget(DropTarget dropTarget) {
        StudyModeManager.dropTarget = dropTarget;
        deleteCross = dropTarget.findViewById(R.id.delete);
        deleteCross = ((View) dropTarget.getParent()).findViewById(R.id.delete);
        deleteCrossParams = new LinearLayout.LayoutParams(
                Globals.appIconWidth,
                Globals.appHeight,
                0.25f
        );
        dropTarget.removeAllViews();
    }

    public static DropTarget getDropTarget() {
        return dropTarget;
    }

    public static List<AppItem> getAllowedApps() {
        return allowedApps;
    }

    public static void removeAllowedApp(PinItem item) {
        initAllowedApps();

        for (int i = 0; i < allowedApps.size(); i++) {
            AppItem allowedApp = allowedApps.get(i);

            if (allowedApp.getIdentifier() == item.getIdentifier()) {
                allowedApps.remove(i);
                updateDropTarget();
                return;
            }
        }
    }

    private static void initAllowedApps() {
        if (allowedApps == null)
            allowedApps = new ArrayList<>();
    }

    private static void expandTarget() {
        dropTarget.setBackgroundText("Drop apps here to pin");

        deleteCrossParams.weight = 0.0f;
        deleteCrossWidth = deleteCross.getLayoutParams().width;
        deleteCross.getLayoutParams().width = 0;
    }

    private static void minimiseTarget() {
        dropTarget.removeBackgroundText();

        deleteCrossParams.weight = deleteCrossWidth;
        deleteCross.getLayoutParams().width = deleteCrossWidth;
    }

    private static boolean enabled;
    private static Thread waitThread;
    private static long endTime;
    private static Context context;
    private static List<AppItem> allowedApps;
    private static DropTarget dropTarget;
    private static DropTarget deleteCross;
    private static LinearLayout.LayoutParams deleteCrossParams;
    private static int deleteCrossWidth;

    private static final int MAX_APPS = 4;
}
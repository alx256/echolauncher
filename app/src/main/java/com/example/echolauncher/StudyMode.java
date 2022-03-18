package com.example.echolauncher;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.gridlayout.widget.GridLayout;

import java.util.ArrayList;
import java.util.List;

public class StudyMode {
    public static void enable(int hours, int minutes, Activity activity) {
        endTime = System.currentTimeMillis() +
                ((long) hours * 60 * 60 * 1000) +
                ((long) minutes * 60 * 1000);
        isEnabled = true;

        Intent intent = new Intent(activity, activity.getClass());
        activity.finish();
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        activity.startActivity(intent);
    }

    public static void disable() {
        isEnabled = false;
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

        for (AppItem item : allowedApps) {
            AppItem itemCopy = item;
            itemCopy.isMovable = moveable;
            grid.addView(itemCopy.toView(item.context));
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
        StudyMode.dropTarget = dropTarget;
        deleteCross = dropTarget.findViewById(R.id.delete);
        deleteCross = ((View) dropTarget.getParent()).findViewById(R.id.delete);
        deleteCrossParams = new LinearLayout.LayoutParams(
                Globals.appIconWidth,
                Globals.appHeight,
                0.25f
        );
        dropTarget.removeAllViews();
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

    public static boolean isEnabled() {
        return isEnabled;
    }

    private static boolean isEnabled;
    private static long endTime;
    private static List<AppItem> allowedApps;
    private static DropTarget dropTarget, deleteCross;
    private static LinearLayout.LayoutParams deleteCrossParams;
    private static int deleteCrossWidth;
    private static final int MAX_APPS = 4;
}

package com.example.echolauncher;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;

/**
 * This app is used to store the available apps and
 * widgets as well as provide the functionality to
 * search and order them
 * **/

public class Library {
    // Used to determine how items should be compared when
    // searching or sorting through them
    public enum Comparison {
        APP_NAME, // The name of the app that is shown to the user
        APP_IDENTIFIER, // The package name that uniquely identifies the app
        WIDGET_NAME, // The name of the widget
        WIDGET_IDENTIFIER, // The name that uniquely identifies the widget
        DATE, // The date associated with the item (used for sorting events)
        NONE
    }

    public static void init(Context context) {
        apps = new ArrayList<>();
        packageManager = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);

        // Add all apps
        for (ResolveInfo resolveInfo : resolveInfos) {
            ApplicationInfo info = resolveInfo.activityInfo.applicationInfo;

            // Don't show EchoLauncher app on all apps
            if (info.packageName.equals(context.getPackageName()))
                continue;

            Item.Name name = new Item.Name(packageManager.getApplicationLabel(info).toString());
            apps.add(new AppItem(name, info.packageName));
        }

        // Add all widgets
        widgets = new ArrayList<>();
        // Time widget
        widgets.add(new WidgetItem(new Item.Name("Time"), new TimeWidget()));
        // Weather widget
        widgets.add(new WidgetItem(new Item.Name("Weather"), new WeatherWidget()));
        // TimetableSlots widget
        widgets.add(new WidgetItem(new Item.Name("TimetableSlots"), new TimetableWidget()));

        // Order apps by app name so that they in alphabetical order
        Sort.sortApps(Comparison.APP_NAME);
    }

    public static Drawable getDrawable(String packageName) {
        try {
            return packageManager.getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<AppItem> getAllApps() {
        return apps;
    }

    public static List<WidgetItem> getAllWidgets() {
        return widgets;
    }

    public static PackageManager getPackageManager() {
        return packageManager;
    }

    public static Item getDragging() {
        try {
            return Search.get(draggingIdentifier);
        } catch (InvalidObjectException e) {
            // No valid app or widget found
            e.printStackTrace();
            return null;
        }
    }

    public static AppItem appAt(int index) {
        return apps.get(index);
    }

    public static WidgetItem widgetAt(int index) {
        return widgets.get(index);
    }

    public static void setDragging(String identifier) {
        draggingIdentifier = identifier;
    }

    private static List<AppItem> apps;
    private static List<WidgetItem> widgets;
    private static PackageManager packageManager;
    private static String draggingIdentifier;
}

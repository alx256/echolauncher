package com.example.echolauncher;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

public class Library {
    public enum Comparison {
        APP_NAME,
        APP_IDENTIFIER,
        WIDGET_NAME,
        WIDGET_IDENTIFIER,
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

            PinItem.Name name = new PinItem.Name(packageManager.getApplicationLabel(info).toString());
            apps.add(new AppItem(name, info.packageName));
        }

        // Add all widgets
        widgets = new ArrayList<>();
        // Time widget
        widgets.add(new WidgetItem(new PinItem.Name("Time"), new TimeWidget()));
        // Weather widget
        widgets.add(new WidgetItem(new PinItem.Name("Weather"), new WeatherWidget()));
        // Events widget
        widgets.add(new WidgetItem(new PinItem.Name("Events"), new EventsWidget()));

        Sort.mergeSort(Comparison.APP_NAME);
    }

    public static Drawable getDrawable(String packageName) {
        try {
            return packageManager.getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<AppItem> getAllApps() {return apps; };
    public static List<WidgetItem> getAllWidgets() { return widgets; }
    public static PackageManager getPackageManager() { return packageManager; }
    public static PinItem getDragging() { return Search.get(draggingIdentifier); }

    public static AppItem appAt(int index) { return apps.get(index); }
    public static WidgetItem widgetAt(int index) { return widgets.get(index); }

    public static void setDragging(String identfier) {
        draggingIdentifier = identfier;
    }

    private static List<AppItem> apps;
    private static List<WidgetItem> widgets;
    private static PackageManager packageManager;
    private static String draggingIdentifier;
}

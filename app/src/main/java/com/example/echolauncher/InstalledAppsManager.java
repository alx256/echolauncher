package com.example.echolauncher;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

enum Comparison {
    APP_NAME,
    PACKAGE_NAME
}

public class InstalledAppsManager {
    static public void Init(Context context) {
        apps = new ArrayList<>();
        packageManager = context.getPackageManager();
        availableIcons = new ArrayList<>();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);

        for (ResolveInfo resolveInfo : resolveInfos) {
            ApplicationInfo info = resolveInfo.activityInfo.applicationInfo;

            // Don't show EchoLauncher app on all apps
            if (info.packageName.equals(context.getPackageName()))
                continue;

            availableIcons.add(getDrawable(info.packageName));
            apps.add(new AppItem(packageManager.getApplicationLabel(info).toString(), info.packageName, availableIcons.size() - 1));
        }

        sort(apps);
    }

    static public List<AppItem> getAll() {
        return apps;
    }

    static public PinItem get(String packageName) {
        int index = binarySearch(packageName, 0, apps.size() - 1, Comparison.PACKAGE_NAME);
        if (index > -1)
            return apps.get(index);
        else
            return null;
    }

    static public Drawable getDrawable(String packageName) {
        try {
            return packageManager.getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    static public List<AppItem> searchFor(String app) {
        int result = binarySearch(app, 0, apps.size() - 1, Comparison.APP_NAME);

        if (result == -1)
            return new ArrayList<>();

        int start = result, end = result;
        start = findLimit(start, -1, app);
        end = findLimit(end, 1, app);

        return new ArrayList<>(apps.subList(start, end + 1));
    }

    // Find when index doesn't contain a matching item anymore
    static private int findLimit(int index, int add, String string) {
        boolean done = false;

        while (!done) {
            if (index < 0) {
                index = 0;
                break;
            }

            if (index >= apps.size()) {
                index = apps.size() - 1;
                break;
            }

            AppItem item = apps.get(index);
            if (item == null)
                break;

            String shortened, name = item.getName();
            if (string.length() <= name.length())
                shortened = name.substring(0, string.length());
            else
                shortened = name;

            if (!shortened.equalsIgnoreCase(string)) {
                index -= add;
                done = true;
            } else
                index += add;
        }

        return index;
    }

    static private int binarySearch(String string, int start, int end, Comparison comparison) {
        while (end >= start) {
            int mid = (start + end) / 2;
            String target, search;
            switch (comparison) {
                case APP_NAME:
                    target = apps.get(mid).getName();
                    break;
                case PACKAGE_NAME:
                    target = apps.get(mid).getIdentifier();
                    break;
                default:
                    target = new String();
            }

            search = target;

            if (target.length() > string.length())
                search = target.substring(0, string.length());

            if (compare(search, string))
                start = mid + 1;
            else if (!compare(search, string))
                end = mid - 1;

            if (search.equalsIgnoreCase(string))
                return mid;
        }

        return -1;
    }

    static private boolean compare(String string1, String string2) {
        if (string1 == null && string2 == null)
            return true;

        for (int index = 0; index < string1.length(); index++) {
            if (index >= string2.length())
                return true;

            String lowercase1 = string1.toLowerCase(), lowercase2 = string2.toLowerCase();

            if (lowercase1.charAt(index) != lowercase2.charAt(index))
                return lowercase1.charAt(index) < lowercase2.charAt(index);
        }

        return false;
    }

    static private boolean compare(AppItem item1, AppItem item2) {
        return compare(item1.getName(), item2.getName());
    }

    static private void sort(List<AppItem> list) {
        if (list.size() <= 1)
            return;

        int mid = list.size() / 2;

        List<AppItem> list1 = new ArrayList<>(list.subList(0, mid)),
                list2 = new ArrayList<>(list.subList(mid, list.size()));

        sort(list1);
        sort(list2);

        merge(list1, list2, list);
    }

    static private void merge(List<AppItem> list1, List<AppItem> list2, List<AppItem> list) {
        int index1 = 0, index2 = 0, index = 0;

        while (index1 < list1.size() && index2 < list2.size()) {
            if (compare(list1.get(index1), list2.get(index2)))
                list.set(index++, list1.get(index1++));
            else
                list.set(index++, list2.get(index2++));
        }

        // Fill list with remaining items from list1 and list2
        while (index1 < list1.size())
            list.set(index++, list1.get(index1++));

        while (index2 < list2.size())
            list.set(index++, list2.get(index2++));
    }

    static public PackageManager getPackageManager() { return packageManager; }

    static public PinItem dragging;
    static public List<Drawable> availableIcons;
    static public AppShadowBuilder shadowBuilder;
    static public boolean hidden = false;

    static private PackageManager packageManager;
    static private List<AppItem> apps;
}
package com.example.echolauncher;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class InstalledAppsManager {
    static public List<AppItem> getApps(Context context) {
        apps = new ArrayList<>();
        packageManager = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);

        for (ResolveInfo resolveInfo : resolveInfos) {
            ApplicationInfo info = resolveInfo.activityInfo.applicationInfo;

            // Don't show EchoLauncher app on all apps
            if (info.packageName.equals(context.getPackageName()))
                continue;

            apps.add(new AppItem(packageManager.getApplicationLabel(info).toString(), info.packageName, info.loadIcon(packageManager)));
        }

        mergeSortApps();

        return apps;
    }

    static public List<AppItem> searchFor(String app) {
        int result = binarySearch(app, 0, apps.size() - 1);

        if (result == -1)
            return new ArrayList<>();

        List<AppItem> finalList = new ArrayList<>();
        boolean done = false;
        int index = result;

        while (!done) {
            if (apps.get(index).getName().substring(0, app.length()).equalsIgnoreCase(app))
                finalList.add(apps.get(index++));
            else
                done = true;
        }

        return finalList;
    }

    // Checks if the list starts and ends with items that match the string
    static private boolean isFound(String string, List<AppItem> list) {
        String string1 = list.get(0).getName().substring(0, string.length()),
                string2 = list.get(list.size() - 1).getName().substring(0, string.length());

        return (string1.equalsIgnoreCase(string)) && (string2.equalsIgnoreCase(string));
    }

    static private int binarySearch(String string, int start, int end) {
        while (end >= start) {
            int mid = start + (end - start) / 2;
            String shortenedString = apps.get(mid).getName().substring(0, string.length());

            if (shortenedString.equalsIgnoreCase(string))
                return mid;

            if (compare(shortenedString, string))
                start = mid + 1;
            else
                end = mid - 1;
        }

        return -1;
    }

//    static private List<AppItem> binarySearch(String string, List<AppItem> list) {
//        if (list == null)
//            return new ArrayList<>();
//
//        int mid = list.size() / 2;
//
//        if (list.get(mid).getName() == string)
//            return new ArrayList<>(list.subList(mid, mid + 1));
//
//        List<AppItem> list1 = new ArrayList<>(list.subList(0, mid)),
//                list2 = new ArrayList<>(list.subList(mid, list.size()));
//
//        if (list1.isEmpty())
//            return list2;
//
//        if (list2.isEmpty())
//            return list1;
//
//        String string1 = list1.get(list1.size() - 1).getName(),
//                string2 = list2.get(0).getName();
//
//        Log.d("", "list1:");
//        for (AppItem item : list1)
//            Log.d("", "\t" + item.getName());
//
//        Log.d("", "list2:");
//        for (AppItem item : list2)
//            Log.d("", "\t" + item.getName());
//
//        if (compare(string, string1))
//            return binarySearch(string, list1);
//
//        if (compare(string2, string))
//            return binarySearch(string, list2);
//
//        if (isFound(string, list1))
//            return list1;
//
//        if (isFound(string, list2))
//            return list2;
//
//
//        Log.d("", "end");
//        return new ArrayList<>();
//    }

    static private boolean compare(String string1, String string2) {
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

    static private void mergeSortApps() {
        sort(apps);
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

    static private PackageManager packageManager;
    static private List<AppItem> apps;
}

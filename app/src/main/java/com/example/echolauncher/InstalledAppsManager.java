package com.example.echolauncher;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import okhttp3.CertificatePinner;

enum Comparison {
    APP_NAME,
    APP_IDENTIFIER,
    WIDGET_NAME,
    WIDGET_IDENTIFIER,
    NONE
}

enum Instruction {
    PIN,
    ADD,
    HOVER,
    CLEAR
}

public class InstalledAppsManager {
    static public class InstructionCollection {
        public InstructionCollection(Instruction instruction, String identifier) {
            this.instruction = instruction;
            this.identifier = identifier;
        }

        public Instruction getInstruction() {
            return instruction;
        }

        public String getIdentifier() {
            return identifier;
        }

        private Instruction instruction;
        private String identifier;
    }

    static public void Init(Context context) {
        apps = new ArrayList<>();
        packageManager = context.getPackageManager();
        availableIcons = new ArrayList<>();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);

        // Add all apps
        for (ResolveInfo resolveInfo : resolveInfos) {
            ApplicationInfo info = resolveInfo.activityInfo.applicationInfo;

            // Don't show EchoLauncher app on all apps
            if (info.packageName.equals(context.getPackageName()))
                continue;

            availableIcons.add(getDrawable(info.packageName));
            PinItem.Name name = new PinItem.Name(packageManager.getApplicationLabel(info).toString());
            apps.add(new AppItem(name, info.packageName, availableIcons.size() - 1));
        }

        // Add all widgets
        widgets = new ArrayList<>();
        // Weather widget
        widgets.add(new WidgetItem(new PinItem.Name("Weather"), new WeatherWidget()));

        mergeSort(Comparison.APP_NAME);
    }

    static public List<AppItem> getAllApps() {
        return apps;
    }
    static public List<WidgetItem> getAllWidgets() { return widgets; }

    static public PinItem get(String identifier) {
        int index = binarySearch(identifier, 0, apps.size() - 1, Comparison.APP_IDENTIFIER);
        if (index > -1)
            return apps.get(index);

        index = binarySearch(identifier, 0, widgets.size() - 1, Comparison.WIDGET_IDENTIFIER);
        if (index > - 1)
            return widgets.get(index);

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

    static public void updateGrid(int position, Instruction instruction, String identifier) {
        if (homeScreenInstructions.get(position) == null)
            homeScreenInstructions.put(position, new ArrayList<>());
        homeScreenInstructions.get(position).add(new InstructionCollection(instruction, identifier));
        gridAdapter.notifyItemChanged(position);
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

            String shortened, name = item.getName().full();
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
        mergeSort(comparison);

        while (end >= start) {
            int mid = (start + end) / 2;
            String target;
            switch (comparison) {
                case APP_NAME:
                    target = apps.get(mid).getName().full();
                    break;
                case APP_IDENTIFIER:
                    target = apps.get(mid).getIdentifier();
                    break;
                case WIDGET_NAME:
                    target = widgets.get(mid).getName().full();
                    break;
                case WIDGET_IDENTIFIER:
                    target = widgets.get(mid).getIdentifier();
                    break;
                default:
                    target = new String();
            }

            if (target.length() > string.length())
                target = target.substring(0, string.length());

            if (compare(target, string))
                start = mid + 1;
            else if (!compare(target, string))
                end = mid - 1;

            if (target.equalsIgnoreCase(string))
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

    static private void mergeSort(Comparison comparison) {
        if (comparison == currentComparison)
            return;

        sort(apps, comparison);
        currentComparison = comparison;
    }

    static private void sort(List<AppItem> list, Comparison comparison) {
        if (list.size() <= 1)
            return;

        int mid = list.size() / 2;

        List<AppItem> list1 = new ArrayList<>(list.subList(0, mid)),
                list2 = new ArrayList<>(list.subList(mid, list.size()));

        sort(list1, comparison);
        sort(list2, comparison);

        merge(list1, list2, list, comparison);
    }

    static private void merge(List<AppItem> list1, List<AppItem> list2, List<AppItem> list,
                              Comparison comparison) {
        int index1 = 0, index2 = 0, index = 0;
        String string1, string2;

        while (index1 < list1.size() && index2 < list2.size()) {
            if (comparison == Comparison.APP_NAME) {
                string1 = list1.get(index1).getName().full();
                string2 = list2.get(index2).getName().full();
            } else {
                string1 = list1.get(index1).getIdentifier();
                string2 = list2.get(index2).getIdentifier();
            }

            if (compare(string1, string2))
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

    static public String dragging;
    static public List<Drawable> availableIcons;
    static public Map<Integer, List<InstructionCollection>> homeScreenInstructions;
    static public AppShadowBuilder shadowBuilder;
    static public HomeScreenGridAdapter gridAdapter;
    static public boolean hidden = false;

    static private PackageManager packageManager;
    static private List<AppItem> apps;
    static private List<WidgetItem> widgets;
    static private Comparison currentComparison = Comparison.NONE;
}
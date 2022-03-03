package com.example.echolauncher;

import java.util.ArrayList;
import java.util.List;

public class Search {
    public static List<AppItem> find(String app) {
        int result = binarySearch(app, 0, Library.getAllApps().size() - 1, Library.Comparison.APP_NAME);

        if (result == -1)
            return new ArrayList<>();

        int start = result, end = result;
        start = findLimit(start, -1, app);
        end = findLimit(end, 1, app);

        return new ArrayList<>(Library.getAllApps().subList(start, end + 1));
    }

    public static PinItem get(String identifier) {
        int index = binarySearch(identifier, 0,
                Library.getAllApps().size() - 1, Library.Comparison.APP_IDENTIFIER);
        if (index > -1)
            return Library.getAllApps().get(index);

        index = binarySearch(identifier, 0,
                Library.getAllWidgets().size() - 1,Library.Comparison.WIDGET_IDENTIFIER);
        if (index > - 1)
            return Library.getAllWidgets().get(index);

        return null;
    }

    private static int binarySearch(String string, int start, int end, Library.Comparison comparison) {
        Sort.mergeSort(comparison);

        while (end >= start) {
            int mid = (start + end) / 2;
            String target;
            switch (comparison) {
                case APP_NAME:
                    target = Library.appAt(mid).getName().full();
                    break;
                case APP_IDENTIFIER:
                    target = Library.appAt(mid).getIdentifier();
                    break;
                case WIDGET_NAME:
                    target = Library.widgetAt(mid).getName().full();
                    break;
                case WIDGET_IDENTIFIER:
                    target = Library.widgetAt(mid).getIdentifier();
                    break;
                default:
                    target = "";
            }

            if (target.length() > string.length())
                target = target.substring(0, string.length());

            int compare = target.compareToIgnoreCase(string);

            if (compare < 0)
                start = mid + 1;
            else if (compare > 0)
                end = mid - 1;

            if (compare == 0)
                return mid;
        }

        return -1;
    }

    // Find when index doesn't contain a matching item anymore
    private static int findLimit(int index, int add, String string) {
        boolean done = false;

        while (!done) {
            if (index < 0) {
                index = 0;
                break;
            }

            if (index >= Library.getAllApps().size()) {
                index = Library.getAllApps().size() - 1;
                break;
            }

            AppItem item = Library.appAt(index);
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
}

package com.example.echolauncher;

import java.util.ArrayList;
import java.util.List;

/**
 * Merge sort algorithm
 * **/

public class Sort {
    public interface StringVersion {
        String getString(int listNumber, int index);
    }

    public static void sortApps(Library.Comparison comparison) {
        // Don't sort data again if it has already
        // been sorted
        if (comparison == appComparison)
            return;

        sort(Library.getAllApps(), comparison);
        appComparison = comparison;
    }

    public static void sortWidgets(Library.Comparison comparison) {
        // Don't sort data again if it has already
        // been sorted
        if (comparison == widgetComparison)
            return;

        sort(Library.getAllWidgets(), comparison);
        widgetComparison = comparison;
    }

    public static void sortTimetable() {
        sort(TimetableSlots.getAll(), Library.Comparison.DATE);
    }

    private static <T> void sort(List<T> list, Library.Comparison comparison) {
        // List is either empty or has one item,
        // so there is no point in sorting
        if (list.size() <= 1)
            return;

        int mid = list.size() / 2;

        // Split list
        List<T> list1 = new ArrayList<>(list.subList(0, mid)),
                list2 = new ArrayList<>(list.subList(mid, list.size()));

        StringVersion stringVersion = (listNumber, index) -> {
            Object var = null;

            if (listNumber == 1)
                var = list1.get(index);
            else if (listNumber == 2)
                var = list2.get(index);

            if (comparison == Library.Comparison.APP_NAME ||
                comparison == Library.Comparison.WIDGET_NAME)
                return ((Item) var).getName().full();

            if (comparison == Library.Comparison.APP_IDENTIFIER ||
                comparison == Library.Comparison.WIDGET_IDENTIFIER)
                return ((Item) var).getIdentifier();

            if (comparison == Library.Comparison.DATE)
                return Long.toString(((TimetableSlot) var).getMillis());

            return null;
        };

        // Recursively split lists again
        sort(list1, comparison);
        sort(list2, comparison);

        // Merge lists
        merge(list1, list2, list, stringVersion);
    }

    static private <T> void merge(List<T> list1,
                              List<T> list2,
                              List<T> list,
                              StringVersion stringVersion) {
        int index1 = 0, index2 = 0, index = 0;

        while (index1 < list1.size() && index2 < list2.size()) {
            String string1 = stringVersion.getString(1, index1),
                string2 = stringVersion.getString(2, index2);

            if (string1.compareToIgnoreCase(string2) < 0)
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

    private static Library.Comparison appComparison = Library.Comparison.NONE,
        widgetComparison = Library.Comparison.NONE;
}

package com.example.echolauncher;

import java.util.ArrayList;
import java.util.List;

public class Sort {
    static public void mergeSort(Library.Comparison comparison) {
        if (comparison == currentComparison)
            return;

        sort(Library.getAllApps(), comparison);
        currentComparison = comparison;
    }

    static private void sort(List<AppItem> list, Library.Comparison comparison) {
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
                              Library.Comparison comparison) {
        int index1 = 0, index2 = 0, index = 0;
        String string1, string2;

        while (index1 < list1.size() && index2 < list2.size()) {
            if (comparison == Library.Comparison.APP_NAME) {
                string1 = list1.get(index1).getName().full();
                string2 = list2.get(index2).getName().full();
            } else {
                string1 = list1.get(index1).getIdentifier();
                string2 = list2.get(index2).getIdentifier();
            }

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

    private static Library.Comparison currentComparison = Library.Comparison.NONE;
}

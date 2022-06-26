package com.example.echolauncher.utilities;

import com.example.echolauncher.drawer.AppItem;
import com.example.echolauncher.drawer.Item;
import com.example.echolauncher.apps.Library;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;

/**
 * Binary search algorithm
 */

public class Search {
    // Returns all AppItems that match the string
    public static List<AppItem> find(String app) {
        // Returns the index of the first matching item
        int result = binarySearch(app, 0, Library.getAllApps().size() - 1, Library.Comparison.APP_NAME);

        // No matches, return empty list
        if (result == -1)
            return new ArrayList<>();

        // As the data is sorted in alphabetical order,
        // search after and before the first matching item
        // until the item doesn't match any more to find
        // all matching items
        int start = result, end = result;
        start = findLimit(start, -1, app);
        end = findLimit(end, 1, app);

        // Return sublist of all installed apps,
        // but only the ones that match the string
        return new ArrayList<>(Library.getAllApps().subList(start, end + 1));
    }

    // Returns the item with the provided identifier
    // (only needs to return 1 as the identifier is unique)
    public static Item get(String identifier) throws InvalidObjectException {
        int index;

        // If the identifier matches an app, then return that app
        index = binarySearch(identifier, 0,
                Library.getAllApps().size() - 1,
                Library.Comparison.APP_IDENTIFIER);
        if (index > -1)
            return Library.getAllApps().get(index);

        // If the identifier matches a widget, then return that widget
        index = binarySearch(identifier, 0,
                Library.getAllWidgets().size() - 1,
                Library.Comparison.WIDGET_IDENTIFIER);
        if (index > -1)
            return Library.getAllWidgets().get(index);

        // None match, meaning this function has been called
        // incorrectly, so return an exception instead
        throw new InvalidObjectException("Invalid identifier '" + identifier + "' for get() method!");
    }

    private static int binarySearch(String string, int start, int end, Library.Comparison comparison) {
        // Sort the data so it can be searched.
        // If the data is already sorted in the way
        // that is needed, then the sortApps method
        // won't sort it again
        if (comparison == Library.Comparison.APP_NAME ||
                comparison == Library.Comparison.APP_IDENTIFIER)
            Sort.sortApps(comparison);

        if (comparison == Library.Comparison.WIDGET_NAME ||
                comparison == Library.Comparison.WIDGET_IDENTIFIER)
            Sort.sortWidgets(comparison);

        while (end >= start) {
            // Calculate middle
            int mid = (start + end) / 2;
            String target;

            // Find the necessary string
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

            // If the user has only typed some of the word,
            // then only search for the equivalent substring.
            // E.g. if the user has entered "ma", then
            // "maps" should be a search result as it starts
            // with "ma"
            if (target.length() > string.length())
                target = target.substring(0, string.length());

            int compare = target.compareToIgnoreCase(string);

            // String is after the current item, increase the start
            if (compare < 0)
                start = mid + 1;
            // String is before the current item, decrease the end
            else if (compare > 0)
                end = mid - 1;

            // String is the current item, return it
            if (compare == 0)
                return mid;
        }

        // Not found, return -1
        return -1;
    }

    // Find when index doesn't contain a matching item anymore
    private static int findLimit(int index, int add, String string) {
        boolean done = false;

        while (!done) {
            // Reached beginning, don't
            // allow out of bounds indices
            if (index < 0) {
                index = 0;
                break;
            }

            // Reached end, don't
            // allow out of bounds indices
            if (index >= Library.getAllApps().size()) {
                index = Library.getAllApps().size() - 1;
                break;
            }

            AppItem item = Library.appAt(index);

            // Only search substring of word, based on
            // how much user has typed
            String shortened, name = item.getName().full();
            if (string.length() <= name.length())
                shortened = name.substring(0, string.length());
            else
                shortened = name;

            // Found item that doesn't match anymore, subtract index
            // and terminate loop
            if (!shortened.equalsIgnoreCase(string)) {
                index -= add;
                done = true;
            // Item still needs to be found, keep looking
            } else
                index += add;
        }

        return index;
    }
}

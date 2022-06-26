package com.example.echolauncher.utilities;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

/**
 * Allows storing data on a local file.
 * Used to store home screen layouts,
 * the timetable and study mode allowed apps
 */

public class Storage {
    // Wrapper class around a hashmap that is returned
    // when data is read. This class is used purely
    // so that receiving a string value and an
    // int value is easier with the getString and
    // getInt methods
    public static class Line {
        public Line() {
            table = new Hashtable<>();
        }

        public void put(String key, String value) {
            table.put(key, value);
        }

        public String getString(String name) {
            return get(name);
        }

        public int getInt(String name) {
            return Integer.parseInt(get(name));
        }

        public long getLong(String name) {
            return Long.parseLong(get(name));
        }

        private String get(String name) {
            String string = table.get(name);

            if (string == null) {
                // Report a message for debugging
                android.util.Log.i("Storage",
                        "Accessed string " + name +
                                " is null! There could be a typo or this data has not been saved.");
                return "-1";
            }

            return string;
        }

        Hashtable<String, String> table;
    }

    public Storage(Context context, String format, String fileName) {
        FILE_NAME = fileName;

        // Create file if necessary, but don't overwrite any data
        try {
            outputStream = context.openFileOutput(FILE_NAME, Context.MODE_APPEND);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // The format is a string that tells the Storage class
        // how the data should be formatted. For example,
        // identifier,position,screen means that each line will
        // have the identifier as the first value stored, the position
        // as the second value stored and so on, with each value being
        // separated by a comma.
        this.format = format;

        // Find the character separating values
        for (char c : format.toCharArray()) {
            if (!Character.isLetter(c)) {
                delimiter = c;
                break;
            }
        }

        CONTEXT = context;
        String[] names = format.split(Character.toString(delimiter));
        formatNames = new ArrayList<>();
        formatNames.addAll(Arrays.asList(names));
    }

    public void writeItem(Object... args) throws IOException {
        // Open in append mode to append data to the end of the file
        try {
            outputStream = CONTEXT.openFileOutput(FILE_NAME, Context.MODE_APPEND);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        StringBuilder data = new StringBuilder();

        for (Object object : args) {
            String string = String.valueOf(object);
            data.append(string).append(delimiter);
        }

        data.append('\n');

        for (char c : data.toString().toCharArray())
            outputStream.write(c);

        outputStream.close();
    }

    // Returns list containing all stored items, stored as Line instances
    public List<Line> readItems() throws IOException {
        try {
            inputStream = CONTEXT.openFileInput(FILE_NAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        char c;
        int segment = 0, value;
        List<Line> list = new ArrayList<>();
        Line line = new Line();
        String str = "";

        while ((value = inputStream.read()) != -1) {
            c = (char) value;

            if (c == '\n') {
                // Newline
                list.add(line);
                line = new Line();
                segment = 0;
                str = "";
            } else {
                if (c == delimiter) {
                    line.put(formatNames.get(segment++), str);
                    str = "";
                    continue;
                }

                str += c;
            }
        }

        inputStream.close();
        return list;
    }

    public void clear() {
        // Reset file
        try {
            outputStream = CONTEXT.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private FileOutputStream outputStream;
    private FileInputStream inputStream;
    private final String FILE_NAME;
    private String format = "";
    private char delimiter;
    private List<String> formatNames;
    private final Context CONTEXT;
}

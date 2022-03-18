package com.example.echolauncher;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class Storage {
    public static class Line {
        public Line() {
            map = new Hashtable<>();
        }

        public void put(String key, String value) {
            map.put(key, value);
        }

        public String getString(String name) {
            return map.get(name);
        }

        public int getInt(String name) {
            return Integer.parseInt(map.get(name));
        }

        Map<String, String> map;
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

        this.format = format;

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

    // Returns multi dimensional array containing all stored items
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

            switch (c) {
                case '\n':
                    // Newline
                    list.add(line);
                    line = new Line();
                    segment = 0;
                    str = "";
                    break;
                default:
                    if (c == delimiter) {
                        line.put(formatNames.get(segment++), str);
                        str = "";
                        break;
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

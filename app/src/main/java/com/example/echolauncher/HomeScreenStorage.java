package com.example.echolauncher;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class HomeScreenStorage {
    public static void init(Context context) {
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
    }

    public static void writeItem(String identifier, int position, int screen, Context context) throws IOException {
        try {
            outputStream = context.openFileOutput(FILE_NAME, Context.MODE_APPEND);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String data = identifier + ',' + position + ',' + screen + '\n';

        for (char c : data.toCharArray())
            outputStream.write(c);

        outputStream.close();
    }

    public static void readItems(Context context) throws IOException {
        try {
            inputStream = context.openFileInput(FILE_NAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        char c;
        String identifier = "", position = "", screen = "";
        int segment = 0, value;

        while ((value = inputStream.read()) != -1) {
            c = (char) value;
            switch (c) {
                case '\n':
                    // Newline
                    int positionInt = toInt(position), screenInt = toInt(screen);

                    HomeScreenGrid.updateGrid(positionInt, HomeScreenGridAdapter.Instruction.ADD, Search.get(identifier));

                    identifier = "";
                    position = "";
                    screen = "";
                    segment = 0;
                    break;
                case ',':
                    segment++;
                    break;
                default:
                    switch (segment) {
                        case 0:
                            identifier += c;
                            break;
                        case 1:
                            position += c;
                            break;
                        case 2:
                            screen += c;
                            break;
                    }
            }
        }

        inputStream.close();
    }

    private static int toInt(String value) {
        int ret = 0, place = 1;
        char[] arr = value.toCharArray();

        for (int i = arr.length - 1; i >= 0; i--) {
            char c = arr[i];
            ret += (c - '0') * place;
            place *= 10;
        }

        return ret;
    }

    private static FileOutputStream outputStream;
    private static FileInputStream inputStream;
    private static final String FILE_NAME = "echolauncher_homescreen";
}

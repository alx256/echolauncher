package com.example.echolauncher;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class HomeScreenStorage {
    public static class SavedItem {
        public SavedItem(String identifier, int position, int screen) {
            this.identifier = identifier;
            this.position = position;
            this.screen = screen;
        }

        public String identifier;
        public int position, screen;
    }

    public static void Init(Context context) {
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

    public static void WriteItem(String identifier, int position, int screen, Context context) throws IOException {
        try {
            outputStream = context.openFileOutput(FILE_NAME, Context.MODE_APPEND);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String data = identifier + ',' + position + ',' + screen + '\n';
        SavedItem item = new SavedItem(identifier, position, screen);

        for (char c : data.toCharArray())
            outputStream.write(c);

        outputStream.close();
    }

    public static void ReadItems(Context context) throws IOException {
        try {
            inputStream = context.openFileInput(FILE_NAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        char c;
        String identifier = "", position = "", screen = "";
        int segment = 0, value;

//        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (service.getClass().getName().equals(serviceInfo.service.getClassName())) {
//                // Service is already running
//                return;
//            }
//        }

        int i = 0;

        while ((value = inputStream.read()) != -1) {
            c = (char) value;
            switch (c) {
                case '\n':
                    // Newline
                    int positionInt = toInt(position), screenInt = toInt(screen);

                    InstalledAppsManager.updateGrid(positionInt, Instruction.ADD, identifier);

                    Log.d("test" + (i++), positionInt + ":" + identifier);

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

        isRead = true;
        inputStream.close();
    }

    public static boolean isRead() {
        return isRead;
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
    private static boolean isRead = false;
    private static final String FILE_NAME = "echolauncher_homescreen";
}
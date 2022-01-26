package com.example.echolauncher;

import java.util.ArrayList;
import java.util.List;

public class WidgetsManager {
    public static List<WidgetItem> getAll() {
        List<WidgetItem> list = new ArrayList<>();
        // Weather widget
        list.add(new WidgetItem("Weather", new WeatherWidget()));
        return list;
    }
}
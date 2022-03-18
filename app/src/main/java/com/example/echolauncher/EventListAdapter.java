package com.example.echolauncher;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventListAdapter extends BaseAdapter {
    public EventListAdapter(Context context, Storage storage) {
        super();

        CONTEXT = context;
        EventListAdapter.storage = storage;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Event event = events.get(position);
        LinearLayout layout = new LinearLayout(CONTEXT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 0, 30, 0);
        layout.setDividerPadding(30);
        layout.addView(event.getNameTextView(CONTEXT));
        layout.addView(event.getDateTextView(CONTEXT));
        layout.addView(event.getTimeRemainingTextView(CONTEXT));
        return layout;
    }

    public static void read() {
        if (storage == null)
            return;

        events = new ArrayList<>();
        try {
            List<Storage.Line> contents = storage.readItems();

            for (Storage.Line line : contents) {
                events.add(new Event(line.getString("name"),
                        line.getInt("year"),
                        line.getInt("month"),
                        line.getInt("day"),
                        line.getInt("hours"),
                        line.getInt("minutes")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        hasRead = true;
    }

    public static void addNew(Event event) {
        if (events == null)
            events = new ArrayList<>();

        events.add(event);
    }

    public static String getNext() {
        if (events == null && !hasRead) {
            read();
            return "Retrieving events...";
        }

        if (events == null && hasRead)
            return "No events scheduled!";

        return events.get(0).getName();
    }

    private static List<Event> events;
    private final Context CONTEXT;
    private static Storage storage;
    private static boolean hasRead = false;
}

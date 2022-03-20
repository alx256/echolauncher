package com.example.echolauncher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TimetableSlots {
    public static void read(Storage storage) {
        timetableSlots = new ArrayList<>();

        if (storage == null)
            return;

        try {
            List<Storage.Line> contents = storage.readItems();

            for (Storage.Line line : contents) {
                timetableSlots.add(new TimetableSlot(line.getString("name"),
                        line.getInt("year"),
                        line.getInt("month"),
                        line.getInt("day"),
                        line.getInt("hours"),
                        line.getInt("minutes"),
                        line.getLong("dateMillis")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addNew(TimetableSlot timetableSlot) {
        if (timetableSlots == null)
            timetableSlots = new ArrayList<>();

        timetableSlots.add(timetableSlot);

        Sort.sortTimetable();
    }

    public static String getNext() {
        if (timetableSlots == null)
            timetableSlots = new ArrayList<>();

        if (timetableSlots.size() < 1)
            return "No activities scheduled!";

        return "Next activity: " + timetableSlots.get(0).getName();
    }

    public static List<TimetableSlot> getAll() {
        if (timetableSlots == null)
            timetableSlots = new ArrayList<>();

        return timetableSlots;
    }

    public static TimetableSlot get(int position) {
        if (timetableSlots == null)
            timetableSlots = new ArrayList<>();

        return timetableSlots.get(position);
    }

    private static List<TimetableSlot> timetableSlots;
}

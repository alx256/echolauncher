package com.example.echolauncher;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Widget that displays the time
 */

public class TimeWidget extends Widget {
    TimeWidget() {
        super.identifier = "widget.time";
        super.color = 0xFFFFEE99; // Yellow colour
    }

    @Override
    public void tick() {
        super.textPositions.put('L', getTime());
    }

    private String getTime() {
        // Get the time and display it as a formatted string
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.UK);
        Date date = Calendar.getInstance().getTime();
        return format.format(date);
    }
}

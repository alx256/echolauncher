package com.example.echolauncher;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimeWidget extends Widget {
    TimeWidget() {
        super.identifier = "widget.time";
        super.color = 0xFFFFEE99;
    }

    @Override
    public void tick() {
        super.textPositions.put('L', getTime());
    }

    private String getTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.US);
        Date date = Calendar.getInstance().getTime();
        return format.format(date);
    }
}

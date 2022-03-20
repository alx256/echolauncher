package com.example.echolauncher;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import java.util.Locale;

public class TimetableSlot {
    TimetableSlot(String name, int year, int month, int day, int hours, int minutes, long dateMillis) {
        this.name = name;

        this.year = year;
        this.month = month;
        this.day = day;
        this.hours = hours;
        this.minutes = minutes;
        this.dateMillis = dateMillis;
    }

    public String getName() {
        return name;
    }

    public long getMillis() {
        return dateMillis;
    }

    public TextView getNameTextView(Context context) {
        TextView textView = getTextView(context);
        textView.setText(name);
        return textView;
    }

    public TextView getDateTextView(Context context) {
        TextView textView = getTextView(context);
        textView.setText(String.format(Locale.UK, "%d/%d/%d", day, month, year));
        return textView;
    }

    public TextView getTimeRemainingTextView(Context context) {
        TextView textView = getTextView(context);
        textView.setText(TimeSetter.NaturalLanguage.get(hours, minutes));
        return textView;
    }

    private TextView getTextView(Context context) {
        TextView textView = new TextView(context);
        Typeface font = Typeface.createFromAsset(context.getAssets(), "KumbhSans-Light.ttf");
        textView.setTypeface(font);
        textView.setTextSize(17.0f);
        return textView;
    }

    private String name;
    private int year, month, day, hours, minutes;
    private long dateMillis;
}

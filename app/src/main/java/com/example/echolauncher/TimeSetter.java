package com.example.echolauncher;

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.TextView;

/**
 * Class to display time setting
 * interface
 */

public class TimeSetter {
    // Listener which is called when
    // the time has been set
    public interface OnFinishListener {
        void onFinish(int hours, int minutes);
    }

    // Returns the hours and minutes remaining
    // as a natural language string
    // e.g. "2 hours and 30 minutes" instead of
    // "2:30"
    public static class NaturalLanguage {
        public static String get(int hours, int minutes) {
            StringBuilder hoursStr = new StringBuilder(),
                    minutesStr = new StringBuilder();
            String str;

            // Pluralise hours if necessary
            hoursStr.append(hours).append(" ").append((hours > 1) ? "hours" : "hour");
            // Pluralise minutes if necessary
            if (minutes >= 1)
                minutesStr.append(minutes).append(" ").append((minutes > 1) ? "minutes" : "minute");
            else {
                minutesStr.append("less than 1 minute");

                // Capitalise "less" if necessary
                minutesStr.setCharAt(0, (hours <= 0) ? 'L' : 'l');
            }

            // Display just the hours, just the minutes,
            // or both depending on which values have
            // been set
            if (hours <= 0)
                str = minutesStr.toString();
            else if (minutes <= 0)
                str = hoursStr.toString();
            else
                str = String.format("%s and %s", hoursStr, minutesStr);

            return str;
        }
    }

    public TimeSetter(TextView textView, Context context) {
        textView.setOnClickListener(view -> showDialog(context));

        onTimeSetListener = (v, hours, minutes) -> {
            this.hours = hours;
            this.minutes = minutes;

            textView.setText(TimeSetter.NaturalLanguage.get(hours, minutes));
            onFinishListener.onFinish(hours, minutes);
        };
    }

    // Create new TimePickerDialog and show it
    public void showDialog(Context context) {
        dialog = new TimePickerDialog(context,
                onTimeSetListener, hours, minutes, true);
        dialog.show();
    }

    public void setOnFinishListener(OnFinishListener listener) {
        onFinishListener = listener;
    }

    private TimePickerDialog.OnTimeSetListener onTimeSetListener;
    private OnFinishListener onFinishListener;
    private TimePickerDialog dialog;
    private int hours, minutes;
}

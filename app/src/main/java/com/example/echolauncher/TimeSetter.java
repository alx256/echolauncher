package com.example.echolauncher;

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.TextView;

public class TimeSetter {
    public interface OnFinishListener {
        void onFinish(int hours, int minutes);
    }

    public static class NaturalLanguage {
        public static String get(int hours, int minutes) {
            String str = "", hoursStr, minutesStr;

            hoursStr = hours + " " + ((hours > 1) ? "hours" : "hour");
            if (minutes >= 1)
                minutesStr = minutes + " " + ((minutes > 1) ? "minutes" : "minute");
            else if (hours <= 0)
                minutesStr = "Less than 1 minute";
            else
                minutesStr = "less that 1 minute";

            if (hours <= 0)
                str = minutesStr;
            else if (minutes <= 0)
                str = hoursStr;
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

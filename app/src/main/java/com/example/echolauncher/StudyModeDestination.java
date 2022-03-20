package com.example.echolauncher;

import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;

import androidx.gridlayout.widget.GridLayout;

import java.util.Timer;
import java.util.TimerTask;

public class StudyModeDestination {
    public static void setup(Activity activity) {
        remainingTextView = activity.findViewById(R.id.remainingTime);

        GridLayout allowedApps = activity.findViewById(R.id.allowedApps);
        allowedApps.getLayoutParams().height = (int) (Globals.APP_HEIGHT * 1.5f);
        StudyMode.updateGrid(allowedApps,false);

        updateText();

        updateTimer = new Timer();
        // Task that updates the time remaining
        updateTask = new TimerTask() {
            @Override
            public void run() {
                // Time is up, return to home screen
                if (StudyMode.remainingTime() <= 0) {
                    Intent intent = new Intent(activity, activity.getClass());
                    StudyMode.disable();
                    updateTimer.cancel();

                    activity.finish();
                    activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    activity.startActivity(intent);
                    return;
                }

                updateText();
            }
        };

        // Every minute run the updateTask
        updateTimer.schedule(updateTask, 1000,60000);
    }

    private static void updateText() {
        // Calculate the remaining time from milliseconds to hours
        // and minutes
        long remainingTime = StudyMode.remainingTime(),
                remainingHours = remainingTime / (60 * 60 * 1000),
                remainingMinutes = (remainingTime - (remainingHours * 60 * 60 * 1000)) / (60 * 1000);

        remainingTextView.setText(String.format("%s remaining",
                TimeSetter.NaturalLanguage.get((int) remainingHours, (int) remainingMinutes)));
    }

    private static Timer updateTimer;
    private static TimerTask updateTask;
    private static TextView remainingTextView;
}

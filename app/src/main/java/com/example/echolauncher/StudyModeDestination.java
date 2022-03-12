package com.example.echolauncher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.gridlayout.widget.GridLayout;

public class StudyModeDestination {
    public static void setup(Activity activity) {
        TextView remaining = activity.findViewById(R.id.remainingTime);

        GridLayout allowedApps = activity.findViewById(R.id.allowedApps);
        allowedApps.getLayoutParams().height = (int) (Globals.appHeight * 1.5f);
        StudyMode.updateGrid(allowedApps, false);

        updateThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (remaining != null) {
                        long remainingTime = StudyMode.remainingTime(),
                                remainingHours = remainingTime / (60 * 60 * 1000),
                                remainingMinutes = (remainingTime - (remainingHours * 60 * 60 *1000)) / (60 * 1000);

                        if (remainingTime <= 0) {
                            Intent intent = new Intent(activity, activity.getClass());
                            StudyMode.disable();
                            activity.finish();
                            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            activity.startActivity(intent);
                            break;
                        }

                        hours = remainingHours + " " + ((remainingHours > 1) ? "hours" : "hour");
                        if (remainingMinutes >= 1)
                            minutes = remainingMinutes + " " + ((remainingMinutes > 1) ? "minutes" : "minute");
                        else if (remainingHours <= 0)
                            minutes = "Less than 1 minute";
                        else
                            minutes = "less that 1 minute";

                        if (remainingHours <= 0)
                            remaining.setText(String.format("%s remaining", minutes));
                        else if (remainingMinutes <= 0)
                            remaining.setText(String.format("%s remaining", hours));
                        else
                            remaining.setText(String.format(
                                    "%s and %s remaining",
                                    hours, minutes));

                        // Sleep for a minute
                        try {
                            sleep(60000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };

        updateThread.start();
    }

    private static Thread updateThread;
    private static String hours, minutes;
}

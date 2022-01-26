package com.example.echolauncher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

public class StudyModeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_mode);

        TextView remaining = findViewById(R.id.remainingTime);

        GridLayout allowedApps = findViewById(R.id.allowedApps);
        allowedApps.getLayoutParams().height = (int) (Globals.appHeight * 1.5f);
        StudyModeManager.updateGrid(allowedApps);

        updateThread = new Thread() {
            @Override
            public void run() {
                while (enabled) {
                    if (remaining != null) {
                        long remainingMinutes = StudyModeManager.remainingTime() / (60 * 1000);

                        minutes = Long.toString(remainingMinutes);
                        seconds = Long.toString((StudyModeManager.remainingTime() - (remainingMinutes * 60 * 1000))
                                / (1000));

                        if (minutes.length() < 2)
                            minutes = "0" + minutes;

                        if (seconds.length() < 2)
                            seconds = "0" + seconds;

                        remaining.setText(String.format("%s:%s", minutes, seconds));
                    }
                }
            }
        };

        updateThread.start();
    }

    public static boolean enabled = false;

    private Thread updateThread;
    private String minutes, seconds;
}
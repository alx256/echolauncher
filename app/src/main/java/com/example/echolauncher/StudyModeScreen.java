package com.example.echolauncher;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class StudyModeScreen {
    @NonNull
    public static View get(Context context) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.study_mode_screen, null, false);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        Button start = view.findViewById(R.id.startButton);

        LinearLayout layout = view.findViewById(R.id.mainLayout);
        layout.setY(Globals.statusBarHeight);

        TextView timeView = view.findViewById(R.id.timeView);
        TimeSetter timeSetter = new TimeSetter(timeView, context);

        // Set hours and minutes to the values
        // chosen by the TimeSetter
        timeSetter.setOnFinishListener((h, m) -> {
            hours = h;
            minutes = m;
        });

        DropTarget target = view.findViewById(R.id.allowedApps);
        target.getLayoutParams().height = (int) (Globals.APP_HEIGHT * 1.5f);
        // When an app is dropped on this
        // DropTarget, add it to the allowed
        // apps list and display it in
        // the view
        target.setOnDropListener(item -> {
            if (item instanceof WidgetItem) {
                // Pinning widgets is not allowed
                return;
            }

            StudyMode.addAllowedApp((AppItem) item);
            StudyMode.updateDropTarget();
        });

        DropTarget delete = view.findViewById(R.id.delete);
        delete.getLayoutParams().width = Globals.APP_ICON_WIDTH;
        delete.getLayoutParams().height = (int) (Globals.APP_HEIGHT * 1.5f);
        delete.setOnDropListener(StudyMode::removeAllowedApp);

        StudyMode.setDropTarget(target);
        StudyMode.updateDropTarget();

        // Message to be displayed if a duration
        // has not been set
        Toast message = Toast.makeText(context,
                "Need to set duration!",
                Toast.LENGTH_LONG);

        // If an hour or minute value has been entered
        // then start StudyMode
        start.setOnClickListener(v -> {
            if (hours > 0 || minutes > 0)
                StudyMode.enable(hours, minutes, (Activity) context);
            else
                message.show();
        });

        return view;
    }

    private static int hours, minutes;
}

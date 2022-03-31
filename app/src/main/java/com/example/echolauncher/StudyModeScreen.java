package com.example.echolauncher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Handles the logic of the study mode
 * configuration screen fragment
 */

public class StudyModeScreen extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.study_mode_screen, null, false);

        Button start = view.findViewById(R.id.startButton);

        LinearLayout layout = view.findViewById(R.id.mainLayout);
        layout.setY(Globals.statusBarHeight);

        TextView timeView = view.findViewById(R.id.timeView);
        TimeSetter timeSetter = new TimeSetter(timeView, getContext());

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
        Toast message = Toast.makeText(getContext(),
                "Need to set duration!",
                Toast.LENGTH_LONG);

        // If an hour or minute value has been entered
        // then start StudyMode
        start.setOnClickListener(v -> {
            if (hours > 0 || minutes > 0)
                StudyMode.enable(hours, minutes, getActivity());
            else
                message.show();
        });

        return view;
    }

    private int hours, minutes;
}

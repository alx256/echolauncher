package com.example.echolauncher;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

public class StudyModeScreen extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.study_mode_screen, null, false);

        Button start = view.findViewById(R.id.startButton);

        LinearLayout layout = view.findViewById(R.id.masterLayout);
        layout.setY(Globals.statusBarHeight);

        TextView timeView = view.findViewById(R.id.timeView);
        TimeSetter timeSetter = new TimeSetter(timeView, getContext());

        timeSetter.setOnFinishListener(new TimeSetter.OnFinishListener() {
            @Override
            public void onFinish(int h, int m) {
                hours = h;
                minutes = m;
            }
        });

        DropTarget target = view.findViewById(R.id.allowedApps);
        target.getLayoutParams().height = (int) (Globals.appHeight * 1.5f);
        target.setOnDropListener(item -> {
            StudyMode.addAllowedApp((AppItem) item);
            StudyMode.updateDropTarget();
        });

        DropTarget delete = view.findViewById(R.id.delete);
        delete.getLayoutParams().width = Globals.appIconWidth;
        delete.getLayoutParams().height = (int) (Globals.appHeight * 1.5f);
        delete.setOnDropListener(StudyMode::removeAllowedApp);

        StudyMode.setDropTarget(target);
        StudyMode.updateDropTarget();

        Toast message = Toast.makeText(getContext(),
                "Need to set duration!",
                Toast.LENGTH_LONG);

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

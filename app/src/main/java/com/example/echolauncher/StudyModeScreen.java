package com.example.echolauncher;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

public class StudyModeScreen extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.study_mode_screen, null, false);

        TextInputEditText minutesInput = view.findViewById(R.id.minutesInput);
        minutesInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable editable) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.toString().isEmpty()) {
                    minutes = 0;
                    return;
                }

                minutes = Integer.valueOf(charSequence.toString());
            }
        });

        TextInputEditText secondsInput = view.findViewById(R.id.secondsInput);
        secondsInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable editable) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.toString().isEmpty()) {
                    seconds = 0;
                    return;
                }

                seconds = Integer.valueOf(charSequence.toString());
            }
        });

        Button start = view.findViewById(R.id.startButton);

        LinearLayout layout = view.findViewById(R.id.masterLayout);
        layout.setY(Globals.statusBarHeight);

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

        start.setOnClickListener(v -> {
            if (minutes > 0 || seconds > 0)
                StudyMode.enable(minutes, seconds, getContext());
        });

        return view;
    }

    private int minutes, seconds;
}

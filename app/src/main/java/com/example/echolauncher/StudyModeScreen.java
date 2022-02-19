package com.example.echolauncher;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Debug;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.gridlayout.widget.GridLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class StudyModeScreen extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.study_mode_screen, null, false);

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
        target.setOnDropListener(new DropTarget.OnDropListener() {
            @Override
            public void onDrop(PinItem item) {
                StudyModeManager.addAllowedApp((AppItem) item);
                StudyModeManager.updateDropTarget();
            }
        });

        DropTarget delete = view.findViewById(R.id.delete);
        delete.getLayoutParams().width = Globals.appIconWidth;
        delete.getLayoutParams().height = (int) (Globals.appHeight * 1.5f);
        delete.setOnDropListener(new DropTarget.OnDropListener() {
            @Override
            public void onDrop(PinItem item) {
                StudyModeManager.removeAllowedApp(item);
            }
        });

        StudyModeManager.setDropTarget(target);
        StudyModeManager.updateDropTarget();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (minutes > 0 || seconds > 0)
                    StudyModeManager.enable(minutes, seconds, getContext());
            }
        });

        return view;
    }

    private View view, crossView;
    private int minutes = 0, seconds = 0;
    private List<AppItem> pinned;
}
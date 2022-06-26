package com.example.echolauncher.widget_drawer.timetable_widget;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.echolauncher.R;
import com.example.echolauncher.utilities.Storage;
import com.example.echolauncher.utilities.TimeSetter;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;

/**
 * Dialog that is shown when the
 * user taps on the timetable widget,
 * allowing them to add and remove
 * timetable events
 */

public class TimetableDialog extends DialogFragment {
    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        storage = new Storage(getContext(),
                "name,year,month,day,hours,minutes,dateMillis",
                "echolauncher_timetable");

        View view = inflater.inflate(R.layout.timetable_dialog, container);

        // Retrieve necessary UI elements
        ViewFlipper flipper = view.findViewById(R.id.eventsViewFlipper);
        View eventList = flipper.findViewById(R.id.eventList),
            eventNew = flipper.findViewById(R.id.eventNew);
        GridView eventsGridView = eventList.findViewById(R.id.eventsGridView);
        Button addNewButton = eventList.findViewById(R.id.addNewButton),
            doneButton = eventList.findViewById(R.id.doneButton),
            clearButton = eventList.findViewById(R.id.clearButton),
            finishButton = eventNew.findViewById(R.id.finishButton);
        TextView eventTimeText = eventNew.findViewById(R.id.eventTimeText);
        CalendarView calendarView = eventNew.findViewById(R.id.calendarView);
        TextInputEditText eventNameInput = eventNew.findViewById(R.id.eventNameInput);

        // Add animation
        flipper.setInAnimation(getContext(), android.R.anim.slide_in_left);
        flipper.setOutAnimation(getContext(), android.R.anim.slide_out_right);

        addNewButton.setOnClickListener(v -> {
            flipper.showNext();
            eventNameInput.setText("");
            eventTimeText.setText("Tap here to set time");
            // Don't allow the user to set dates before the current date
            calendarView.setMinDate(System.currentTimeMillis() - (24 * 60 * 60 * 1000 * 10));
            calendarView.clearFocus();
        });
        doneButton.setOnClickListener(v -> dismiss());
        clearButton.setOnClickListener(v -> {
            // Reset
            storage.clear();
            eventsGridView.setAdapter(new TimetableListAdapter(getContext()));
        });

        eventsGridView.setAdapter(new TimetableListAdapter(getContext()));

        TimetableSlots.read(storage);

        TimeSetter setter = new TimeSetter(eventTimeText, getContext());
        setter.setOnFinishListener((h, m) -> {
            hours = h;
            minutes = m;
        });

        calendarView.setOnDateChangeListener((view1, y, m, d) -> {
            year = y;
            month = m;
            day = d;
            dateMillis = calendarView.getDate();
        });

        eventNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                name = s.toString();
            }
        });

        finishButton.setOnClickListener(v -> {
            if (year <= 0 && month <= 0 && day <= 0) {
                Toast.makeText(getContext(),
                        "Invalid date!",
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (hours <= 0 && minutes <= 0) {
                Toast.makeText(getContext(),
                        "Invalid duration!",
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (name == null || name.isEmpty()) {
                Toast.makeText(getContext(),
                        "Empty name!",
                        Toast.LENGTH_LONG).show();
                return;
            }

            TimetableSlots.addNew(new TimetableSlot(name, year, month, day, hours, minutes, dateMillis));
            flipper.showPrevious();
            try {
                storage.writeItem(name, year, month, day, hours, minutes, dateMillis);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return view;
    }

    private int hours, minutes, year, month, day;
    private long dateMillis;
    private String name;
    private Storage storage;
}

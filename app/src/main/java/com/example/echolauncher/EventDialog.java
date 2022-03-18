package com.example.echolauncher;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
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

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;

public class EventDialog extends DialogFragment {
    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        storage = new Storage(getContext(),
                "name,year,month,day,hours,minutes",
                "echolauncher_events");

        View view = inflater.inflate(R.layout.event_dialog, container);

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

        flipper.setInAnimation(getContext(), android.R.anim.slide_in_left);
        flipper.setOutAnimation(getContext(), android.R.anim.slide_out_right);

        addNewButton.setOnClickListener(v -> {
            flipper.showNext();
            eventNameInput.setText("");
            eventTimeText.setText("Tap here to set time");
            // Don't allow the user to set dates before the current date
            calendarView.setMinDate(System.currentTimeMillis());
            calendarView.clearFocus();
        });
        doneButton.setOnClickListener(v -> dismiss());
        clearButton.setOnClickListener(v -> {
            // Reset
            storage.clear();
            eventsGridView.setAdapter(new EventListAdapter(getContext(), storage));
        });

        eventsGridView.setAdapter(new EventListAdapter(getContext(), storage));

        TimeSetter setter = new TimeSetter(eventTimeText, getContext());
        setter.setOnFinishListener((h, m) -> {
            hours = h;
            minutes = m;
        });

        calendarView.setOnDateChangeListener((view1, y, m, d) -> {
            year = y;
            month = m;
            day = d;
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

            EventListAdapter.addNew(new Event(name, year, month, day, hours, minutes));
            flipper.showPrevious();
            try {
                storage.writeItem(name, year, month, day, hours, minutes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return view;
    }

    private int hours, minutes, year, month, day;
    private String name;
    private Storage storage;
}

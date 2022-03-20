package com.example.echolauncher;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

public class TimetableListAdapter extends BaseAdapter {
    public TimetableListAdapter(Context context) {
        super();

        CONTEXT = context;
    }

    @Override
    public int getCount() {
        return TimetableSlots.getAll().size();
    }

    @Override
    public Object getItem(int position) {
        return TimetableSlots.getAll().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TimetableSlot timetableSlot = TimetableSlots.get(position);
        LinearLayout layout = new LinearLayout(CONTEXT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 0, 30, 0);
        layout.setDividerPadding(30);
        layout.addView(timetableSlot.getNameTextView(CONTEXT));
        layout.addView(timetableSlot.getDateTextView(CONTEXT));
        layout.addView(timetableSlot.getTimeRemainingTextView(CONTEXT));
        return layout;
    }

    private final Context CONTEXT;
}

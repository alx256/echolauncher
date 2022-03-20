package com.example.echolauncher;

public class TimetableWidget extends Widget {
    TimetableWidget() {
        super.identifier = "widget.timetable";
        super.color = 0xFFB3FFB3;;
    }

    @Override
    public void tick() {
        super.textPositions.put('L', TimetableSlots.getNext());
    }

    @Override
    public void onTap() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            TimetableDialog dialog = new TimetableDialog();
            dialog.show(Dispatcher.getSavedFragmentManager(), "TimetableWidget");
        }
    }
}

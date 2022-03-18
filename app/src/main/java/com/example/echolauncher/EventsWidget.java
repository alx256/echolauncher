package com.example.echolauncher;

public class EventsWidget extends Widget {
    EventsWidget() {
        super.identifier = "widget.event";
        super.color = 0xFFB3FFB3;;
    }

    @Override
    public void tick() {
        super.textPositions.put('L', EventListAdapter.getNext());
    }

    @Override
    public void onTap() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            EventDialog dialog = new EventDialog();
            dialog.show(Dispatcher.getSavedFragmentManager(), "Event Widget");
        }
    }
}

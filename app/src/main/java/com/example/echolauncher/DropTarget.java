package com.example.echolauncher;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.View;
import android.widget.TextView;

import androidx.gridlayout.widget.GridLayout;

public class DropTarget extends GridLayout {
    public interface OnDropListener {
        void onDrop(PinItem item);
    }

    public DropTarget(Context context) {
        super(context);
        init();
    }

    public DropTarget(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public void setOnDropListener(OnDropListener listener) {
        this.listener = listener;
    }

    public void setBackgroundText(String text) {
        TextView infoTextView = new TextView(getContext());
        LayoutParams params = new LayoutParams();
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.MATCH_PARENT;

        infoTextView.setText(text);
        infoTextView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        infoTextView.setLayoutParams(params);
        infoTextView.setTextSize(20.0f);
        addView(infoTextView);
    }

    public void removeBackgroundText() {
        View text = findViewWithTag("infoTextView");

        if (text != null)
            removeView(text);
    }

    private void init() {
        setBackgroundColor(Color.GRAY);

        super.setOnDragListener((view, event) -> {
            if (event.getAction() == DragEvent.ACTION_DRAG_ENTERED)
                setBackgroundColor(Color.DKGRAY);

            if (event.getAction() == DragEvent.ACTION_DRAG_EXITED)
                setBackgroundColor(Color.GRAY);

            if (event.getAction() == DragEvent.ACTION_DROP) {
                setBackgroundColor(Color.GRAY);
                listener.onDrop(Library.getDragging());
            }

            return true;
        });
    }

    private OnDropListener listener;
}

package com.example.echolauncher;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;

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

    public void setOnDropListener(OnDropListener onDropListener) {
        this.onDropListener = onDropListener;
    }

    private void init() {
        setBackgroundColor(Color.GRAY);

        super.setOnDragListener(new OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent event) {
                if (event.getAction() == DragEvent.ACTION_DRAG_ENTERED)
                    setBackgroundColor(Color.DKGRAY);

                if (event.getAction() == DragEvent.ACTION_DRAG_EXITED)
                    setBackgroundColor(Color.GRAY);

                if (event.getAction() == DragEvent.ACTION_DROP) {
                    setBackgroundColor(Color.GRAY);
                    onDropListener.onDrop(InstalledAppsManager.dragging);
                }

                return true;
            }
        });
    }

    private OnDropListener onDropListener;
    private boolean droppable;
}

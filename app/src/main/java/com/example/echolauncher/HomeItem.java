package com.example.echolauncher;

import android.content.Context;
import android.graphics.Color;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InvalidObjectException;

/**
 * Empty item that occupies home
 * screen grid location if another
 * item has not been dropped there
 */

public class HomeItem extends Item {
    public HomeItem() {
        super.isEmpty = true;
        super.imageHeight = Globals.APP_ICON_HEIGHT;
        super.imageWidth = Globals.APP_ICON_WIDTH;
        super.textHeight = Globals.APP_TEXT_HEIGHT;
        super.textSize = Globals.APP_TEXT_SIZE;
        super.isWidget = false;
        super.isDuplicable = false;
    }

    public View toView(Context context) {
        View finalView;
        super.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        finalView = inflater.inflate(R.layout.item, null, false);

        ImageView imageView = finalView.findViewById(R.id.appIcon);
        imageView.getLayoutParams().width = imageWidth;
        imageView.getLayoutParams().height = imageHeight;

        TextView textView = finalView.findViewById(R.id.textView);
        textView.setTextSize(textSize);

        finalView.setOnDragListener((view, dragEvent) -> {
            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DRAG_ENTERED:
                    updateGrid(HomeScreenGridAdapter.Instruction.HOVER);
                    // The user is hovering, so don't display the unpin effect
                    Pages.displayUnpinEffect(view, Color.TRANSPARENT);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    updateGrid(HomeScreenGridAdapter.Instruction.CLEAR);
                    // The user is no longer hovering
                    // Display the unpin effect so that if the user does not
                    // hover over another HomeItem instead, the effect will
                    // occur
                    Pages.displayUnpinEffect(view,
                            context.getResources().getColor(R.color.transparent_gray_select));
                    break;
                case DragEvent.ACTION_DROP:
                    updateGrid(HomeScreenGridAdapter.Instruction.CLEAR);
                    updateGrid(HomeScreenGridAdapter.Instruction.PIN);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    Pages.displayUnpinEffect(view, Color.TRANSPARENT);
                    break;
            }

            return true;
        });

        return finalView;
    }

    private void updateGrid(HomeScreenGridAdapter.Instruction instruction) {
        try {
            Pages.doInstruction(getGridIndex(), getPageNumber(), instruction, Library.getDragging());
        } catch (InvalidObjectException e) {
            e.printStackTrace();
        }
    }
}

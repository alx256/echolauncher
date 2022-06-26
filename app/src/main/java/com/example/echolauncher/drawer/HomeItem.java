package com.example.echolauncher.drawer;

import android.content.Context;
import android.graphics.Color;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.echolauncher.R;
import com.example.echolauncher.apps.Library;
import com.example.echolauncher.home_screen.HomeScreenGridAdapter;
import com.example.echolauncher.home_screen.Pages;
import com.example.echolauncher.utilities.Globals;

import java.io.InvalidObjectException;

/**
 * Empty item that occupies home
 * screen grid location if another
 * item has not been dropped there
 */

public class HomeItem extends Item {
    public HomeItem() {
        isEmpty = true;
        imageHeight = Globals.APP_ICON_HEIGHT;
        imageWidth = Globals.APP_ICON_WIDTH;
        textHeight = Globals.APP_TEXT_HEIGHT;
        textSize = Globals.APP_TEXT_SIZE;
        isWidget = false;
        isDuplicable = false;
    }

    public View toView(Context context) {
        ConstraintLayout layout = new ConstraintLayout(context);
        layout.setId(R.id.constraintLayout);
        super.context = context;

        ImageView imageView = new ImageView(context);
        View genericView = getGenericView();
        measureGenericView();
        imageView.setLayoutParams(new ViewGroup.LayoutParams(genericView.getMeasuredWidth(),
                genericView.getMeasuredHeight()));
        imageView.setId(R.id.imageView);
        layout.addView(imageView);

        layout.setOnDragListener((view, dragEvent) -> {
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

        return layout;
    }

    private void updateGrid(HomeScreenGridAdapter.Instruction instruction) {
        try {
            Pages.doInstruction(getGridIndex(), getPageNumber(), instruction, Library.getDragging());
        } catch (InvalidObjectException e) {
            e.printStackTrace();
        }
    }
}

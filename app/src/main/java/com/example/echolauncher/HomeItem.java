package com.example.echolauncher;

import android.content.Context;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeItem extends PinItem {
    public HomeItem() {
        super.isEmpty = true;
        super.imageHeight = Globals.appIconHeight;
        super.imageWidth = Globals.appIconWidth;
        super.textHeight = Globals.appTextHeight;
        super.textSize = Globals.appTextSize;
        super.isHomeScreen = true;
        super.isWidget = false;
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

        finalView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DRAG_ENTERED:
                        updateGrid(HomeScreenGridAdapter.Instruction.HOVER);
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        updateGrid(HomeScreenGridAdapter.Instruction.CLEAR);
                        break;
                    case DragEvent.ACTION_DROP:
                        updateGrid(HomeScreenGridAdapter.Instruction.CLEAR);
                        updateGrid(HomeScreenGridAdapter.Instruction.PIN);
                        break;
                }

                return true;
            }
        });

        return finalView;
    }

    private void updateGrid(HomeScreenGridAdapter.Instruction instruction) {
        HomeScreenGrid.updateGrid(getGridIndex(), instruction, Library.getDragging());
    }
}

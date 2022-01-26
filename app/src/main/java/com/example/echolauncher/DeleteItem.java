package com.example.echolauncher;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class DeleteItem extends AppItem {
    public DeleteItem() {
        super();
//        super.setOnDropListener(new OnDropListener() {
//            @Override
//            public void onDrop(PinItem item) {
//            }
//        });
    }

    public View toView(Context context) {
        View finalView;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        finalView = inflater.inflate(R.layout.item_delete, null, false);
        ImageView cross = finalView.findViewById(R.id.deleteCross);
        cross.getLayoutParams().width = Globals.appIconWidth;
        cross.getLayoutParams().height = Globals.appHeight;

        return finalView;
    }
}

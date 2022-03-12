package com.example.echolauncher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class AppItem extends PinItem {
    public AppItem(Name name, String packageName) {
        super.name = name;
        super.identifier = packageName;
        super.drawable = Library.getDrawable(packageName);
        setSuper();

        empty = false;
    }

    private void setSuper() {
        super.imageHeight = Globals.appIconHeight;
        super.imageWidth = Globals.appIconWidth;
        super.textHeight = Globals.appTextHeight;
        super.textSize = Globals.appTextSize;
        super.isWidget = false;
    }

    public View toView(Context context) {
        View finalView;
        super.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        finalView = inflater.inflate(R.layout.item, null, false);
        super.initView(finalView);

        return finalView;
    }
}

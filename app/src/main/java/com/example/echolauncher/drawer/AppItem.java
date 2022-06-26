package com.example.echolauncher.drawer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.echolauncher.utilities.Globals;
import com.example.echolauncher.R;
import com.example.echolauncher.apps.Library;
import com.example.echolauncher.home_screen.HomeScreenGridAdapter;
import com.example.echolauncher.home_screen.Pages;

import java.io.InvalidObjectException;

/**
 * Item that represents an app
 */

public class AppItem extends Item {
    public AppItem(Name name, String packageName) {
        super.name = name;
        identifier = packageName;
        drawable = Library.getDrawable(packageName);
        imageHeight = Globals.APP_ICON_HEIGHT;
        imageWidth = Globals.APP_ICON_WIDTH;
        textHeight = Globals.APP_TEXT_HEIGHT;
        textSize = Globals.APP_TEXT_SIZE;
        isWidget = false;
        isEmpty = false;
        isDuplicable = true;
    }

    @Override
    public View toView(Context context) {
        super.context = context;

        if (specificView != null)
            return specificView;

        specificView = new ImageView(context);

        convertViewToBitmap();

        specificView.setLayoutParams(new ViewGroup.LayoutParams(bitmap.getWidth(), bitmap.getHeight()));
        specificView.setImageBitmap(bitmap);
        specificView.setOnTouchListener(getOnTouchListener());
        specificView.setOnDragListener(getOnDragListener());

        return specificView;
    }

    @Override
    protected void onTap() {
        PackageManager packageManager = Library.getPackageManager();
        Log.d("AppAdapter", "Opening " + identifier + "...");
        Intent intent = packageManager.getLaunchIntentForPackage(identifier);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        // This is extremely unlikely to happen
        // but, just in case it does, display
        // a debugging message
        if (intent == null) {
            Log.d("AppAdapter", "Intent was null");
            return;
        }

        // Open the app with a custom animation
        Activity activity = (Activity) context;
        activity.startActivity(intent);
        activity.finish();
        activity.overridePendingTransition(R.transition.open_app, 0);
    }

    @Override
    protected void onMove() {
        if (!isDuplicable) {
            try {
                Pages.doInstruction(getGridIndex(),
                        getPageNumber(),
                        HomeScreenGridAdapter.Instruction.REMOVE,
                        Library.getDragging());
            } catch (InvalidObjectException e) {
                e.printStackTrace();
            }
        }
    }

    private void convertViewToBitmap() {
        if (bitmap != null)
            return;

        View genericView = getGenericView();

        ImageView imageView = genericView.findViewById(R.id.appIcon);
        TextView textView = genericView.findViewById(R.id.textView);

        imageView.setImageDrawable(drawable);
        textView.setText(name.shortened());

        measureGenericView();
        bitmap = Bitmap.createBitmap(genericView.getMeasuredWidth(),
                genericView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        genericView.layout(0, 0, genericView.getMeasuredWidth(),
                genericView.getMeasuredHeight());
        genericView.draw(canvas);
    }

    private Bitmap bitmap;
    private ImageView specificView;
}

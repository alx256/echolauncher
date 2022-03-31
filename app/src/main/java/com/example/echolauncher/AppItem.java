package com.example.echolauncher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

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
    }

    @Override
    public View toView(Context context) {
        View finalView;
        super.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        finalView = inflater.inflate(R.layout.item, null, false);
        super.initView(finalView);

        return finalView;
    }

    @Override
    protected void onTap() {
        PackageManager packageManager = Library.getPackageManager();
        Log.d("AppAdapter", "Opening " + identifier + "...");
        Intent intent = packageManager.getLaunchIntentForPackage(identifier);

        // This is extremely unlikely to happen
        // but, just in case it does, display
        // a debugging message
        if (intent == null) {
            Log.d("AppAdapter", "Intent was null");
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            return;
        }

        // Open the app with a custom animation
        Activity activity = (Activity) context;
        activity.startActivity(intent);
        activity.finish();
        activity.overridePendingTransition(R.transition.open_app, 0);
    }
}

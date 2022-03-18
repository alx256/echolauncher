package com.example.echolauncher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

public class AppItem extends PinItem {
    public AppItem(Name name, String packageName) {
        super.name = name;
        super.identifier = packageName;
        super.drawable = Library.getDrawable(packageName);
        setSuper();

        isEmpty = false;
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

    @Override
    protected void onTap() {
        PackageManager packageManager = Library.getPackageManager();
        Log.d("AppAdapter", "Opening " + identifier + "...");
        Intent intent = packageManager.getLaunchIntentForPackage(identifier);

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

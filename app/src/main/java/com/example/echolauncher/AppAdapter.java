package com.example.echolauncher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class AppAdapter extends BaseAdapter {
    public AppAdapter(Context context, List<AppItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() { return items.size(); }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_app, parent, false);
        } else {
            view = convertView;
        }

        ImageView image = view.findViewById(R.id.imageView);
        TextView textView = view.findViewById(R.id.textView);

        image.getLayoutParams().width = 150;
        image.getLayoutParams().height = 150;
        image.setImageDrawable(items.get(position).getIcon());

        textView.setTextSize(12);
        textView.setText(shortened(items.get(position).getName()));

        // Open app when this view is pressed
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageManager packageManager = InstalledAppsManager.getPackageManager();
                String packageName = items.get(position).getPackageName();
                Log.d("AppAdapter", "Opening " + packageName + "...");
                Intent intent = packageManager.getLaunchIntentForPackage(packageName);

                if (intent == null) {
                    Log.d("AppAdapter", "Intent was null");
                    return;
                }

                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                context.startActivity(intent);
            }
        });

        return view;
    }

    private String shortened(String name) {
        // Maximum number of characters before shortening the text
        int maxChars = 10;
        String shortenedString;

        if (name.length() >= maxChars) {
            shortenedString = name.substring(0, maxChars);
            shortenedString += "...";
            return shortenedString;
        }

        return name;
    }

    private Context context;
    private List<AppItem> items;
    private Activity activity;
}
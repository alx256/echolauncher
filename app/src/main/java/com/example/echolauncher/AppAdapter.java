package com.example.echolauncher;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class AppAdapter extends BaseAdapter {
    public AppAdapter(Context context, List<AppItem> items) {
        this.context = context;
        this.items = items;
        stationary = true;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View finalView;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            finalView = inflater.inflate(R.layout.item_app, parent, false);
        } else {
            finalView = convertView;
        }

        image = finalView.findViewById(R.id.imageView);
        textView = finalView.findViewById(R.id.textView);
        linearLayout = finalView.findViewById(R.id.appLayout);

        image.getLayoutParams().height = imageHeight;
        image.getLayoutParams().width = imageWidth;
        textView.setTextSize(textSize);

        AppItem item = items.get(position);

        if (item == null)
            return finalView;

        if (!item.empty)
            setApp(item, finalView);
        else
            setEmpty(item, finalView);

        return finalView;
    }

    private void setTemp(AppItem item, View view) {
        image = view.findViewById(R.id.imageView);
        textView = view.findViewById(R.id.textView);
        image.setImageDrawable(InstalledAppsManager.getDrawable(item.getPackageName()));
        textView.setText(shortened(item.getName()));
    }

    private void clear(View view) {
        image = view.findViewById(R.id.imageView);
        textView = view.findViewById(R.id.textView);
        image.setImageDrawable(null);
        textView.setText("");
    }

    private void setApp(AppItem item, View view) {
        setTemp(item, view);

        // User has done one short tap. Open the app that they have tapped on
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageManager packageManager = InstalledAppsManager.getPackageManager();
                String packageName = item.getPackageName();
                Log.d("AppAdapter", "Opening " + packageName + "...");
                Intent intent = packageManager.getLaunchIntentForPackage(packageName);

                if (intent == null) {
                    Log.d("AppAdapter", "Intent was null");
                    return;
                }

                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                activity = (Activity) context;
                activity.startActivity(intent);
                //activity.finish();
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // User is moving the app. Start dragging the app that they have tapped on
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    ClipData data = ClipData.newPlainText("ECHOLAUNCHER_APPDRAG",
                            item.getPackageName());
                    InstalledAppsManager.shadowBuilder = new AppShadowBuilder(view);
                    view.startDrag(data, InstalledAppsManager.shadowBuilder, view, 0);
                    ScrollManager.scrollTo(ScrollManager.HOME_SCREEN);
                    stationary = false;
                    InstalledAppsManager.dragging = item;

                    if (isHomeScreen)
                        setEmpty(item, view);

                    return true;
                }

                return false;
            }
        });

        // Handle dragging events
        view.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                if (stationary)
                    return true;

                if (dragEvent.getAction() == DragEvent.ACTION_DRAG_ENDED) {
                    ScrollManager.scrollBack();
                    stationary = true;
                }

                return true;
            }
        });
    }

    private void setEmpty(AppItem item, View view) {
        clear(view);

        // Do nothing when touched
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        view.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                ClipData data = dragEvent.getClipData();
                AppItem app = new AppItem();
                if (data != null) {
                    ClipData.Item item = data.getItemAt(0);
                    String packageName = item.getText().toString();
                    app = InstalledAppsManager.get(packageName);
                } else {
                    app = InstalledAppsManager.dragging;
                }

                if (dragEvent.getAction() == DragEvent.ACTION_DRAG_ENTERED) {
//                    InstalledAppsManager.hidden = true;
//                    InstalledAppsManager.shadowBuilder.update();
                    setTemp(app, view);
                } else if (dragEvent.getAction() == DragEvent.ACTION_DRAG_EXITED) {
//                    InstalledAppsManager.hidden = false;
//                    InstalledAppsManager.shadowBuilder.update();
                    clear(view);
                }

                if (dragEvent.getAction() == DragEvent.ACTION_DROP) {
                    setApp(app, view);
                    return true;
                }

                return true;
            }
        });
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

    public boolean isHomeScreen = false;

    private Context context;
    private List<AppItem> items;
    private Activity activity;
    private ImageView image;
    private TextView textView;
    private LinearLayout linearLayout;

    private final int imageHeight = 150, imageWidth = 150, textSize = 12,
            highlight = 0x44000000;
    private boolean stationary;
}
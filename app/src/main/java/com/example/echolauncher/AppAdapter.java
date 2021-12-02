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
        View finalView = items.get(position).toView(context);

//        if (convertView == null) {
//            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            finalView = inflater.inflate(R.layout.item_app, parent, false);
//        } else {
//            finalView = convertView;
//        }

//        image = finalView.findViewById(R.id.imageView);
//        textView = finalView.findViewById(R.id.textView);
//        linearLayout = finalView.findViewById(R.id.appLayout);
//
//        image.getLayoutParams().height = imageHeight;
//        image.getLayoutParams().width = imageWidth;
//        textView.setTextSize(textSize);
//        textView.getLayoutParams().height = textHeight;
//
//        AppItem item = items.get(position);
//
//        if (item == null)
//            return finalView;
//
//        if (!item.empty)
//            setApp(item, finalView);
//        else
//            setEmpty(item, finalView);

        return finalView;
    }

    private Context context;
    private List<AppItem> items;
}
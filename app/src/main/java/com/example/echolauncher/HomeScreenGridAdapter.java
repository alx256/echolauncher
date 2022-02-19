package com.example.echolauncher;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.format.Time;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;

import okhttp3.CertificatePinner;

public class HomeScreenGridAdapter extends RecyclerView.Adapter<HomeScreenGridAdapter.ViewHolder> {
    public enum Type {
        NONE,
        APP,
        WIDGET
    }

    public static class ViewHolder extends  RecyclerView.ViewHolder {
        public ViewHolder(View view, PinItem item) {
            super(view);
            this.item = item;
            this.type = Type.NONE;
        }

        public void setType(Type type) { this.type = type; }

        public PinItem getItem() {
            return item;
        }
        public Type getType() { return type; }

        private PinItem item;
        private Type type;
    }

    public HomeScreenGridAdapter(Context context) {
        this.context = context;

        // Starts off with 1, actual total is calculate once
        // views can be measured
        total = 1;

        layoutWidthApps = Globals.metricsFull.widthPixels / NUM_ROW_APPS;
        layoutWidthWidgets = Globals.metricsFull.widthPixels / NUM_ROW_WIDGETS;

        InstalledAppsManager.homeScreenInstructions = new Hashtable<>();
    }

    @NonNull
    @Override
    public HomeScreenGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        HomeItem item;

        item = new HomeItem();
        item.dragEventStatus = dragEventStatus;

        view = item.toView(context);

        ImageView imageView = view.findViewById(R.id.appIcon);
        TextView textView = view.findViewById(R.id.textView);
        imageView.setImageDrawable(null);
        textView.setText("");

        if (!filled) {
            view.post(new Runnable() {
                @Override
                public void run() {
                    if (view.getMeasuredHeight() > 0) {
                        InstalledAppsManager.gridAdapter.updateTotal(view.getMeasuredHeight());
                        InstalledAppsManager.gridAdapter.notifyItemChanged(0);
                        view.removeCallbacks(this);
                    }
                }
            });
        }

        ViewHolder holder = new ViewHolder(view, item);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder.item != null)
            if (holder.item.getGridIndex() == -1)
                holder.item.setGridIndex(position);

        ImageView imageView = holder.itemView.findViewById(R.id.appIcon);
        TextView textView = holder.itemView.findViewById(R.id.textView);
        Drawable drawable;
        List<Instruction> instructions = InstalledAppsManager.homeScreenInstructions.get(position);

        if (instructions != null) {
            String identifier = InstalledAppsManager.dragging.identifier;
            PinItem item = InstalledAppsManager.get(identifier);

            for (Instruction instruction : instructions) {
                switch (instruction) {
                    case PIN:
                        // Item needs to be pinned to home screen
                        holder.item = item;
                        holder.setType(type(item));

                        setImageViewWidth(imageView, item.imageWidth);

                        drawable = item.drawable;
                        drawable.clearColorFilter();
                        drawable.setAlpha(0xFF);
                        imageView.setImageDrawable(drawable);

                        textView.setText(item.name.shortened());

                        holder.itemView.setOnDragListener(item.getOnDragListener());
                        holder.itemView.setOnTouchListener(item.getOnTouchListener());
                        holder.itemView.setOnClickListener(item.getOnClickListener());
                        break;
                    case HOVER:
                        // Item needs to display the hover effect
                        holder.item = item;
                        holder.setType(type(item));

                        setImageViewWidth(imageView, item.imageWidth);

                        drawable = item.drawable;
                        imageView.setImageDrawable(drawable);
                        imageView.getDrawable().setColorFilter(0, PorterDuff.Mode.DARKEN);
                        imageView.getDrawable().setAlpha(0x62);

                        textView.setText("");
                        break;
                    case CLEAR:
                        // Item needs to be cleared
                        holder.item = new HomeItem();
                        holder.setType(Type.NONE);

                        setImageViewWidth(imageView, Globals.appIconWidth);

                        imageView.setImageDrawable(null);
                        textView.setText("");

                        break;
                }

                ConstraintLayout layout = holder.itemView.findViewById(R.id.constraintLayout);

                if (layout.getLayoutParams().width != layoutWidthApps &&
                        holder.getType() == Type.APP)
                    layout.getLayoutParams().width = layoutWidthApps;

                if (layout.getLayoutParams().width != layoutWidthWidgets &&
                        holder.getType() == Type.WIDGET)
                    layout.getLayoutParams().width = layoutWidthWidgets;
            }

            InstalledAppsManager.homeScreenInstructions.remove(position);
        }
    }

    @Override
    public int getItemCount() {
        return total;
    }

    public void updateTotal(int height) {
        if (height == 0)
            return;

        int columns = Globals.metricsFull.heightPixels / height;
        total = columns * NUM_ROW_APPS;
        filled = true;
    }

    private void setImageViewWidth(ImageView imageView, int width) {
        if (imageView.getLayoutParams().width > layoutWidthApps)
            imageView.getLayoutParams().width = layoutWidthApps;
        else
            imageView.getLayoutParams().width = width;
    }

    private Type type(PinItem item) {
        assert item != null;
        if (item.isWidget)
            return Type.WIDGET;

        return Type.APP;
    }

    public int dragEventStatus = -1;

    private final Context context;
    private final int NUM_ROW_APPS = 4, NUM_ROW_WIDGETS = 2;
    private int total, layoutWidthApps, layoutWidthWidgets;
    private boolean filled = false;
}
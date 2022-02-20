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
import android.view.ViewTreeObserver;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
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

        // Starts off with 1, actual total is calculated once
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

        ViewTreeObserver observer = view.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ConstraintLayout layout = view.findViewById(R.id.constraintLayout);
                layout.getLayoutParams().width = layoutWidthApps;

                ViewTreeObserver temp = view.getViewTreeObserver();
                temp.removeOnGlobalLayoutListener(this);
            }
        });

        return new ViewHolder(view, item);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ConstraintLayout layout = holder.itemView.findViewById(R.id.constraintLayout);
        ViewTreeObserver observer = layout.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                updateItem(holder);

                ViewTreeObserver temp = layout.getViewTreeObserver();
                temp.removeOnPreDrawListener(this);

                return false;
            }
        });
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
    }

    private void setImageViewWidth(ImageView imageView, int width) {
        if (imageView.getLayoutParams().width > layoutWidthApps)
            imageView.getLayoutParams().width = layoutWidthApps;
        else
            imageView.getLayoutParams().width = width;
    }

    private void updateItem(ViewHolder holder) {
        int position = holder.getAdapterPosition();

        if (holder.item != null)
            if (holder.item.getGridIndex() == -1)
                holder.item.setGridIndex(position);

        ImageView imageView = holder.itemView.findViewById(R.id.appIcon);
        TextView textView = holder.itemView.findViewById(R.id.textView);
        Drawable drawable;
        List<InstalledAppsManager.InstructionCollection> instructionCollections
                = InstalledAppsManager.homeScreenInstructions.get(position);

        if (instructionCollections != null) {
            for (InstalledAppsManager.InstructionCollection instructionCollection : instructionCollections) {
                String identifier = instructionCollection.getIdentifier();
                PinItem item = InstalledAppsManager.get(identifier);

                switch (instructionCollection.getInstruction()) {
                    case ADD:
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

                        if (instructionCollection.getInstruction() == Instruction.PIN) {
                            try {
                                HomeScreenStorage.WriteItem(item.identifier, position, 0, context);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

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
                scaleItems(layout, holder);

                InstalledAppsManager.homeScreenInstructions.remove(position);
            }
        }
    }

    private Type type(PinItem item) {
        assert item != null;
        if (item.isWidget)
            return Type.WIDGET;

        return Type.APP;
    }

    private void scaleItems(ConstraintLayout layout, ViewHolder holder) {
        if (layout.getLayoutParams().width != layoutWidthApps &&
                holder.getType() == Type.APP)
            layout.getLayoutParams().width = layoutWidthApps;

        if (layout.getLayoutParams().width != layoutWidthWidgets &&
                holder.getType() == Type.WIDGET)
            layout.getLayoutParams().width = layoutWidthWidgets;
    }

    public int dragEventStatus = -1;

    private final Context context;
    private final int NUM_ROW_APPS = 4, NUM_ROW_WIDGETS = 2;
    private int total, layoutWidthApps, layoutWidthWidgets;
}
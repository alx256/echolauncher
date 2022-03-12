package com.example.echolauncher;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

public class HomeScreenGridAdapter extends RecyclerView.Adapter<HomeScreenGridAdapter.ViewHolder> {
    public enum Instruction {
        PIN,
        ADD,
        HOVER,
        CLEAR
    }

    public enum Type {
        NONE,
        APP,
        WIDGET
    }

    public static class ViewHolder extends  RecyclerView.ViewHolder {
        public ViewHolder(View view, PinItem item) {
            super(view);
            this.item = item;
            type = Type.NONE;
        }

        public void setType(Type type) { type = type; }
        public void setItem(PinItem item) { item = item; }
        public void setGridIndex(int gridIndex) { item.setGridIndex(gridIndex); }

        public PinItem getItem() {
            return item;
        }
        public int getGridIndex() { return item.getGridIndex(); }
        public Type getType() { return type; }

        private PinItem item;
        private Type type;
    }

    public HomeScreenGridAdapter(Context context) {
        CONTEXT = context;

        // Starts off with 1, actual total is calculated once
        // views can be measured
        total = 1;

        LAYOUT_WIDTH_APPS = Globals.metrics.widthPixels / NUM_ROW_APPS;
        LAYOUT_WIDTH_WIDGETS = Globals.metrics.widthPixels / NUM_ROW_WIDGETS;

        HomeScreenGrid.homeScreenInstructions = new Hashtable<>();
    }

    @NonNull
    @Override
    public HomeScreenGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        HomeItem item;

        item = new HomeItem();
        view = item.toView(CONTEXT);

        ImageView imageView = view.findViewById(R.id.appIcon);
        TextView textView = view.findViewById(R.id.textView);
        imageView.setImageDrawable(null);
        textView.setText("");

        ViewTreeObserver observer = view.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ConstraintLayout layout = view.findViewById(R.id.constraintLayout);
                layout.getLayoutParams().width = LAYOUT_WIDTH_APPS;

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

        int columns = Globals.metrics.heightPixels / height;
        total = columns * NUM_ROW_APPS;
    }

    private void setImageViewWidth(ImageView imageView, int width) {
        if (imageView.getLayoutParams().width > LAYOUT_WIDTH_APPS)
            imageView.getLayoutParams().width = LAYOUT_WIDTH_APPS;
        else
            imageView.getLayoutParams().width = width;
    }

    private void updateItem(ViewHolder holder) {
        int position = holder.getAdapterPosition();

        if (holder.getItem() != null)
            if (holder.getGridIndex() == -1)
                holder.setGridIndex(position);

        ImageView imageView = holder.itemView.findViewById(R.id.appIcon);
        TextView textView = holder.itemView.findViewById(R.id.textView);
        Drawable drawable;
        List<HomeScreenGrid.InstructionCollection> instructionCollections
                = HomeScreenGrid.homeScreenInstructions.get(position);

        if (instructionCollections != null) {
            for (HomeScreenGrid.InstructionCollection instructionCollection : instructionCollections) {
                PinItem item = instructionCollection.getItem();

                switch (instructionCollection.getInstruction()) {
                    case ADD:
                    case PIN:
                        // Item needs to be pinned to home screen
                        holder.setItem(item);
                        holder.setType(type(item));

                        setImageViewWidth(imageView, item.imageWidth);

                        drawable = item.drawable;
                        drawable.clearColorFilter();
                        drawable.setAlpha(0xFF);
                        imageView.setImageDrawable(drawable);

                        textView.setText(item.name.shortened());

                        holder.itemView.setOnDragListener(item.getOnDragListener());
                        holder.itemView.setOnTouchListener(item.getOnTouchListener());

                        if (instructionCollection.getInstruction() == Instruction.PIN) {
                            try {
                                HomeScreenStorage.writeItem(item.identifier, position, 0, CONTEXT);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        break;
                    case HOVER:
                        // Item needs to display the hover effect
                        holder.setItem(item);
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
                        holder.setItem(new HomeItem());
                        holder.setType(Type.NONE);

                        setImageViewWidth(imageView, Globals.appIconWidth);

                        imageView.setImageDrawable(null);
                        textView.setText("");

                        break;
                }
                
                ConstraintLayout layout = holder.itemView.findViewById(R.id.constraintLayout);
                scaleItems(layout, holder);

                HomeScreenGrid.homeScreenInstructions.remove(position);
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
        if (layout.getLayoutParams().width != LAYOUT_WIDTH_APPS &&
                holder.getType() == Type.APP)
            layout.getLayoutParams().width = LAYOUT_WIDTH_APPS;

        if (layout.getLayoutParams().width != LAYOUT_WIDTH_WIDGETS &&
                holder.getType() == Type.WIDGET)
            layout.getLayoutParams().width = LAYOUT_WIDTH_WIDGETS;
    }

    private final Context CONTEXT;
    private final int NUM_ROW_APPS = 4, NUM_ROW_WIDGETS = 2,
            LAYOUT_WIDTH_APPS, LAYOUT_WIDTH_WIDGETS;
    private int total;
}

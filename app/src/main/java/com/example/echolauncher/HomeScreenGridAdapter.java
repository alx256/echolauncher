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

import java.util.ArrayList;
import java.util.List;

/**
 * This is the adapter responsible
 * for updating the home screen grid
 * with the necessary apps and widgets
 */

public class HomeScreenGridAdapter extends RecyclerView.Adapter<HomeScreenGridAdapter.ViewHolder> {
    // Available instructions
    public enum Instruction {
        // Pin the item to the screen,
        // called by Items when they
        // are dropped
        PIN,
        // Similar to PIN, but without
        // saving the item to storage
        // as this is called when items
        // are retrieved from storage
        // so this does not need to be
        // done again
        ADD,
        // Display a transparent preview
        // of where the item will be
        // placed
        HOVER,
        // Clear the item
        CLEAR,
        // Clear the item and remove it
        // Used for when an app is moved
        // from one location to another
        REMOVE
    }

    // Required by the adapter
    // Holds a view and the
    // corresponding item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view, Item item) {
            super(view);
            this.item = item;
            isOccupied = false;
        }

        public void setPreview(Item item) {
            this.item = item.clone();
        }

        public void setItem(Item item) {
            this.item = item.clone();
            this.item.setDuplicable(false);
            isOccupied = true;
        }

        public void clearItem() {
            item = new HomeItem();
            isOccupied = false;
        }

        public void setGridIndex(int gridIndex) {
            item.setGridIndex(gridIndex);
        }

        public void setPageNumber(int pageNumber) {
            item.setPageNumber(pageNumber);
        }

        public Item getItem() {
            return item;
        }

        public boolean isOccupied() {
            return isOccupied;
        }

        private Item item;
        private boolean isOccupied;
    }

    public HomeScreenGridAdapter(Context context) {
        // Starts off with 1, actual total is calculated once
        // views can be measured
        int rows = (Globals.metrics.heightPixels
                - Globals.statusBarHeight
                - Globals.navigationBarHeight) / Item.getHeight();
        total = rows * NUM_APPS_PER_ROW;

        LAYOUT_WIDTH_APPS = Globals.metrics.widthPixels / NUM_APPS_PER_ROW;
        LAYOUT_WIDTH_WIDGETS = Globals.metrics.widthPixels / NUM_WIDGETS_PER_ROW;

        occupiedIndices = new ArrayList<>();
        locations = new HomeScreenLocations(context);
    }

    @NonNull
    @Override
    public HomeScreenGridAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        HomeItem item;

        item = new HomeItem();
        item.setPageNumber(pageNumber);
        view = item.toView(parent.getContext());

        ImageView imageView = view.findViewById(R.id.appIcon);
        TextView textView = view.findViewById(R.id.textView);
        imageView.setImageDrawable(null);
        textView.setText("");

        ViewTreeObserver observer = view.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // When app is first launched, set
                // the width to the app width
                ConstraintLayout layout = view.findViewById(R.id.constraintLayout);
                layout.getLayoutParams().width = LAYOUT_WIDTH_APPS;

                // Only needs to be done once, so
                // remove when done
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

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    private void updateItem(ViewHolder holder) {
        // If item is null then something
        // has gone wrong. Make sure that
        // this is not the case
        assert holder.item != null;

        int position = holder.getAdapterPosition();

        holder.setGridIndex(position);
        holder.setPageNumber(pageNumber);

        ImageView imageView = holder.itemView.findViewById(R.id.appIcon);
        TextView textView = holder.itemView.findViewById(R.id.textView);
        Drawable drawable;
        List<HomeScreenGrid.InstructionCollection> instructionCollections
                = Pages.getInstructions(position, pageNumber);

        if (instructionCollections != null) {
            for (HomeScreenGrid.InstructionCollection instructionCollection : instructionCollections) {
                Item item = instructionCollection.getItem().clone();
                item.setGridIndex(position);
                item.setPageNumber(pageNumber);
                Instruction instruction = instructionCollection.getInstruction();

                if (item instanceof WidgetItem) {
                    boolean ignore = false;

                    // Forbid placing on end of line
                    if (occupiedIndices.contains(item.getGridIndex() + 1) &&
                        (item.getGridIndex() - 3) % 4 == 0)
                        ignore = true;

                    // Forbid placing on beginning of line
                    if (occupiedIndices.contains(item.getGridIndex() - 1) &&
                            item.getGridIndex() % 4 == 0)
                        ignore = true;

                    if (ignore) {
                        Pages.removeInstruction(position, pageNumber);
                        continue;
                    }
                }

                if (holder.isOccupied() && instruction != Instruction.REMOVE) {
                    Pages.removeInstruction(position, pageNumber);
                    return;
                }

                switch (instruction) {
                    case ADD:
                    case PIN:
                        // Item needs to be pinned to home screen
                        occupiedIndices.add(item.getGridIndex());

                        holder.setItem(item);

                        drawable = holder.item.drawable;
                        if (holder.item instanceof WidgetItem)
                            drawable.clearColorFilter();
                        // Fully visible
                        holder.item.drawable.setAlpha(0xFF);
                        imageView.setImageDrawable(drawable);

                        if (holder.item instanceof AppItem)
                            textView.setText(holder.item.name.shortened());

                        if (holder.item instanceof WidgetItem)
                            ((WidgetItem) holder.item).addReferenceView(holder.itemView);

                        holder.itemView.setOnTouchListener(holder.item.getOnTouchListener());

                        if (holder.item instanceof WidgetItem) {
                            holder.itemView.getLayoutParams().width = LAYOUT_WIDTH_WIDGETS;
                            imageView.getLayoutParams().width = LAYOUT_WIDTH_WIDGETS;
                        } else {
                            holder.itemView.getLayoutParams().width = LAYOUT_WIDTH_APPS;
                            imageView.getLayoutParams().width = Globals.APP_ICON_WIDTH;
                        }

                        if (instructionCollection.getInstruction() == Instruction.PIN) {
                            // Store this item
                            locations.writeItemToDatabase(holder.item);
                        }

                        break;
                    case HOVER:
                        // Item needs to display the hover effect
                        holder.setPreview(item);

                        if (holder.item instanceof WidgetItem) {
                            holder.itemView.getLayoutParams().width = LAYOUT_WIDTH_WIDGETS;
                            imageView.getLayoutParams().width = LAYOUT_WIDTH_WIDGETS;
                        } else {
                            holder.itemView.getLayoutParams().width = LAYOUT_WIDTH_APPS;
                            imageView.getLayoutParams().width = Globals.APP_ICON_WIDTH;
                        }

                        drawable = holder.item.drawable;
                        imageView.setImageDrawable(drawable);
                        if (holder.item instanceof WidgetItem)
                            imageView.getDrawable().setColorFilter(0, PorterDuff.Mode.DARKEN);
                        imageView.getDrawable().setAlpha(0x62);

                        textView.setText("");

                        break;
                    case REMOVE:
                    case CLEAR:
                        // Item needs to be cleared
                        if (holder.isOccupied)
                            locations.removeItemFromDatabase(holder.item);

                        holder.clearItem();

                        holder.itemView.getLayoutParams().width = LAYOUT_WIDTH_APPS;
                        imageView.getLayoutParams().width = Globals.APP_ICON_WIDTH;

                        imageView.setImageDrawable(null);
                        textView.setText("");

                        break;
                }

                Pages.removeInstruction(position, pageNumber);
            }
        }
    }

    private final int NUM_APPS_PER_ROW = 4, NUM_WIDGETS_PER_ROW = 2,
            LAYOUT_WIDTH_APPS, LAYOUT_WIDTH_WIDGETS;
    private int total, pageNumber;
    private List<Integer> occupiedIndices;
    private HomeScreenLocations locations;
}

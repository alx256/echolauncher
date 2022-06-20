package com.example.echolauncher;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

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

    public HomeScreenGridAdapter(Context context, List<Item> items) {
        CONTEXT = context;

        // Starts off with 1, actual total is calculated once
        // views can be measured
        int rows = (Globals.metrics.heightPixels
                - Globals.statusBarHeight
                - Globals.navigationBarHeight) / Item.getHeight();
        total = rows * Globals.NUM_APPS_PER_ROW;

        occupiedIndices = new ArrayList<>();
        locations = new HomeScreenLocations(context);

        isHovering = false;
    }

    @NonNull
    @Override
    public HomeScreenGridAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        HomeItem item;

        item = new HomeItem();
        item.setPageNumber(pageNumber);
        view = item.toView(parent.getContext());

        ViewTreeObserver observer = view.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // When app is first launched, set
                // the width to the app width
                ConstraintLayout layout = view.findViewById(R.id.constraintLayout);
                layout.getLayoutParams().width = Globals.layoutWidthApps;

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
        holder.item.setGridIndex(position);
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

        // The ImageView belonging to the HomeItem
        // that we are currently working on
        ImageView client = holder.itemView.findViewById(R.id.imageView);
        // The ImageView of the app that is dropped
        // onto the client ImageView (assigned to
        // when each instruction is applied)
        ImageView server;

        List<HomeScreenGrid.InstructionCollection> instructionCollections
                = Pages.getInstructions(position, pageNumber);

        if (instructionCollections != null) {
            for (HomeScreenGrid.InstructionCollection instructionCollection : instructionCollections) {
                Item item = instructionCollection.getItem().clone();
                item.setGridIndex(position);
                item.setPageNumber(pageNumber);
                Instruction instruction = instructionCollection.getInstruction();

                server = (ImageView) item.toView(CONTEXT);

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

                        client.setImageDrawable(server.getDrawable());

                        if (holder.item instanceof WidgetItem)
                            client.clearColorFilter();
                        // Fully visible
                        client.getDrawable().setAlpha(0xFF);

                        if (holder.item instanceof WidgetItem)
                            ((WidgetItem) holder.item).addReferenceView(holder.itemView);

                        client.setOnTouchListener(holder.item.getOnTouchListener());

                        if (holder.item instanceof WidgetItem)
                            client.getLayoutParams().width = Globals.layoutWidthWidgets;
                        else
                            client.getLayoutParams().width = Globals.layoutWidthApps;

                        if (instructionCollection.getInstruction() == Instruction.PIN) {
                            // Store this item
                            locations.writeItemToDatabase(holder.item);
                        }

                        break;
                    case HOVER:
                        // Item needs to display the hover effect
                        holder.setPreview(item);

                        client.setImageDrawable(server.getDrawable());

                        // Slightly transparent
                        client.getDrawable().setAlpha(0x62);

                        if (holder.item instanceof WidgetItem) {
                            client.getLayoutParams().width = Globals.layoutWidthWidgets;
                            client.getDrawable().setColorFilter(0, PorterDuff.Mode.DARKEN);
                        } else {
                            client.getLayoutParams().width = Globals.layoutWidthApps;
                        }
                        break;
                    case REMOVE:
                    case CLEAR:
                        // Item needs to be cleared
                        if (holder.isOccupied)
                            locations.removeItemFromDatabase(holder.item);

                        holder.clearItem();

                        client.setImageDrawable(null);

                        client.getLayoutParams().width = Globals.layoutWidthApps;

                        break;
                }

                Pages.removeInstruction(position, pageNumber);
            }
        }
    }

    private final Context CONTEXT;

    private int total, pageNumber;
    private List<Integer> occupiedIndices;
    private List<Item> items;
    private HomeScreenLocations locations;
    private boolean isHovering;
}

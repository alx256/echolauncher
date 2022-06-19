package com.example.echolauncher;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.List;

/**
 * Adapter that populates the home screen
 * with 10 pages, the first of which
 * is the study mode configuration
 * screen
 */

public class HomeScreenAdapter extends RecyclerView.Adapter<HomeScreenAdapter.ViewHolder> {
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public HomeScreenAdapter(Context context) {
        CONTEXT = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // First item, so we inflate the study mode
        // options screen
        if (viewType == 0)
            return new ViewHolder(StudyModeScreen.get(CONTEXT));

        RecyclerView recyclerView = new RecyclerView(CONTEXT);
        recyclerView.setLayoutParams(
                new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        FlexboxLayoutManager manager = new FlexboxLayoutManager(parent.getContext());
        // Multi-line
        manager.setFlexWrap(FlexWrap.WRAP);
        // Widgets expand along the row
        manager.setFlexDirection(FlexDirection.ROW);
        // Stretch to align items
        manager.setAlignItems(AlignItems.STRETCH);
        // Evenly distribute items
        manager.setJustifyContent(JustifyContent.SPACE_EVENLY);

        // Allow for easier navigation
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(manager);

        recyclerView.setAdapter(Pages.getGridAdapter());

        return new ViewHolder(recyclerView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if ((position - 1) < 0)
            return;

        RecyclerView recyclerView = (RecyclerView) holder.itemView;
        HomeScreenGridAdapter gridAdapter = (HomeScreenGridAdapter) recyclerView.getAdapter();
        HomeScreenGrid grid = Pages.getPage(position - 1);
        List<Item> items = grid.getItems();

        assert gridAdapter != null;
        gridAdapter.setPageNumber(position - 1);

        if (!grid.getItems().isEmpty()) {
            for (Item item : items) {
                grid.updateGrid(item.getGridIndex(),
                        HomeScreenGridAdapter.Instruction.ADD,
                        item);
            }
            grid.clearItems();
        }
    }

    @Override
    public int getItemCount() {
        return Pages.getNumberOfPages();
    }

    @Override
    public int getItemViewType(int position) {
        // A different viewType is returned for
        // the first position to account for the
        // study mode options screen
        if (position == 0)
            return 0;

        return 1;
    }

    private final Context CONTEXT;
}

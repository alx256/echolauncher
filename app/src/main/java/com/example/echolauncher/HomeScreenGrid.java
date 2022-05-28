package com.example.echolauncher;

import android.graphics.Color;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.io.InvalidObjectException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HomeScreenGrid is used to hold a
 * Flexbox layout. This holds apps and
 * widgets in a layout where widgets do
 * not adjust the column size despite being
 * longer
 * */

public class HomeScreenGrid extends Fragment {

    // Contains the Instruction and the item which
    // is having the instruction carried out on
    // it
    public static class InstructionCollection {
        public InstructionCollection(HomeScreenGridAdapter.Instruction instruction, Item item) {
            INSTRUCTION = instruction;
            ITEM = item;
        }

        public HomeScreenGridAdapter.Instruction getInstruction() {
            return INSTRUCTION;
        }

        public Item getItem() {
            return ITEM;
        }

        private final HomeScreenGridAdapter.Instruction INSTRUCTION;
        private final Item ITEM;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_screen_grid, container, false);

        recyclerView = view.findViewById(R.id.homeScreenGrid);

        FlexboxLayoutManager manager = new FlexboxLayoutManager(getContext());
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

        HomeScreenGridAdapter adapter = new HomeScreenGridAdapter(getContext());
        recyclerView.setAdapter(adapter);

        HomeScreenPages.hideActions();

        final long ANIMATION_DURATION = 212;

        // When an item is being dragged, shrink the grid
        // as this is more intuitive for the user
        shrinkAnimation = new ScaleAnimation(1.0f, SHRINK_SCALE,
                1.0f, SHRINK_SCALE,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        shrinkAnimation.setDuration(ANIMATION_DURATION);
        shrinkAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                if (recyclerView.getScaleX() != SHRINK_SCALE &&
                    recyclerView.getScaleY() != SHRINK_SCALE)
                    animation.cancel();
                recyclerView.setScaleX(SHRINK_SCALE);
                recyclerView.setScaleY(SHRINK_SCALE);
            }

            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });


        // When the item has finished being dragged, expand the
        // grid to its normal size
        expandAnimation = new ScaleAnimation(1.0f, 1.0f + (1.0f - SHRINK_SCALE),
                1.0f, 1.0f + (1.0f - SHRINK_SCALE),
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        expandAnimation.setDuration(ANIMATION_DURATION);
        expandAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                if (recyclerView.getScaleX() != 1.0f &&
                    recyclerView.getScaleY() != 1.0f)
                    animation.cancel();

                recyclerView.setScaleX(1.0f);
                recyclerView.setScaleY(1.0f);
            }

            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        // Shrink or expand grid as necessary
        recyclerView.setOnDragListener((view, dragEvent) -> {
            HomeScreenPages.showActions();
            shrink(view);

            if (dragEvent.getAction() == DragEvent.ACTION_DRAG_ENDED) {
                HomeScreenPages.hideActions();
                expand(view);
            }

            return true;
        });

        View unpinLayer = view.findViewById(R.id.unpinLayer);
        unpinLayer.setOnDragListener((v, event) -> {
            if (event.getAction() == DragEvent.ACTION_DRAG_ENTERED)
                unpinLayer.setBackgroundColor(0x44FFFFFF);

            if (event.getAction() == DragEvent.ACTION_DRAG_EXITED ||
                    event.getAction() == DragEvent.ACTION_DRAG_ENDED)
                unpinLayer.setBackgroundColor(Color.TRANSPARENT);

            return true;
        });

        // Open or create the database used to store
        // items that are pinned to the home screen
        locations = new HomeScreenLocations(getContext());

        ViewTreeObserver observer = recyclerView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(0);
                HomeScreenGridAdapter adapter = (HomeScreenGridAdapter) recyclerView.getAdapter();
                adapter.updateTotal(holder.itemView.getMeasuredHeight());
                adapter.notifyItemChanged(0);

                try {
                    // When app is opened, read the stored data and
                    // add the items in their necessary positions
                    locations.readFromDatabase();
                } catch (SQLException | InvalidObjectException e) {
                    e.printStackTrace();
                }

                // This only needs to be carried out once, so
                // delete this listener
                ViewTreeObserver temp = view.getViewTreeObserver();
                temp.removeOnGlobalLayoutListener(this);
            }
        });

        return view;
    }

    public static void updateGrid(int position, HomeScreenGridAdapter.Instruction instruction,
                                  Item item) {
        // If item is null then something
        // has gone wrong. Make sure that
        // this is not the case
        assert item != null;

        // Create homeScreenInstructions if it does not
        // already exist
        if (homeScreenInstructions.get(position) == null)
            homeScreenInstructions.put(position, new ArrayList<>());

        // Add the new instructions
        homeScreenInstructions.get(position).add(new InstructionCollection(instruction, item));
        // Update the necessary item
        recyclerView.getAdapter().notifyItemChanged(position);
    }

    public static Map<Integer, List<InstructionCollection>> getHomeScreenInstructions() {
        return homeScreenInstructions;
    }

    public static void setHomeScreenInstructions(Map<Integer, List<InstructionCollection>> map) {
        homeScreenInstructions = map;
    }

    private void shrink(View view) {
        if (isMultiplePageMode)
            return;

        view.clearAnimation();
        view.startAnimation(shrinkAnimation);
        view.setBackgroundColor(getResources().getColor(R.color.light_gray_background)); // Light grey
        isMultiplePageMode = true;
    }

    private void expand(View view) {
        if (!isMultiplePageMode)
            return;

        view.clearAnimation();
        view.startAnimation(expandAnimation);
        view.setBackgroundColor(Color.TRANSPARENT);
        isMultiplePageMode = false;
    }

    public final static float SHRINK_SCALE = 0.8f;

    private static Map<Integer, List<InstructionCollection>> homeScreenInstructions;
    private View view;
    private Animation shrinkAnimation, expandAnimation;
    private static HomeScreenLocations locations;
    private static boolean isMultiplePageMode;
    private static RecyclerView recyclerView;
}

package com.example.echolauncher;

import android.content.Context;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager2.widget.ViewPager2;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;

public class Pages {
    public static void init(View view, Context context) {
        pages = new ArrayList<>();

        actions = view.findViewById(R.id.actions);
        dots = view.findViewById(R.id.dots);
        dot = dots.findViewById(R.id.dot);
        LinearLayout.LayoutParams dotParams = (LinearLayout.LayoutParams) dot.getLayoutParams();

        // Center the dots (leftMargin and half of the dot's width
        // subtracted to prevent the dots from being slightly to the left)
        dots.setX((Globals.metrics.widthPixels / 2.0f) - (dotParams.width / 2.0f)
                - (dotParams.leftMargin));

        for (int i = 0 ; i <= 10; i++) {
            pages.add(new HomeScreenGrid(context, i));
            dots.addView(getNewDot(dot));
        }

        pager = view.findViewById(R.id.homeScreenPager);
        HomeScreenAdapter adapter = new HomeScreenAdapter(context);
        pager.setAdapter(adapter);

        final float UNPIN_CHECK_THRESHOLD = (Globals.metrics.widthPixels -
                (Globals.metrics.widthPixels * Animations.getShrinkScale())) / 2;

        // Shrink or expand grid as necessary
        pager.setOnDragListener((v, dragEvent) -> {
            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    startMultiplePageMode(v);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    endMultiplePageMode(v);
                    break;
            }

            return true;
        });

        HomeScreenLocations locations = new HomeScreenLocations(context);
        try {
            locations.readFromDatabase();
        } catch (InvalidObjectException e) {
            e.printStackTrace();
        }

        final int BARRIER_PADDING = 10;
        pager.setPadding(0, Globals.statusBarHeight + BARRIER_PADDING, 0, Globals.navigationBarHeight + BARRIER_PADDING);

        // Start on page 1
        pager.setCurrentItem(1, false);

        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            private boolean hasStarted = false;
            private float goal, initialX, lastPositionOffset;
            private int initialPosition, lastPositionOffsetPixels = -1, diff;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (!hasStarted) {
                    initialPosition = pager.getCurrentItem();
                    initialX = dots.getX();
                    goal = dot.getWidth() + dotParams.rightMargin + dotParams.leftMargin;
                    hasStarted = true;
                }

                // Snap into place
                if (positionOffsetPixels == 0) {
                    if (position < initialPosition) {
                        dots.setX(initialX + goal);
                    } else if (position > initialPosition) {
                        dots.setX(initialX - goal);
                    } else {
                        dots.setX(initialX);
                    }

                    hasStarted = false;
                    lastPositionOffsetPixels = -1;
                    lastPositionOffset = 0.0f;

                    super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                    return;
                }

                if (lastPositionOffsetPixels != -1)
                    diff = positionOffsetPixels - lastPositionOffsetPixels;
                else
                    diff = 0;

                if (diff < 0)
                    dots.setX(dots.getX() + (goal * Math.abs(positionOffset - lastPositionOffset)));
                else if (diff > 0)
                    dots.setX(dots.getX() - (goal * Math.abs(positionOffset - lastPositionOffset)));

                lastPositionOffsetPixels = positionOffsetPixels;
                lastPositionOffset = positionOffset;

                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        });

        isMultiplePageMode = false;
        actions.setVisibility(View.INVISIBLE);
    }

    public static void addItem(Item item) {
        int page = item.getPageNumber();
        // TODO: When dynamic page creation is added,
        //  use this method to create a page if the page
        //  does not already exist

        pages.get(page).addItem(item);
    }

    public static void doInstruction(int position, int page,
                                     HomeScreenGridAdapter.Instruction instruction, Item item)
            throws InvalidObjectException {
        if (position == -1)
            throw new InvalidObjectException("Position is invalid! (Index - 1)");
        if (page == -1)
            throw new InvalidObjectException("Page is invalid! (Index -1)");

        pages.get(page).updateGrid(position,
                instruction,
                item);
    }

    public static void removeInstruction(int position, int page) {
        pages.get(page).getHomeScreenInstructions().remove(position);
    }

    public static void displayUnpinEffect(View view, int tint) {
        if (unpinLayer == null)
            unpinLayer = (View) view.getParent().getParent().getParent().getParent();

        ImageView wallpaperView = unpinLayer.findViewById(R.id.wallpaperView);
        assert wallpaperView != null;

        wallpaperView.setColorFilter(tint);
    }

    public static HomeScreenGrid getPage(int index) {
        return pages.get(index);
    }

    public static List<HomeScreenGrid.InstructionCollection> getInstructions(int position,
                                                                             int page) {
        return pages.get(page).getHomeScreenInstructions().get(position);
    }

    public static int getNumberOfPages() {
        return pages.size();
    }

    private static ImageView getNewDot(ImageView original) {
        ImageView view = new ImageView(original.getContext());
        view.setImageDrawable(original.getDrawable());
        view.setLayoutParams(original.getLayoutParams());
        return view;
    }

    private static void startMultiplePageMode(View view) {
        actions.setVisibility(View.VISIBLE);
        dots.setVisibility(View.INVISIBLE);

        if (isMultiplePageMode)
            return;

        Animations.perform(Animations.AnimationName.SHRINK_ANIMATION, view);
        isMultiplePageMode = true;
    }

    private static void endMultiplePageMode(View view) {
        actions.setVisibility(View.INVISIBLE);
        dots.setVisibility(View.VISIBLE);

        if (!isMultiplePageMode)
            return;

        Animations.perform(Animations.AnimationName.EXPAND_ANIMATION, view);
        isMultiplePageMode = false;
    }

    private static List<HomeScreenGrid> pages;
    private static LinearLayout actions, dots;
    private static ImageView dot;
    private static ViewPager2 pager;
    private static boolean isMultiplePageMode;
    private static View unpinLayer;
}

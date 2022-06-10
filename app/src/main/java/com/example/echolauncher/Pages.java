package com.example.echolauncher;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Pages {
    public static void init(View view, FragmentActivity activity) {
        ViewPager2 pager = view.findViewById(R.id.homeScreenPager);
        HomeScreenAdapter adapter = new HomeScreenAdapter(activity);
        pager.setAdapter(adapter);

        final int BARRIER_PADDING = 10;
        pager.setPadding(0, Globals.statusBarHeight + BARRIER_PADDING, 0, Globals.navigationBarHeight + BARRIER_PADDING);

        // Start on page 1
        pager.setCurrentItem(1, false);

        pages = new ArrayList<>();

        LinearLayout actions = view.findViewById(R.id.actions),
            dots = view.findViewById(R.id.dots);
        ImageView dot = dots.findViewById(R.id.dot);
        LinearLayout.LayoutParams dotParams = (LinearLayout.LayoutParams) dot.getLayoutParams();

        for (int i = 0; i <= 10; i++) {
            HomeScreenGrid grid = new HomeScreenGrid(i);
            grid.setActionsReference(actions);
            grid.setDotsReference(dots);
            dots.addView(getNewDot(dot));
            pages.add(grid);
        }

        // Center the dots (leftMargin and half of the dot's width
        // subtracted to prevent the dots from being slightly to the left)
        dots.setX((Globals.metrics.widthPixels / 2.0f) - (dotParams.width / 2.0f)
                - (dotParams.leftMargin));

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

        clearInstructions();
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

    public static void clearInstructions() {
        for (HomeScreenGrid grid : pages)
            grid.setHomeScreenInstructions(new Hashtable<>());
    }

    public static void removeInstruction(int position, int page) {
        pages.get(page).getHomeScreenInstructions().remove(position);
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

    private static List<HomeScreenGrid> pages;
}

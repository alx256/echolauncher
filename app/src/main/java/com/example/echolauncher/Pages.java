package com.example.echolauncher;

import android.view.View;
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

        LinearLayout actions = view.findViewById(R.id.actions);

        for (int i = 0; i <= 10; i++) {
            HomeScreenGrid grid = new HomeScreenGrid(i);
            grid.setActionsReference(actions);
            pages.add(grid);
        }

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

    private static List<HomeScreenGrid> pages;
}

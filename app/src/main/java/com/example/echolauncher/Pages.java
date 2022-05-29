package com.example.echolauncher;

import android.view.View;

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
        final int BARRIER_PADDING = 10;
        pager.setPadding(0, Globals.statusBarHeight + BARRIER_PADDING, 0, Globals.navigationBarHeight + BARRIER_PADDING);
        pager.setAdapter(adapter);
        // Start on page 1
        pager.setCurrentItem(1, false);

        pages = new ArrayList<>();

        pages.add(new HomeScreenGrid(0));
        pages.add(new HomeScreenGrid(1));
        pages.add(new HomeScreenGrid(2));
        pages.add(new HomeScreenGrid(3));
        pages.add(new HomeScreenGrid(4));
        pages.add(new HomeScreenGrid(5));
        pages.add(new HomeScreenGrid(6));

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

    private static List<HomeScreenGrid> pages;
    private static ViewPager2 pager;
}

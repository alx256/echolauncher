package com.example.echolauncher;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * Adapter that populate the home screen
 * with 10 pages, the first of which
 * is the study mode configuration
 * screen
 */

public class HomeScreenAdapter extends FragmentStateAdapter {
    public HomeScreenAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0)
            return new StudyModeScreen();

        return new HomeScreenGrid();
    }

    @Override
    public int getItemCount() {
        return pages;
    }

    private int pages = 10;
}

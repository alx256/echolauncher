package com.example.echolauncher;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

/**
 * This class implements the functionality of the home
 * screen pages fragment which contains the pages where
 * apps and widgets can be pinned
 */

public class HomeScreenPages extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_screen, null, false);

        ViewPager2 pager = view.findViewById(R.id.homeScreenPager);
        HomeScreenAdapter adapter = new HomeScreenAdapter(getActivity());
        final int BARRIER_PADDING = 10;
        pager.setPadding(0, Globals.statusBarHeight + BARRIER_PADDING, 0, Globals.navigationBarHeight + BARRIER_PADDING);
        pager.setAdapter(adapter);
        // Start on page 1
        pager.setCurrentItem(1, false);

        // Get the user's wallpaper
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getContext());
        ImageView wallpaperView = view.findViewById(R.id.wallpaperView);
        // Set the background to the user's wallpaper
        wallpaperView.setImageDrawable(wallpaperManager.getDrawable());

        View statusBarBarrier = view.findViewById(R.id.statusBarBarrier);
        statusBarBarrier.getLayoutParams().height = Globals.statusBarHeight;

        actions = view.findViewById(R.id.actions);

        LinearLayout cancelLayout = actions.findViewById(R.id.cancelLayout),
                deleteLayout = actions.findViewById(R.id.deleteLayout);

        cancelLayout.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_ENTERED:
                    cancelLayout.setBackgroundColor(getResources().getColor(R.color.transparent_gray_select));
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    cancelLayout.setBackgroundColor(Color.TRANSPARENT);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                case DragEvent.ACTION_DROP:
                    // User has decided to cancel the dragging operation.
                    // Move the dragging app back to the original location
                    Item dragging = Library.getDragging();

                    if (dragging.getGridIndex() == -1)
                        return true;

                    HomeScreenGrid.updateGrid(dragging.getGridIndex(),
                            HomeScreenGridAdapter.Instruction.PIN,
                            Library.getDragging());
                    break;
            }

            return true;
        });

        deleteLayout.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_ENTERED:
                    cancelLayout.setBackgroundColor(getResources().getColor(R.color.transparent_gray_select));
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    cancelLayout.setBackgroundColor(Color.TRANSPARENT);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                case DragEvent.ACTION_DROP:
                    // User has decided to delete the app that they
                    // are dragging. Show a confirmation interface
                    Item dragging = Library.getDragging();
                    if (dragging instanceof AppItem) {
                        Uri uri = Uri.fromParts("package", dragging.getIdentifier(), null);

                        Intent uninstall = new Intent(Intent.ACTION_DELETE, uri);
                        startActivity(uninstall);
                    }
                    break;
            }

            return true;
        });

        return view;
    }

    public static void showActions() {
        actions.setVisibility(View.VISIBLE);
        actions.bringToFront();
    }

    public static void hideActions() {
        actions.setVisibility(View.INVISIBLE);
    }

    private static LinearLayout actions;
}

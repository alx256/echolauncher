package com.example.echolauncher.home_screen;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Color;
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

import com.example.echolauncher.drawer.AppItem;
import com.example.echolauncher.utilities.Globals;
import com.example.echolauncher.drawer.Item;
import com.example.echolauncher.apps.Library;
import com.example.echolauncher.R;

import java.io.InvalidObjectException;

/**
 * This class implements the functionality of the home
 * screen pages fragment which contains the pages where
 * apps and widgets can be pinned
 */

public class HomeScreen extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_screen, null, false);

        Pages.init(view, getContext());

        // Get the user's wallpaper
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getContext());
        ImageView wallpaperView = view.findViewById(R.id.wallpaperView);
        // Set the background to the user's wallpaper
        wallpaperView.setImageDrawable(wallpaperManager.getDrawable());

        View statusBarBarrier = view.findViewById(R.id.statusBarBarrier);
        statusBarBarrier.getLayoutParams().height = Globals.statusBarHeight;

        View navigationBarBarrier = view.findViewById(R.id.navigationBarBarrier);
        navigationBarBarrier.getLayoutParams().height = Globals.navigationBarHeight + 10;

        LinearLayout actions = view.findViewById(R.id.actions),
                cancelLayout = actions.findViewById(R.id.cancelLayout),
                deleteLayout = actions.findViewById(R.id.deleteLayout);

        cancelLayout.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_ENTERED:
                    cancelLayout.setBackgroundColor(getResources().getColor(R.color.transparent_gray_select));
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                case DragEvent.ACTION_DRAG_EXITED:
                    cancelLayout.setBackgroundColor(Color.TRANSPARENT);
                    break;
                case DragEvent.ACTION_DROP:
                    // User has decided to cancel the dragging operation.
                    // Move the dragging app back to the original location
                    Item dragging = Library.getDragging();

                    if (dragging.getGridIndex() == -1)
                        return true;

                    try {
                        Pages.doInstruction(dragging.getGridIndex(), dragging.getPageNumber(),
                                HomeScreenGridAdapter.Instruction.PIN, dragging);
                    } catch (InvalidObjectException e) {
                        e.printStackTrace();
                    }
                    break;
            }

            return true;
        });

        deleteLayout.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_ENTERED:
                    deleteLayout.setBackgroundColor(getResources().getColor(R.color.transparent_gray_select));
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                case DragEvent.ACTION_DRAG_EXITED:
                    deleteLayout.setBackgroundColor(Color.TRANSPARENT);
                    break;
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
}

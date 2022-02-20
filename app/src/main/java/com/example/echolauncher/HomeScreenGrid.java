package com.example.echolauncher;

import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.gridlayout.widget.GridLayout;

import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxItemDecoration;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.CertificatePinner;

public class HomeScreenGrid extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_screen_grid, container, false);

        final RecyclerView recyclerView = view.findViewById(R.id.homeScreenGrid);

        FlexboxLayoutManager manager = new FlexboxLayoutManager(getContext());
        manager.setFlexWrap(FlexWrap.WRAP);
        manager.setFlexDirection(FlexDirection.ROW);
        manager.setAlignItems(AlignItems.STRETCH);
        manager.setJustifyContent(JustifyContent.SPACE_EVENLY);

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(manager);

        InstalledAppsManager.gridAdapter = new HomeScreenGridAdapter(getContext());
        recyclerView.setAdapter(InstalledAppsManager.gridAdapter);

        ImageView delete = view.findViewById(R.id.cross);
        delete.setY(-Globals.metricsFull.heightPixels + delete.getLayoutParams().height);
        delete.setVisibility(View.INVISIBLE);

        recyclerView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                delete.setVisibility(View.VISIBLE);

                if (dragEvent.getAction() == DragEvent.ACTION_DRAG_ENDED)
                    delete.setVisibility(View.INVISIBLE);

                return true;
            }
        });

        ViewTreeObserver observer = recyclerView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(0);
                HomeScreenGridAdapter adapter = (HomeScreenGridAdapter) recyclerView.getAdapter();
                adapter.updateTotal(holder.itemView.getMeasuredHeight());

                try {
                    HomeScreenStorage.ReadItems(getContext());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                adapter.notifyDataSetChanged();

                ViewTreeObserver temp = view.getViewTreeObserver();
                temp.removeOnGlobalLayoutListener(this);
            }
        });

        return view;
    }

    private View view;
}
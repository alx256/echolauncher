package com.example.echolauncher;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.fragment.app.Fragment;
import androidx.loader.content.AsyncTaskLoader;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WidgetDrawer extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.widget_drawer, container, false);

        initDrawer();

        // In order to update widgets (because Android forbids updating
        // UI elements on a thread that did not create them), a handler is created
        // that recursively calls itself in order constantly update the widgets

        handler = new Handler();
        updateRunnable = () -> {
            for (WidgetItem item : Library.getAllWidgets()) {
                item.getWidget().tick();
                item.update();
            }
            handler.postDelayed(updateRunnable, 1000);
        };

        handler.post(updateRunnable);

        return view;
    }

    private void initDrawer() {
        GridView drawerGridView = view.findViewById(R.id.drawerGrid);
        WidgetAdapter adapter = new WidgetAdapter(view.getContext(), Library.getAllWidgets());
        drawerGridView.setAdapter(adapter);
    }

    private View view;
    private Handler handler;
    private Runnable updateRunnable;
}

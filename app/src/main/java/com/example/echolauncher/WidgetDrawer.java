package com.example.echolauncher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.fragment.app.Fragment;

import java.util.List;

public class WidgetDrawer extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.widget_drawer, container, false);

        displayAllWidgets();
        initDrawer();

        return view;
    }

    private void displayAllWidgets() {
        widgets = Library.getAllWidgets();

        // Update each widget
        tickThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    for (WidgetItem widgetItem : widgets)
                        widgetItem.getWidget().Tick();

                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        tickThread.start();
    }

    private void initDrawer() {
        GridView drawerGridView = view.findViewById(R.id.drawerGrid);
        WidgetAdapter adapter = new WidgetAdapter(view.getContext(), widgets);
        drawerGridView.setAdapter(adapter);
    }

    private List<WidgetItem> widgets;
    private View view;
    private Thread tickThread;
}

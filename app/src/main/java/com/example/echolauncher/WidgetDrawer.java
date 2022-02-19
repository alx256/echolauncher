package com.example.echolauncher;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import com.google.android.material.textfield.TextInputEditText;

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
        widgets = InstalledAppsManager.getAllWidgets();

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
        Globals.widgetAdapter = new WidgetAdapter(view.getContext(), widgets);
        drawerGridView.setAdapter(Globals.widgetAdapter);

//        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//        float rows = (float) Math.ceil(drawerGridView.getCount() / drawerGridView.getNumColumns()),
//            itemHeight = 280 + drawerGridView.getVerticalSpacing();
//        view.getLayoutParams().height = (int) (itemHeight * rows);
    }

    private List<WidgetItem> widgets;
    private View view;
    private Thread tickThread;
}
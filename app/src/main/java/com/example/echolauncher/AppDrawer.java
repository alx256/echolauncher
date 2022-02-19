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

public class AppDrawer extends Fragment {
    private List<AppItem> apps;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.app_drawer, container, false);

        displayAllApps();
        initDrawer();
        initSearchBar();

        return view;
    }

    private void displayAllApps() {
        apps = InstalledAppsManager.getAllApps();
    }

    private void displaySearchedApps(String string) {
        apps = InstalledAppsManager.searchFor(string);
    }

    private void initDrawer() {
        View drawer = view.findViewById(R.id.drawer);
        GridView drawerGridView = view.findViewById(R.id.drawerGrid);
        Globals.appAdapter = new AppAdapter(view.getContext(), apps);
        drawerGridView.setAdapter(Globals.appAdapter);

//        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//        float rows = (float) Math.ceil(drawerGridView.getCount() / drawerGridView.getNumColumns()),
//            itemHeight = 280 + drawerGridView.getVerticalSpacing();
//        view.getLayoutParams().height = (int) (itemHeight * rows);
    }

    private void initSearchBar() {
        TextInputEditText input = view.findViewById(R.id.appSearchBar);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    displaySearchedApps(s.toString());
                    initDrawer();
                } else {
                    displayAllApps();
                    initDrawer();
                }
            }
        });
    }
}

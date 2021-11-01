package com.example.echolauncher;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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
        initScroll();

        return view;
    }

    private void displayAllApps() {
        apps = InstalledAppsManager.getApps(view.getContext());
    }

    private void displaySearchedApps(String string) {
        apps = InstalledAppsManager.searchFor(string);
    }

    private void initDrawer() {
        View drawer = view.findViewById(R.id.drawer);
        final GridView drawerGridView = view.findViewById(R.id.drawerGrid);
        drawerGridView.setAdapter(new AppAdapter(view.getContext(), apps));
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initScroll() {
        LinearLayout linearLayout = view.findViewById(R.id.linearLayout);

        if (linearLayout.canScrollVertically(1)) {
            linearLayout.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int scrollX, int scrollY,
                                           int oldScrollX, int oldScrollY) {
                    Log.d("", Integer.toString(scrollY));
                }
            });
        }
    }
}

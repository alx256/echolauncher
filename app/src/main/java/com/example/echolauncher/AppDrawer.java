package com.example.echolauncher;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class AppDrawer extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.app_drawer, container, false);

        apps = Library.getAllApps();
        initDrawer();

        TextInputEditText input = view.findViewById(R.id.appSearchBar);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0)
                    apps = Search.find(s.toString());
                else
                    apps = Library.getAllApps();

                initDrawer();
            }
        });

        return view;
    }

    private void initDrawer() {
        GridView drawerGridView = view.findViewById(R.id.drawerGrid);
        AppAdapter adapter = new AppAdapter(view.getContext(), apps);
        drawerGridView.setAdapter(adapter);
    }

    private List<AppItem> apps;
    private View view;
}

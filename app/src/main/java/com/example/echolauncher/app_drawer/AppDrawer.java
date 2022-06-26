package com.example.echolauncher.app_drawer;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.fragment.app.Fragment;

import com.example.echolauncher.drawer.DrawerAdapter;
import com.example.echolauncher.R;
import com.example.echolauncher.drawer.AppItem;
import com.example.echolauncher.utilities.Search;
import com.example.echolauncher.apps.Library;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains the logic for the app drawer fragment.
 */

public class AppDrawer extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the app drawer style file into a view
        view = inflater.inflate(R.layout.app_drawer, container, false);

        // Get all installed apps
        allApps = Library.getAllApps();
        allAppsAdapter = new DrawerAdapter(view.getContext(), allApps);

        searchResults = new ArrayList<>();
        searchResultsAdapter = new DrawerAdapter(view.getContext(), searchResults);

        drawerGridView = view.findViewById(R.id.drawerGrid);
        drawerGridView.setAdapter(allAppsAdapter);

        TextInputEditText input = view.findViewById(R.id.appSearchBar);
        input.addTextChangedListener(new TextWatcher() {
            // beforeTextChanged and afterTextChanged need to be overridden by this
            // listener, but as we only need the onTextChanged method to contain
            // functionality, both these methods can be empty

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Show the search results if the user has searched for something, otherwise
                // show all installed apps
                if (s.length() > 0) {
                    searchResults = Search.find(s.toString());
                    searchResultsAdapter = new DrawerAdapter(view.getContext(), searchResults);
                    drawerGridView.setAdapter(searchResultsAdapter);
                } else
                    drawerGridView.setAdapter(allAppsAdapter);
            }
        });

        return view;
    }

    private List<AppItem> allApps, searchResults;
    private DrawerAdapter allAppsAdapter, searchResultsAdapter;
    private View view;
    private GridView drawerGridView;
}

package com.example.echolauncher;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public class AppAdapter extends BaseAdapter {
    public AppAdapter(Context context, List<AppItem> items) {
        CONTEXT = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return items.get(position).toView(CONTEXT);
    }

    private final Context CONTEXT;
    // All items
    private List<AppItem> items;
}

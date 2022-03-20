package com.example.echolauncher;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * This class is an adapter class that
 * is used to populate the app and
 * widget drawer GridViews
 */

public class DrawerAdapter extends BaseAdapter {
    public DrawerAdapter(Context context, List<? extends Item> items) {
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
    private List<? extends Item> items;
}

package com.example.echolauncher;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public class WidgetAdapter extends BaseAdapter {
    public WidgetAdapter(Context context, List<WidgetItem> items) {
        CONTEXT = context;
        ITEMS = items;
    }

    @Override
    public int getCount() { return ITEMS.size(); }

    @Override
    public Object getItem(int position) {
        return ITEMS.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View finalView = ITEMS.get(position).toView(CONTEXT);
        return finalView;
    }

    private final Context CONTEXT;
    private final List<WidgetItem> ITEMS;
}

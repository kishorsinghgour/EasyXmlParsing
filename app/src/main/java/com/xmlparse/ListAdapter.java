package com.xmlparse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by gour.kishor on 20-Oct-16.
 */

public class ListAdapter extends ArrayAdapter<HashMap<String, String>> {
    public ListAdapter(Context context, List users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        HashMap<String, String> user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(android.R.id.text1);
        // Populate the data into the template view using the data object
        tvName.setText(user.get(MainActivity.KEY_ID));
        convertView.setTag(user.get(MainActivity.KEY_ID));
        // Return the completed view to render on screen
        return convertView;
    }
}
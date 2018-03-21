package com.wilbur.clingdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.fourthline.cling.model.meta.Device;


public class DevicesAdapter extends ArrayAdapter<Device> {
    private LayoutInflater mInflater;

    public DevicesAdapter(Context context) {
        super(context, 0);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = mInflater.inflate(R.layout.devices_items, null);

        Device item = getItem(position);
        if (item == null || item == null) {
            return convertView;
        }

        Device device = item;

        ImageView imageView = (ImageView)convertView.findViewById(R.id.listview_item_image);
        imageView.setBackgroundResource(R.drawable.ic_action_dock);

        TextView textView = (TextView) convertView.findViewById(R.id.listview_item_line_one);
        textView.setText(device.getDetails().getFriendlyName());
        return convertView;
    }
}
package com.jack.laboratory5_6.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.jack.laboratory5_6.R;

import java.util.ArrayList;

public class ScheduleListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> description;
    private final String name;
    private final ArrayList<String> hour;
    private final ArrayList<String> date;
    private final Bitmap image;

    public ScheduleListAdapter(Activity context, ArrayList<String> description, ArrayList<String> hour, ArrayList<String> date, String name, Bitmap image) {
        super(context, R.layout.list_layout, description);
        this.context = context;
        this.description = description;
        this.hour = hour;
        this.date = date;
        this.name = name;
        this.image = image;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_layout, parent, false);
        TextView nameTitle = (TextView) rowView.findViewById(R.id.name_list_view);
        TextView hourTitle = (TextView) rowView.findViewById(R.id.hour_list_view);
        TextView dateTitle = (TextView) rowView.findViewById(R.id.date_list_view);
        TextView statusTitle = (TextView) rowView.findViewById(R.id.status_list_view);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        if (position % 2 == 0) {
            rowView.setBackgroundResource(R.color.white);
        } else {
            rowView.setBackgroundResource(R.color.grey);
        }
        nameTitle.setText(name);
        hourTitle.setText(hour.get(position));
        dateTitle.setText(date.get(position));
        statusTitle.setText(description.get(position));
        imageView.setImageBitmap(image);
        return rowView;
    }
}
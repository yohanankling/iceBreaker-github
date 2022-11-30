package com.example.icebreaker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CostumBaseadapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    ArrayList<String> list;

    public CostumBaseadapter(Context context, ArrayList<String> list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.costumlistview, null);
        TextView textView = (TextView) convertView.findViewById(R.id.row);
        textView.setText(list.get(position));
        return convertView;
    }
}

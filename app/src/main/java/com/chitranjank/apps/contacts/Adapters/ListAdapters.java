package com.chitranjank.apps.contacts.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chitranjank.apps.contacts.R;

import java.util.List;

public class ListAdapters extends BaseAdapter {
    List<ContactContents> list;
    Context context;

    public ListAdapters(List<ContactContents> list, Context context) {
        this.list = list;
        this.context = context;
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

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.contact_contents, parent, false);

        TextView tName = convertView.findViewById(R.id.p_name);
        TextView pNumber = convertView.findViewById(R.id.p_number);
        TextView dateTV = convertView.findViewById(R.id.p_emails);
        TextView dur = convertView.findViewById(R.id.call_dur);
        ImageView incomingImg, outImg, missImg;

        incomingImg = convertView.findViewById(R.id.call_icoming);
        outImg = convertView.findViewById(R.id.call_out);
        missImg = convertView.findViewById(R.id.call_missed);

        pNumber.setText(list.get(position).getpNumber());
        dur.setText(list.get(position).getDuration());
        tName.setText(list.get(position).getpName());
        dateTV.setText(list.get(position).getDate());

        if (list.get(position).getType() == "INCOMING") {
            incomingImg.setVisibility(View.VISIBLE);
        } else if (list.get(position).getType() == "OUTGOING") {
            outImg.setVisibility(View.VISIBLE);
        } else if (list.get(position).getType() == "MISSED") {
            missImg.setVisibility(View.VISIBLE);
        }

        return convertView;
    }
}

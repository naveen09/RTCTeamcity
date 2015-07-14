package com.naveen.rtcteamcity.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.naveen.rtcteamcity.R;
import com.naveen.rtcteamcity.helpers.TCUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


public class ServerAdapter
        extends BaseAdapter {
    private Activity activity;

    private List<String> data = new ArrayList<String>();

    private static LayoutInflater inflater = null;

    public ServerAdapter(Activity a, Set<String> serverBeanList) {
        activity = a;
        data.addAll(Arrays.asList(serverBeanList.toArray(new String[serverBeanList.size()])));
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return data.size();
    }

    public void add(String item) {
        data.add(item);
    }

    public void remove(String item) {
        data.remove(item);
    }

    @Override
    public String getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.activity_serverbar, null);
        }
        TextView serverurl = (TextView) vi.findViewById(R.id.serverurl);

        serverurl.setTypeface(TCUtils.getTCFont(activity.getApplicationContext()));

        serverurl.setTextColor(activity.getResources()
                .getColor(android.R.color.white));

        String url = data.get(position);
        serverurl.setText(url);
        return vi;
    }

}

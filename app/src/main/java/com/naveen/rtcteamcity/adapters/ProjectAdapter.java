package com.naveen.rtcteamcity.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.naveen.rtcteamcity.R;
import com.naveen.rtcteamcity.bo.Project;
import com.naveen.rtcteamcity.helpers.TCUtils;

import java.util.List;


public class ProjectAdapter
        extends BaseAdapter {
    private Activity activity;

    private List<Project> data;

    private static LayoutInflater inflater = null;

    public ProjectAdapter(Activity a, List<Project> list) {
        activity = a;
        data = list;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.activity_projectbar, null);
        }
        TextView projectName = (TextView) vi.findViewById(R.id.projectname);
        TextView pid = (TextView) vi.findViewById(R.id.pid);

        projectName.setTypeface(TCUtils.getTCFont(activity.getApplicationContext()));
        pid.setTypeface(TCUtils.getTCFont(activity.getApplicationContext()));

        projectName.setTextColor(activity.getResources()
                .getColor(android.R.color.white));
        pid.setTextColor(activity.getResources()
                .getColor(android.R.color.white));

        Project projectBean = new Project();
        projectBean = data.get(position);
        projectName.setText(projectBean.getName());
        pid.setText(projectBean.getId());
        return vi;
    }

}

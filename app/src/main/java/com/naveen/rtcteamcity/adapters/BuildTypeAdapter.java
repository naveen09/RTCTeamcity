package com.naveen.rtcteamcity.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.naveen.rtcteamcity.R;
import com.naveen.rtcteamcity.activities.AppController;
import com.naveen.rtcteamcity.bo.RunningBuildType;
import com.naveen.rtcteamcity.helpers.TC;
import com.naveen.rtcteamcity.helpers.TCUtils;

import java.util.List;


public class BuildTypeAdapter
        extends BaseAdapter {
    private Activity activity;

    private List<RunningBuildType> data;

    private static LayoutInflater inflater = null;

    public BuildTypeAdapter(Activity a, List<RunningBuildType> list) {
        activity = a;
        data = list;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    public void clear() {
        data.clear();
    }

    @Override
    public RunningBuildType getItem(int position) {
        return data.get(position);
    }

    public void addAll(List<RunningBuildType> saveFavList) {
        data.addAll(saveFavList);

    }

    public void remove(RunningBuildType item) {
        data.remove(item);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.activity_buildtypebar, null);
        }
        final TextView projectName = (TextView) vi.findViewById(R.id.buildname);
        final TextView buildProject = (TextView) vi.findViewById(R.id.headerHomePage);
        final TextView statusLabel = (TextView) vi.findViewById(R.id.status);
        final TextView percentageText = (TextView) vi.findViewById(R.id.percentageText);
        final ProgressBar percentageBar = (ProgressBar) vi.findViewById(R.id.percentageBar);

        projectName.setTypeface(TCUtils.getTCFont(activity.getApplicationContext()));
        buildProject.setTypeface(TCUtils.getTCFont(activity.getApplicationContext()));
        statusLabel.setTypeface(TCUtils.getTCFont(activity.getApplicationContext()));
        percentageText.setTypeface(TCUtils.getTCFont(activity.getApplicationContext()));

        projectName.setTextColor(activity.getResources()
                .getColor(android.R.color.white));
        buildProject.setTextColor(activity.getResources()
                .getColor(android.R.color.white));

        RunningBuildType projectBean = new RunningBuildType();
        projectBean = data.get(position);
        projectName.setText(projectBean.getName());
        buildProject.setText(projectBean.getProjectName());
        statusLabel.setText(projectBean.getStatus());
        if (projectBean.getStatus() == null
                || projectBean.getStatus().equals("SUCCESS")) {
            percentageBar.setVisibility(View.GONE);
            percentageText.setVisibility(View.GONE);
        } else if (projectBean.getStatus() == null
                || projectBean.getStatus().equals("RUNNING")) {
            percentageBar.setVisibility(View.VISIBLE);
            percentageText.setVisibility(View.VISIBLE);
            String percentage = getPercentage(projectBean.getId());
            percentageBar.setProgress(percentage == ""
                    ? 0
                    : Integer.parseInt(percentage));
            percentageText.setText(percentage + " %");
        }
        updateStatusColor(statusLabel);
        return vi;
    }

    private String getPercentage(String buildId) {

        List<RunningBuildType> perValue = AppController.getRunningBuildTypes();
        for (RunningBuildType rBuild : perValue) {
            if (rBuild.getBuildTypeId().equals(buildId)) {
                return rBuild.getPercentageComplete();
            }
        }
        return "";
    }

    private void updateStatusColor(final TextView statusLabel) {
        switch (statusLabel.getText().toString()) {
            case TC.SUCCESS:
                statusLabel.setTextColor(activity.getResources()
                        .getColor(R.color.green));
                break;
            case TC.FAILURE:
                statusLabel.setTextColor(activity.getResources()
                        .getColor(R.color.red));
                break;
            case TC.ERROR:
                statusLabel.setTextColor(activity.getResources()
                        .getColor(R.color.orange));
                break;
            case TC.RUNNING:
                statusLabel.setTextColor(activity.getResources()
                        .getColor(R.color.orange));
                break;
            case TC.CANCEL:
                statusLabel.setTextColor(activity.getResources()
                        .getColor(R.color.yellow));
                break;
            default:
                statusLabel.setTextColor(activity.getResources()
                        .getColor(R.color.grey));
                break;
        }
    }

}

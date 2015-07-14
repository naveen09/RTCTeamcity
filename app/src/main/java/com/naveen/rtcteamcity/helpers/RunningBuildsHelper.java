package com.naveen.rtcteamcity.helpers;

import android.text.TextUtils;

import com.naveen.rtcteamcity.bo.RunningBuildType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RunningBuildsHelper
{

    private JSONObject             jso;

    private List<RunningBuildType> runningBuildTypes;

    public RunningBuildsHelper(String json)
    {
        runningBuildTypes = new ArrayList<RunningBuildType>();
        try
        {
            jso = new JSONObject(json);
            process(jso);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void process(JSONObject result) throws JSONException
    {
        if (TextUtils.isEmpty(result.toString()))
        {
            return;
        }
        JSONArray projects = result.getJSONArray("build");
        for (int i = 0; i < projects.length(); i++)
        {
            JSONObject obj = (JSONObject) projects.get(i);
            RunningBuildType buildType = new RunningBuildType();
            buildType.setHref(obj.getString("href"));
            buildType.setPercentageComplete(obj.getString("percentageComplete"));
            buildType.setBuildTypeId(obj.getString("buildTypeId"));
            runningBuildTypes.add(buildType);
        }

    }

    public List<RunningBuildType> getRunningBuilds()
    {
        return runningBuildTypes;
    }
}

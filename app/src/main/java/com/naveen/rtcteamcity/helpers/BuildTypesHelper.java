package com.naveen.rtcteamcity.helpers;

import com.naveen.rtcteamcity.bo.RunningBuildType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class BuildTypesHelper {

    private JSONObject jso;

    private List<RunningBuildType> buildTypesList;

    public BuildTypesHelper(String json) {
        buildTypesList = new ArrayList<RunningBuildType>();
        try {
            jso = new JSONObject(json);
            process(jso);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void process(JSONObject result) throws JSONException {
        JSONArray projects = result.getJSONObject("buildTypes")
                .getJSONArray("buildType");
        for (int i = 0; i < projects.length(); i++) {
            JSONObject obj = (JSONObject) projects.get(i);
            RunningBuildType buildType = new RunningBuildType();
            buildType.setHref(obj.getString("href"));
            buildType.setId(obj.getString("id"));
            buildType.setName(obj.getString("name"));
            buildType.setProjectName(obj.getString("projectName"));
            buildTypesList.add(buildType);
        }

    }

    public List<RunningBuildType> getProjectsList() {
        return buildTypesList;
    }
}

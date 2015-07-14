package com.naveen.rtcteamcity.helpers;

import com.naveen.rtcteamcity.bo.Project;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ProjectsHelper
{
    private JSONObject    jso;

    private List<Project> projectsList;

    public ProjectsHelper(String json)
    {
        projectsList = new ArrayList<Project>();
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
        JSONArray projects = result.getJSONArray("project");
        for (int i = 0; i < projects.length(); i++)
        {
            JSONObject obj = (JSONObject) projects.get(i);
            Project project = new Project();
            project.setHref(obj.getString("href"));
            project.setId(obj.getString("id"));
            project.setName(obj.getString("name"));
            projectsList.add(project);
        }

    }

    public List<Project> getProjectsList()
    {
        return projectsList;
    }
}

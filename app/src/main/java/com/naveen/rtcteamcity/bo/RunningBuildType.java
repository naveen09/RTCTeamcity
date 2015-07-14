package com.naveen.rtcteamcity.bo;

import java.io.Serializable;

public class RunningBuildType
    implements
        Serializable
{

    private static final long serialVersionUID = 4762581385823342314L;

    private String            buildTypeId;

    private String            percentageComplete;

    private String            status;

    private String            href;

    private String            server_url;

    private String            id;

    private String            name;

    private String            projectName;

    private String            lastBuildStatus;

    public String getBuildTypeId()
    {
        return buildTypeId;
    }

    public void setBuildTypeId(String buildTypeId)
    {
        this.buildTypeId = buildTypeId;
    }

    public String getPercentageComplete()
    {
        return percentageComplete;
    }

    public void setPercentageComplete(String percentageComplete)
    {
        this.percentageComplete = percentageComplete;
    }

    public String getStatus()
    {
        return status;
    }

    public String getLastBuildStatus()
    {
        return lastBuildStatus;
    }

    public void setLastBuildStatus(String lastBuildStatus)
    {
        this.lastBuildStatus = lastBuildStatus;
    }

    public String getServer_url()
    {
        return server_url;
    }

    public void setServer_url(String server_url)
    {
        this.server_url = server_url;
    }

    public void setStatus(String buildStatus)
    {
        this.status = buildStatus;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getHref()
    {
        return href;
    }

    public void setHref(String href)
    {
        this.href = href;
    }

    public String getProjectName()
    {
        return projectName;
    }

    public void setProjectName(String projectName)
    {
        this.projectName = projectName;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime
                 * result
                 + ((buildTypeId == null) ? 0 : buildTypeId.hashCode());
        result = prime * result + ((href == null) ? 0 : href.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime
                 * result
                 + ((lastBuildStatus == null) ? 0 : lastBuildStatus.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime
                 * result
                 + ((percentageComplete == null)
                                                ? 0
                                                : percentageComplete.hashCode());
        result = prime
                 * result
                 + ((projectName == null) ? 0 : projectName.hashCode());
        result = prime
                 * result
                 + ((server_url == null) ? 0 : server_url.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RunningBuildType other = (RunningBuildType) obj;
        if (buildTypeId == null)
        {
            if (other.buildTypeId != null)
                return false;
        } else if (!buildTypeId.equals(other.buildTypeId))
            return false;
        if (id == null)
        {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}

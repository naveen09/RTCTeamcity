package com.naveen.rtcteamcity.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.naveen.rtcteamcity.R;

public class SettingsActivity
    extends PreferenceActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

}

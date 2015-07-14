package com.naveen.rtcteamcity.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import com.naveen.rtcteamcity.R;
import com.naveen.rtcteamcity.helpers.TCUtils;

public class SplashActivity
    extends ActionBarActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        {
            super.onCreate(savedInstanceState);
            getSupportActionBar().hide();
            setContentView(R.layout.activity_splash);
            TextView splashLabel = (TextView) findViewById(R.id.splashLabel);
            TextView welcomeLabel = (TextView) findViewById(R.id.welcome);
            splashLabel.setTypeface(TCUtils.getTCFont(getApplicationContext()));
            welcomeLabel.setTypeface(TCUtils.getTCFont(getApplicationContext()));
            Handler handler = new Handler();
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    Intent serverConfigItent = new Intent(SplashActivity.this,
                                                          FavoritesActivity.class);
                    startActivity(serverConfigItent);
                }
            },
                                1000);
        }
    }

}

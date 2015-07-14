package com.naveen.rtcteamcity.activities;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.naveen.rtcteamcity.helpers.TC;
import com.naveen.rtcteamcity.service.PreferenceService;

import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;


/**
 * Represents an asynchronous login/registration task used to authenticate the
 * user.
 */
public class UserLoginTask
    extends AsyncTask<String, Void, String>
{
    private Activity context;

    public UserLoginTask(Activity act)
    {
        context = act;
    }

    @Override
    protected String doInBackground(String... params)
    {
        final DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        UsernamePasswordCredentials upc = new UsernamePasswordCredentials(params[0],
                                                                          params[1]);
        defaultHttpClient.getCredentialsProvider()
                         .setCredentials(new AuthScope(AuthScope.ANY_HOST,
                                                       AuthScope.ANY_PORT),
                                         upc);
        PreferenceService.saveSharedPreference(getApplicationContext(),
                TC.USER_NAME,
                params[0]);
        PreferenceService.saveSharedPreference(getApplicationContext(),
                                               TC.PASSWORD,
                                               params[1]);
        HttpGet request = new HttpGet(params[2] + TC.PROJECT_URL);
        request.addHeader(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        request.addHeader(new BasicHeader("Accept", "application/json"));
        HttpEntity entity;
        String data = "";
        try
        {
            entity = defaultHttpClient.execute(request).getEntity();
            Object content = EntityUtils.toString(entity);
            data = content.toString();
        } catch (ClientProtocolException e)
        {
            Log.e("UserLoginTask", e.getMessage());
            return "";
        } catch (IOException e)
        {
            Log.e("UserLoginTask", e.getMessage());
            return "";
        }

        return data;
    }

    private Context getApplicationContext()
    {
        return context.getApplicationContext();
    }

    @Override
    protected void onPostExecute(final String jsonResult)
    {
        if (context instanceof ServerConfigActivity)
        {
            ((ServerConfigActivity) context).onLoginTaskComplete(jsonResult);
        }
    }

    @Override
    protected void onCancelled()
    {
        if (context instanceof ServerConfigActivity)
        {
            ((ServerConfigActivity) context).onCancelled();
        }

    }
}
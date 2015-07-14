package com.naveen.rtcteamcity.helpers;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Base64;

import com.naveen.rtcteamcity.service.PreferenceService;

import org.apache.http.protocol.HTTP;

import java.util.HashMap;


public class TCUtils
{
    private static String fontPath     = "fonts/Roboto-Light.ttf";

    private static String font_awesome = "fonts/fontawesome-webfont.ttf";

    public static Typeface getTCFont(Context context)
    {
        Typeface tf = Typeface.createFromAsset(context.getAssets(), fontPath);
        return tf;
    }

    public static Typeface getFAFont(Context context)
    {
        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                                               font_awesome);
        return tf;
    }

    public static HashMap<String, String> getHeaderParams(Context context)
    {
        String username = PreferenceService.getSharedPreferenceForString(context,
                                                                         TC.USER_NAME);
        String password = PreferenceService.getSharedPreferenceForString(context,
                TC.PASSWORD);
        HashMap<String, String> params = new HashMap<String, String>();
        String creds = String.format("%s:%s", username, password);
        String auth = "Basic "
                      + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
        params.put("Authorization", auth);
        params.put(HTTP.CONTENT_TYPE, "application/json");
        params.put("Accept", "application/json");
        return params;
    }

    public static HashMap<String, String> getTextHeaderParams(Context context)
    {
        String username = PreferenceService.getSharedPreferenceForString(context,
                                                                         TC.USER_NAME);
        String password = PreferenceService.getSharedPreferenceForString(context,
                                                                         TC.PASSWORD);
        HashMap<String, String> params = new HashMap<String, String>();
        String creds = String.format("%s:%s", username, password);
        String auth = "Basic "
                      + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
        params.put("Authorization", auth);
        params.put(HTTP.CONTENT_TYPE, "text/plain");
        params.put("Accept", "text/plain");
        return params;
    }
}

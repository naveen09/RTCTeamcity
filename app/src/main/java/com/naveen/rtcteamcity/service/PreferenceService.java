package com.naveen.rtcteamcity.service;

import static com.naveen.rtcteamcity.helpers.TC.APP_PREFS;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.naveen.rtcteamcity.helpers.TC;


public class PreferenceService {
    public static boolean getPreferenceForBoolean(Context context,
                                                  String prefKey) {
        SharedPreferences sPreferences = context.getSharedPreferences(APP_PREFS,
                0);
        return sPreferences.getBoolean(prefKey, false);
    }

    public static String getPreferenceForString(Context context, String prefKey) {
        SharedPreferences sPreferences = context.getSharedPreferences(APP_PREFS,
                0);
        return sPreferences.getString(prefKey, null);
    }

    public static void savePreference(Context context,
                                      String prefKey,
                                      String value) {
        SharedPreferences sPreferences = context.getSharedPreferences(APP_PREFS,
                0);
        Editor editor = sPreferences.edit();
        editor.putString(prefKey, value);
        editor.commit();
    }

    public static void savePreference(Context context,
                                      String prefKey,
                                      boolean value) {
        SharedPreferences sPreferences = context.getSharedPreferences(APP_PREFS,
                0);
        Editor editor = sPreferences.edit();
        editor.putBoolean(prefKey, value);
        editor.commit();
    }

    public static void savePreference(Context context,
                                      String prefKey,
                                      Set<String> values) {
        Set<String> urlSet = null;
        if (null == PreferenceService.getConfiguredServers(context, prefKey)) {
            urlSet = new HashSet<String>();
        } else {
            urlSet = PreferenceService.getConfiguredServers(context, prefKey);
        }
        SharedPreferences sPreferences = context.getSharedPreferences(APP_PREFS,
                0);
        for (String url : values) {
            urlSet.add(url);
        }
        Editor editor = sPreferences.edit();
        editor.putStringSet(prefKey, urlSet);
        editor.commit();
    }

    public static void clearPreference(Context context) {
        SharedPreferences sPreferences = context.getSharedPreferences(APP_PREFS,
                0);
        Editor editor = sPreferences.edit();
        editor.clear();
        editor.commit();
    }

    public static Set<String> getConfiguredServers(Context context,
                                                   String prefKey) {
        SharedPreferences sPreferences = context.getSharedPreferences(APP_PREFS,
                0);
        return sPreferences.getStringSet(prefKey, null);
    }

    public static String getEmailID(Context context) {
        return getPreferenceForString(context, TC.EMAIL_ID);
    }

    public static String getTeamCityUrl(Context context) {
        return getPreferenceForString(context, TC.TC_URL);
    }

    public static boolean getSharedPreferenceForBoolean(Context context,
                                                        String prefKey) {
        SharedPreferences sPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sPreferences.getBoolean(prefKey, false);
    }

    public static String getSharedPreferenceForString(Context context,
                                                      String prefKey) {
        SharedPreferences sPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sPreferences.getString(prefKey, null);
    }

    public static void saveSharedPreference(Context context,
                                            String prefKey,
                                            boolean value) {
        SharedPreferences sPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sPreferences.edit();
        editor.putBoolean(prefKey, value);
        editor.commit();
    }

    public static void saveSharedPreference(Context context,
                                            String prefKey,
                                            String value) {
        SharedPreferences sPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sPreferences.edit();
        editor.putString(prefKey, value);
        editor.commit();
    }
}

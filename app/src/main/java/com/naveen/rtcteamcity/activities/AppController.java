package com.naveen.rtcteamcity.activities;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.naveen.rtcteamcity.R;
import com.naveen.rtcteamcity.bo.RunningBuildType;
import com.naveen.rtcteamcity.helpers.TC;
import com.naveen.rtcteamcity.service.PreferenceService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppController
        extends Application {
    public static final String TAG = AppController.class.getSimpleName();

    public static List<RunningBuildType> favBuildtypeList = new ArrayList<RunningBuildType>();

    public static List<RunningBuildType> runningBuildTypes = new ArrayList<RunningBuildType>();

    private Gson gson = new GsonBuilder().create();

    private RequestQueue mRequestQueue;

    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static List<RunningBuildType> getRunningBuildTypes() {
        return runningBuildTypes;
    }

    public List<RunningBuildType> getFavBuildList() {
        String fav_json = PreferenceService.getPreferenceForString(getApplicationContext(),
                TC.FAV_PREFS);
        if (fav_json == null) {
            return favBuildtypeList;
        }
        RunningBuildType[] buildTypeArrays = gson.fromJson(fav_json,
                RunningBuildType[].class);
        favBuildtypeList.clear();
        for (RunningBuildType bt : Arrays.asList(buildTypeArrays)) {
            if (!favBuildtypeList.contains(bt)) {
                favBuildtypeList.add(bt);
            }
        }
        return favBuildtypeList;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void createNotification(RunningBuildType buildType) {
        Context context = getApplicationContext();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("RTC Teamcity")
                .setContentText(buildType.getName())
                .setContentInfo(buildType.getStatus());

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(getApplicationContext(),
                FavoritesActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent contentintent = PendingIntent.getBroadcast(getApplicationContext(),
                0,
                intent,
                0);
        mBuilder.setContentIntent(contentintent);
        mBuilder.setSound(Uri.parse("android.resource://"
                + context.getPackageName()
                + "/"
                + R.raw.teamcity));
        mNotificationManager.notify(0, mBuilder.build());

        beep();
    }

    private void beep() {

        AudioManager manager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        MediaPlayer player = MediaPlayer.create(this, notification);
        switch (manager.getRingerMode()) {
            case AudioManager.RINGER_MODE_NORMAL:
                manager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        manager.getStreamVolume(AudioManager.STREAM_MUSIC),
                        0);
                player.start();
                v.vibrate(1000);
                break;
            case AudioManager.RINGER_MODE_SILENT:
                manager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                manager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                v.vibrate(1000);
                break;
        }

    }

    public void saveFavList() {
        Gson builder = new GsonBuilder().create();
        PreferenceService.savePreference(getApplicationContext(),
                TC.FAV_PREFS,
                builder.toJson(favBuildtypeList));

    }

    public void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
                .show();
    }

    public boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}

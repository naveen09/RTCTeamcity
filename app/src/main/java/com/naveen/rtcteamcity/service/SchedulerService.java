package com.naveen.rtcteamcity.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.naveen.rtcteamcity.activities.AppController;
import com.naveen.rtcteamcity.bo.RunningBuildType;
import com.naveen.rtcteamcity.helpers.RunningBuildsHelper;
import com.naveen.rtcteamcity.helpers.TC;
import com.naveen.rtcteamcity.helpers.TCUtils;

import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;


@SuppressLint("all")
public class SchedulerService
        extends Service {
    private final IBinder mBinder = new LocalBinder();

    private Context context;

    private Handler timer = new Handler();

    public class LocalBinder
            extends Binder {
        public SchedulerService getService() {
            return SchedulerService.this;
        }
    }

    @Override
    public void onCreate() {
        context = getApplicationContext();
    }

    Runnable currentBuildsRunning = new Runnable() {

        @Override
        public void run() {
            if (PreferenceService.getPreferenceForBoolean(context,
                    TC.LOGGED_IN)
                    && AppController.getInstance()
                    .haveNetworkConnection()) {
                pullCurrentRunningBuilds();
            }
            timer.postDelayed(currentBuildsRunning,
                    TC.TEN_MIN);
        }
    };

    private void pullCurrentRunningBuilds() {
        RequestQueue queue = AppController.getInstance().getRequestQueue();
        Set<String> urls = PreferenceService.getConfiguredServers(getApplicationContext(),
                TC.URLS);
        if (urls != null) {
            final List<String> server_urls = Arrays.asList(urls.toArray(new String[urls.size()]));
            for (int i = 0; i < server_urls.size(); i++) {
                final String current_url = server_urls.get(i);
                JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                        current_url
                                + "/httpAuth/app/rest/builds?locator=running:true",
                        null,
                        new Response.Listener<JSONObject>() {
                            public void onResponse(JSONObject result) {
                                updateRunningBuildsList(result);
                            }

                            ;
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError e) {
                                if (e.getCause() instanceof UnknownHostException) {
                                    AppController.getInstance()
                                            .showMessage(current_url
                                                    + " not reachable...");
                                }
                                Log.e(SchedulerService.class.getName(),
                                        "Failed to get running builds");
                            }

                        }) {
                    public Map<String, String> getHeaders() throws com.android.volley.AuthFailureError {
                        return TCUtils.getHeaderParams(getApplicationContext());
                    }

                    ;
                };

                queue.add(jsonReq);
            }
        }
        timer.postDelayed(currentBuildsRunning, TC.TEN_SEC);
    }

    protected void updateRunningBuildsList(JSONObject result) {
        AppController.getRunningBuildTypes().clear();
        RunningBuildsHelper rbh = new RunningBuildsHelper(result.toString());
        AppController.getRunningBuildTypes().addAll(rbh.getRunningBuilds());
        for (RunningBuildType buildType : getFavBuildsInRunningBuild()) {
            onFavListAutoRefreshComplete(buildType, "RUNNING");
        }
        if (!PreferenceService.getPreferenceForBoolean(getApplicationContext(),
                TC.IS_ACTIVITY_BACKGROUND)) {
            Intent intent = new Intent(TC.BUILD_STATUS_CHANGE_EVENT);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    Runnable favBuildRunner = new Runnable() {

        @Override
        public void run() {
            if (AppController.getInstance()
                    .haveNetworkConnection()) {
                pullFavBuildStatus();
                timer.postDelayed(favBuildRunner,
                        TC.FIVE_MIN);
            }

        }
    };

    private void pullFavBuildStatus() {
        RequestQueue queue = AppController.getInstance().getRequestQueue();
        List<RunningBuildType> favBuildList = AppController.getInstance()
                .getFavBuildList();
        for (int i = 0; i < favBuildList.size(); i++) {
            final int currentIndex = i;
            final RunningBuildType buildType = favBuildList.get(currentIndex);
            StringRequest sRequest = new StringRequest(Request.Method.GET,
                    buildType.getServer_url()
                            + "/app/rest/builds/buildType:(id:"
                            + buildType.getId()
                            + ")/status",
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String result) {
                            onFavListAutoRefreshComplete(buildType,
                                    result);
                        }

                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError e) {
                            onFavListAutoRefreshFail(buildType,
                                    e);

                        }

                    }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return TCUtils.getTextHeaderParams(getApplicationContext());

                }

            };
            queue.add(sRequest);
        }
    }

    private void onFavListAutoRefreshComplete(final RunningBuildType buildType,
                                              String result) {
        result = runningBuildTypeshas(buildType) ? "RUNNING" : result;
        buildType.setLastBuildStatus(buildType.getStatus());
        buildType.setStatus(result);
        if (!buildType.getLastBuildStatus().equals(result)) {
            AppController.getInstance().createNotification(buildType);
        }
        AppController.getInstance().saveFavList();
    }

    private boolean runningBuildTypeshas(RunningBuildType buildType) {
        for (RunningBuildType rBuild : AppController.getRunningBuildTypes()) {
            if (rBuild.getBuildTypeId().equals(buildType.getId())) {
                return true;
            }
        }
        return false;
    }

    private List<RunningBuildType> getFavBuildsInRunningBuild() {
        List<RunningBuildType> favRunningBuilds = new ArrayList<RunningBuildType>();
        for (RunningBuildType fBuild : AppController.getInstance()
                .getFavBuildList()) {
            if (runningBuildTypeshas(fBuild)) {
                favRunningBuilds.add(fBuild);
            }
        }
        return favRunningBuilds;
    }

    private void onFavListAutoRefreshFail(final RunningBuildType buildType,
                                          VolleyError e) {
        if (e.getCause() instanceof UnknownHostException) {
            AppController.getInstance()
                    .showMessage("Failed to get status for build "
                            + buildType.getName()
                            + ". As "
                            + buildType.getServer_url()
                            + " not reachable...");
        } else {
            AppController.getInstance().showMessage("Failed to get Status "
                    + buildType.getName());

        }
        buildType.setLastBuildStatus(buildType.getStatus());
        buildType.setStatus("Unknown");
        Log.e("BuildTypeAdapter", "Failed to get Status "
                + buildType.getName()
                + "");
    }

    public void enableRefresh(boolean isChecked) {
        PreferenceService.savePreference(getApplicationContext(),
                TC.REFRESH,
                isChecked);
        if (isChecked) {
            favBuildRunner.run();
        } else {
            timer.removeCallbacks(favBuildRunner);
            AppController.getInstance().showMessage("Auto Refresh Disabled");
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer.post(currentBuildsRunning);
        if (PreferenceService.getSharedPreferenceForBoolean(context, TC.REFRESH)) {
            enableRefresh(true);
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

}

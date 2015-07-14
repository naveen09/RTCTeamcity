package com.naveen.rtcteamcity.activities;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.naveen.rtcteamcity.R;
import com.naveen.rtcteamcity.adapters.BuildTypeAdapter;
import com.naveen.rtcteamcity.bo.RunningBuildType;
import com.naveen.rtcteamcity.helpers.SwipeDismissListViewTouchListener;
import com.naveen.rtcteamcity.helpers.TC;
import com.naveen.rtcteamcity.helpers.TCUtils;
import com.naveen.rtcteamcity.service.PreferenceService;
import com.naveen.rtcteamcity.service.SchedulerService;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.JazzyListView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FavoritesActivity
        extends ActionBarActivity {
    private static final String KEY_TRANSITION_EFFECT = "transition_effect";

    private int total_request_served = 0;

    private int mCurrentTransitionEffect = JazzyHelper.CARDS;

    private ProgressDialog loading;

    private JazzyListView listView;

    private BuildTypeAdapter adapter;

    private RequestQueue queue;

    private List<RunningBuildType> buildTypeList;

    private Switch autoRefreshBtn;

    private SchedulerService mService;

    boolean mBound = false;

    private TextView refreshDate;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm",
            Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        PreferenceService.savePreference(getApplicationContext(),
                TC.IS_ACTIVITY_BACKGROUND,
                false);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_bg));
        setTitle(R.string.watch_list);
        refreshDate = (TextView) findViewById(R.id.refreshDate);
        TextView no_data_label = (TextView) findViewById(R.id.no_data_label);
        no_data_label.setTypeface(TCUtils.getTCFont(getApplicationContext()));
        Button configureBtn = (Button) findViewById(R.id.configureBtn);
        configureBtn.setTypeface(TCUtils.getTCFont(getApplicationContext()));

        autoRefreshBtn = (Switch) findViewById(R.id.autoRefreshBtn);
        loading = new ProgressDialog(this);
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);
        loading.setMessage(getString(R.string.loading_build_types_));

        buildTypeList = AppController.getInstance().getFavBuildList();
        listView = (JazzyListView) findViewById(R.id.favList);
        adapter = new BuildTypeAdapter(this, buildTypeList);
        if (savedInstanceState != null) {
            mCurrentTransitionEffect = savedInstanceState.getInt(KEY_TRANSITION_EFFECT,
                    JazzyHelper.HELIX);
            setupJazziness(mCurrentTransitionEffect);
        }
        listView.setAdapter(adapter);
        if (PreferenceService.getSharedPreferenceForBoolean(getApplicationContext(),
                TC.REFRESH)) {
            autoRefreshBtn.setChecked(true);
        } else {
            autoRefreshBtn.setChecked(false);
        }
        if (!isSchedulerServiceRunning()) {
            startService(new Intent(this, SchedulerService.class));
        }
        autoRefreshBtn.setOnCheckedChangeListener(refreshListener);

        SwipeDismissListViewTouchListener swipeToDeleteListener = new SwipeDismissListViewTouchListener(listView,
                new SwipeDismissListViewTouchListener.OnDismissCallback() {

                    @Override
                    public void onDismiss(ListView listView,
                                          int[] reverseSortedPositions) {
                        onSwipeToDelete(listView,
                                reverseSortedPositions);
                    }

                });
        listView.setOnTouchListener(swipeToDeleteListener);
        listView.setOnScrollListener(swipeToDeleteListener.makeScrollListener());
    }

    private void onSwipeToDelete(ListView listView, int[] reverseSortedPositions) {
        for (int position : reverseSortedPositions) {
            RunningBuildType item = adapter.getItem(position);
            adapter.remove(item);
            AppController.getInstance().getFavBuildList().remove(item);
            AppController.getInstance().showMessage("Removed "
                    + item.getName()
                    + " from watch list.");
        }
        adapter.notifyDataSetChanged();
        listView.refreshDrawableState();
        AppController.getInstance().saveFavList();
        showHideData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, SchedulerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private BroadcastReceiver statusReceiver = new BroadcastReceiver() {
        public void onReceive(Context context,
                              Intent intent) {
            adapter.clear();
            adapter.notifyDataSetChanged();
            listView.refreshDrawableState();
            adapter.addAll(AppController.getInstance()
                    .getFavBuildList());
            refreshDate.setText(sdf.format(new Date()));
            if (adapter.getCount() == 0) {
                showNoData();
            }
        }

        ;
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_TRANSITION_EFFECT, mCurrentTransitionEffect);
    }

    private void setupJazziness(int effect) {
        mCurrentTransitionEffect = effect;
        listView.setTransitionEffect(mCurrentTransitionEffect);
    }

    OnCheckedChangeListener refreshListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            mService.enableRefresh(isChecked);
        }
    };

    private void hideNoData() {
        View v = findViewById(R.id.nodata_fav);
        View favView = findViewById(R.id.favView);
        favView.setVisibility(View.VISIBLE);
        v.setVisibility(View.GONE);
    }

    private void showNoData() {
        View v = findViewById(R.id.nodata_fav);
        View favView = findViewById(R.id.favView);
        favView.setVisibility(View.GONE);
        v.setVisibility(View.VISIBLE);
    }

    public void startServerConfigActivity(View v) {
        startServerConfigActivity();
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceService.savePreference(getApplicationContext(),
                TC.IS_ACTIVITY_BACKGROUND,
                true);
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(statusReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(statusReceiver,
                        new IntentFilter(TC.BUILD_STATUS_CHANGE_EVENT));
        buildTypeList = AppController.getInstance().getFavBuildList();
        if (buildTypeList.isEmpty()) {
            showNoData();
        } else {
            hideNoData();
        }
    }

    private void showHideData() {
        if (buildTypeList.isEmpty()) {
            showNoData();
        } else {
            hideNoData();
            pullBuildStatus();
        }
    }

    private void pullBuildStatus() {
        loading.show();
        queue = AppController.getInstance().getRequestQueue();
        loading.setMessage(getString(R.string.loading_build_status_));
        for (int i = 0; i < buildTypeList.size(); i++) {
            final int currentIndex = i;
            final RunningBuildType buildType = buildTypeList.get(currentIndex);
            StringRequest sRequest = new StringRequest(Request.Method.GET,
                    buildType.getServer_url()
                            + "/app/rest/builds/buildType:(id:"
                            + buildType.getId()
                            + ")/status",
                    new Listener<String>() {

                        @Override
                        public void onResponse(String result) {
                            buildType.setStatus(result);
                            updateProgressBar(currentIndex);
                        }

                    },
                    new ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError e) {
                            updateProgressBar(currentIndex);
                            buildType.setStatus("Unknown");
                            AppController.getInstance()
                                    .showMessage("Failed to get Status "
                                            + buildType.getName()
                                            + "");
                            Log.e("BuildTypeAdapter",
                                    "Failed to get Status "
                                            + buildType.getName()
                                            + "");

                        }
                    }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return TCUtils.getTextHeaderParams(FavoritesActivity.this);

                }

            };
            queue.add(sRequest);
        }
    }

    protected void updateProgressBar(int currentIndex) {
        if (++total_request_served == buildTypeList.size()) {
            total_request_served = 0;
            loading.hide();
            listView.refreshDrawableState();
            adapter.notifyDataSetChanged();
            refreshDate.setText(sdf.format(new Date()));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favorites, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.serverconfSettings) {
            startServerConfigActivity();
            return true;
        } else if (id == R.id.setttings) {
            startSettingsActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startServerConfigActivity() {
        Intent serverConfigIntent = new Intent(FavoritesActivity.this,
                ServerConfigActivity.class);
        startActivity(serverConfigIntent);
        overridePendingTransition(R.animator.activity_open_translate,
                R.animator.activity_close_scale);
    }

    private void startSettingsActivity() {
        Intent settingsIntent = new Intent(FavoritesActivity.this,
                SettingsActivity.class);
        startActivity(settingsIntent);
    }

    private boolean isSchedulerServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SchedulerService.class.getName()
                    .equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            SchedulerService.LocalBinder binder = (SchedulerService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}

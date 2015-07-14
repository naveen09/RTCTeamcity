package com.naveen.rtcteamcity.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.naveen.rtcteamcity.R;
import com.naveen.rtcteamcity.adapters.BuildTypeAdapter;
import com.naveen.rtcteamcity.bo.RunningBuildType;
import com.naveen.rtcteamcity.helpers.BuildTypesHelper;
import com.naveen.rtcteamcity.helpers.TC;
import com.naveen.rtcteamcity.helpers.TCUtils;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class BuildTypeActivity
        extends Activity
        implements
        OnItemClickListener {
    private List<RunningBuildType> buildTypeList;

    private BuildTypeAdapter adapter;

    private ProgressDialog loading;

    private String current_url;

    private RequestQueue queue;

    private int total_request_served = 0;

    private ListView mList;

    @Override
    public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
        showFavDialog(v, position);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_buildtype);
        mList = (ListView) findViewById(android.R.id.list);
        loading = new ProgressDialog(this);
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);
        loading.setMessage(getString(R.string.loading_build_types_));

        mList.setOnItemClickListener(this);
    }

    public void onComplete(JSONObject result) {
        BuildTypesHelper helper = new BuildTypesHelper(result.toString());
        buildTypeList = helper.getProjectsList();
        adapter = new BuildTypeAdapter(this, buildTypeList);
        mList.setAdapter(adapter);
        pullBuildStatus();
    }

    private void pullBuildStatus() {
        loading.show();
        queue = AppController.getInstance().getRequestQueue();
        loading.setMessage(getString(R.string.loading_build_status_));
        final String current_url = this.getIntent()
                .getExtras()
                .getString(TC.URLS);
        for (int i = 0; i < buildTypeList.size(); i++) {
            final int currentIndex = i;
            final RunningBuildType buildType = buildTypeList.get(currentIndex);
            StringRequest sRequest = new StringRequest(Request.Method.GET,
                    current_url
                            + "/app/rest/builds/buildType:(id:"
                            + buildType.getId()
                            + ")/status",
                    new Listener<String>() {

                        @Override
                        public void onResponse(String result) {
                            buildType.setStatus(result);
                            buildType.setServer_url(current_url);
                            updateProgressBar(currentIndex);
                        }

                    },
                    new ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError e) {
                            updateProgressBar(currentIndex);
                            Log.e("BuildTypeAdapter",
                                    "Failed to get Status "
                                            + currentIndex
                                            + "");

                        }
                    }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return TCUtils.getTextHeaderParams(BuildTypeActivity.this);

                }

            };
            queue.add(sRequest);
        }
    }

    protected void updateProgressBar(int currentIndex) {
        if (++total_request_served == buildTypeList.size()) {
            total_request_served = 0;
            loading.hide();
            adapter.notifyDataSetChanged();
            mList.refreshDrawableState();
        }

    }

    private void showFavDialog(View view, final int position) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_favorites);

        Button okBtn = (Button) dialog.findViewById(R.id.addBuildBtn);
        Button removeBuildBtn = (Button) dialog.findViewById(R.id.removeBtn);

        okBtn.setBackgroundResource(R.drawable.custom_button);
        removeBuildBtn.setBackgroundResource(R.drawable.custom_button);

        okBtn.setTypeface(TCUtils.getFAFont(getApplicationContext()));
        removeBuildBtn.setTypeface(TCUtils.getFAFont(getApplicationContext()));

        okBtn.setText(String.format(getString(R.string.ok),
                getString(R.string.fa_check)));

        removeBuildBtn.setText(String.format(getString(R.string.remove),
                getString(R.string.fa_times)));
        okBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                addToFavorites(buildTypeList.get(position));
                dialog.dismiss();
                Toast.makeText(getApplicationContext(),
                        "Successfully added "
                                + buildTypeList.get(position).getName()
                                + " to watch list",
                        Toast.LENGTH_SHORT).show();
            }
        });
        removeBuildBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AppController.getInstance()
                        .getFavBuildList()
                        .remove(buildTypeList.get(position));
                dialog.dismiss();

            }
        });
        if (AppController.getInstance()
                .getFavBuildList()
                .contains(buildTypeList.get(position))) {
            Toast.makeText(getApplicationContext(),
                    "Build already added to watch list",
                    Toast.LENGTH_SHORT).show();
        } else {
            dialog.setTitle("Watch this build");
            removeBuildBtn.setVisibility(View.GONE);
            okBtn.setVisibility(View.VISIBLE);
            dialog.show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        String pid = getIntent().getExtras().getString(TC.ID);
        String pName = getIntent().getExtras().getString(TC.PROJECT_NAME);

        String pHref = getIntent().getExtras().getString(TC.PROJECT_HREF);
        TextView header = (TextView) findViewById(R.id.buildTypeHeader);
        header.setText("Build Types in " + pName + "");
        header.setTypeface(TCUtils.getTCFont(getApplicationContext()));

        current_url = getIntent().getExtras().getString(TC.URLS);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                current_url + pHref,
                null,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject result) {
                        onComplete(result);
                    }

                    ;
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        Log.e("BuildTypeActivity",
                                e.getMessage());
                        Toast.makeText(getApplicationContext(),
                                "Failed to get Build types",
                                Toast.LENGTH_SHORT)
                                .show();
                        loading.dismiss();
                    }

                }) {
            public Map<String, String> getHeaders() throws com.android.volley.AuthFailureError {
                return TCUtils.getHeaderParams(getApplicationContext());
            }

            ;
        };
        queue.add(jsonReq);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppController.getInstance().saveFavList();
    }

    protected void addToFavorites(RunningBuildType buildType) {
        List<RunningBuildType> favBuildList = AppController.getInstance()
                .getFavBuildList();
        favBuildList.add(buildType);
        AppController.getInstance().saveFavList();
    }
}

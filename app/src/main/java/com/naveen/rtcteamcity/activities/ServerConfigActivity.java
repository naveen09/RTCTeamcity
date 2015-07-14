package com.naveen.rtcteamcity.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.naveen.rtcteamcity.R;
import com.naveen.rtcteamcity.adapters.ServerAdapter;
import com.naveen.rtcteamcity.helpers.SwipeDismissListViewTouchListener;
import com.naveen.rtcteamcity.helpers.TC;
import com.naveen.rtcteamcity.helpers.TCUtils;
import com.naveen.rtcteamcity.service.PreferenceService;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.JazzyListView;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ServerConfigActivity
        extends Activity
        implements
        OnItemClickListener {
    private Set<String> serverUrlList;

    private ServerAdapter adapter;

    private View noDataView;

    private UserLoginTask mAuthTask = null;

    private ProgressDialog loading;

    private String username;

    private String password;

    private String currentUrl;

    private JazzyListView serverListView;

    private int mCurrentTransitionEffect = JazzyHelper.CARDS;

    private static final String KEY_TRANSITION_EFFECT = "transition_effect";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_server_config);
        loading = new ProgressDialog(this);
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);

        ((TextView) findViewById(R.id.no_data_label)).setTypeface(TCUtils.getTCFont(getApplicationContext()));
        noDataView = findViewById(R.id.nodata);
        serverListView = (JazzyListView) findViewById(R.id.serversList);

        serverUrlList = new HashSet<String>();
        Set<String> urls = PreferenceService.getConfiguredServers(getApplicationContext(),
                TC.URLS);
        if (savedInstanceState != null) {
            mCurrentTransitionEffect = savedInstanceState.getInt(KEY_TRANSITION_EFFECT,
                    JazzyHelper.HELIX);
            setupJazziness(mCurrentTransitionEffect);
        }

        if (null != urls && !urls.isEmpty()) {
            adapter = new ServerAdapter(this, urls);
            serverUrlList = urls;
        } else {
            adapter = new ServerAdapter(this, serverUrlList);
        }
        showHideViews();
        serverListView.setAdapter(adapter);
        serverListView.setOnItemClickListener(this);
        SwipeDismissListViewTouchListener swipeToDeleteListener = new SwipeDismissListViewTouchListener(serverListView,
                new SwipeDismissListViewTouchListener.OnDismissCallback() {

                    @Override
                    public void onDismiss(ListView listView,
                                          int[] reverseSortedPositions) {
                        for (int position : reverseSortedPositions) {
                            String item = adapter.getItem(position);
                            adapter.remove(item);
                            serverUrlList.remove(item);
                            AppController.getInstance()
                                    .showMessage("Removed "
                                            + item
                                            + " from configuration.");
                        }
                        adapter.notifyDataSetChanged();
                        listView.refreshDrawableState();
                        PreferenceService.savePreference(getApplicationContext(),
                                TC.URLS,
                                serverUrlList);
                        showHideViews();
                    }
                });
        serverListView.setOnTouchListener(swipeToDeleteListener);
        serverListView.setOnScrollListener(swipeToDeleteListener.makeScrollListener());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_TRANSITION_EFFECT, mCurrentTransitionEffect);
    }

    private void setupJazziness(int effect) {
        mCurrentTransitionEffect = effect;
        serverListView.setTransitionEffect(mCurrentTransitionEffect);
    }

    @Override
    public void onItemClick(AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {
        String url = (String) Arrays.asList(serverUrlList.toArray())
                .get(position);
        currentUrl = url;
        username = PreferenceService.getSharedPreferenceForString(getApplicationContext(),
                TC.USER_NAME);
        password = PreferenceService.getSharedPreferenceForString(getApplicationContext(),
                TC.PASSWORD);
        if (username != null && password != null) {
            attemptLogin();
        } else {
            openLoginDialog();
        }
    }

    public void openLoginDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Credentials");
        dialog.setContentView(R.layout.loginform_view);
        final EditText username = (EditText) dialog.findViewById(R.id.username);
        final EditText password = (EditText) dialog.findViewById(R.id.password);

        username.setTypeface(TCUtils.getTCFont(getApplicationContext()));
        password.setTypeface(TCUtils.getTCFont(getApplicationContext()));

        Button okBtn = (Button) dialog.findViewById(R.id.okBtn);
        Button cancelBtn = (Button) dialog.findViewById(R.id.cancelBtn);

        okBtn.setBackgroundResource(R.drawable.custom_button);
        cancelBtn.setBackgroundResource(R.drawable.custom_button);

        okBtn.setTypeface(TCUtils.getFAFont(getApplicationContext()));
        cancelBtn.setTypeface(TCUtils.getFAFont(getApplicationContext()));

        okBtn.setText(String.format(getString(R.string.ok),
                getString(R.string.fa_check)));

        cancelBtn.setText(String.format(getString(R.string.cancel),
                getString(R.string.fa_times)));
        okBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (validateFields(username, password)) {
                    PreferenceService.saveSharedPreference(ServerConfigActivity.this,
                            TC.USER_NAME,
                            username.getText()
                                    .toString());
                    PreferenceService.saveSharedPreference(ServerConfigActivity.this,
                            TC.PASSWORD,
                            password.getText()
                                    .toString());

                    dialog.dismiss();
                } else {
                    AppController.getInstance()
                            .showMessage("Fill Required Fields");
                }
            }
        });
        cancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    public void openServerConfigDialog(View v) {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Add a server");
        dialog.setContentView(R.layout.addserver_view);
        final EditText serverUrl = (EditText) dialog.findViewById(R.id.serverurl);

        serverUrl.setTypeface(TCUtils.getTCFont(getApplicationContext()));

        final Button okBtn = (Button) dialog.findViewById(R.id.okBtn);
        final Button cancelBtn = (Button) dialog.findViewById(R.id.cancelBtn);

        okBtn.setBackgroundResource(R.drawable.custom_button);
        cancelBtn.setBackgroundResource(R.drawable.custom_button);

        okBtn.setTypeface(TCUtils.getFAFont(getApplicationContext()));
        cancelBtn.setTypeface(TCUtils.getFAFont(getApplicationContext()));

        okBtn.setText(String.format(getString(R.string.ok),
                getString(R.string.fa_check)));

        cancelBtn.setText(String.format(getString(R.string.cancel),
                getString(R.string.fa_times)));
        okBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String url = serverUrl.getText().toString();
                if (url.isEmpty() || url.trim().length() == 0) {
                    return;
                }
                addServerEntry(url);
                dialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    protected void addServerEntry(String url) {
        if (!serverUrlList.contains(url)) {
            serverUrlList.add(url);
            adapter.add(url);
            adapter.notifyDataSetChanged();
            PreferenceService.savePreference(getApplicationContext(),
                    TC.URLS,
                    serverUrlList);
        }
        showHideViews();
    }

    private void showHideViews() {
        if (serverUrlList.isEmpty()) {
            serverListView.setVisibility(View.GONE);
            noDataView.setVisibility(View.VISIBLE);
        } else {
            noDataView.setVisibility(View.GONE);
            serverListView.setVisibility(View.VISIBLE);
        }
    }

    public boolean validateFields(final EditText usernameView,
                                  final EditText mPasswordView) {
        if (mAuthTask != null) {
            return false;
        }
        username = usernameView.getText().toString();
        password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (password.length() < 4) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        if (TextUtils.isEmpty(username)) {
            usernameView.setError(getString(R.string.error_field_required));
            focusView = usernameView;
            cancel = true;
        } else if (username.length() != 7) {
            usernameView.setError(getString(R.string.error_invalid_email));
            focusView = usernameView;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        }
        return !cancel;
    }

    private void attemptLogin() {
        loading.setMessage("Loading Build Types");
        loading.show();
        mAuthTask = new UserLoginTask(this);
        mAuthTask.execute(username, password, currentUrl);
    }

    public void onLoginTaskComplete(String jsonResult) {
        mAuthTask = null;
        if (!TextUtils.isEmpty(jsonResult)) {
            loading.hide();
            PreferenceService.savePreference(getApplicationContext(),
                    TC.LOGGED_IN,
                    true);
            final Intent homePageIntent = new Intent(ServerConfigActivity.this,
                    ProjectsHomePage.class);
            homePageIntent.putExtra(TC.URLS, currentUrl);
            homePageIntent.putExtra("result", jsonResult);
            startActivity(homePageIntent);

        } else {
            loading.hide();
            PreferenceService.savePreference(getApplicationContext(),
                    TC.LOGGED_IN,
                    false);
            AppController.getInstance()
                    .showMessage("Failed to connect to teamcity server/Password is incorrect");
        }

    }

    public void onCancelled() {
        mAuthTask = null;
        loading.hide();

    }
}

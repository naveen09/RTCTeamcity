package com.naveen.rtcteamcity.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.naveen.rtcteamcity.R;
import com.naveen.rtcteamcity.adapters.ProjectAdapter;
import com.naveen.rtcteamcity.bo.Project;
import com.naveen.rtcteamcity.helpers.ProjectsHelper;
import com.naveen.rtcteamcity.helpers.TC;
import com.naveen.rtcteamcity.helpers.TCUtils;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.JazzyListView;

import java.util.List;

public class ProjectsHomePage
    extends Activity
{
    private List<Project>       projectsList;

    private ProjectAdapter adapter;

    private ProgressDialog      loading;

    private String              current_url;

    private JazzyListView       listView;

    private int                 mCurrentTransitionEffect = JazzyHelper.CARDS;

    private static final String KEY_TRANSITION_EFFECT    = "transition_effect";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home_page);
        loading = new ProgressDialog(this);
        loading.setCancelable(true);
        loading.setCanceledOnTouchOutside(false);
        loading.setMessage("Loading Projects...");
        loading.show();
        String result = getIntent().getExtras().getString("result");
        ProjectsHelper helper = new ProjectsHelper(result);
        listView = (JazzyListView) findViewById(R.id.projectslist);

        TextView header = (TextView) findViewById(R.id.headerHomePage);
        header.setTypeface(TCUtils.getTCFont(getApplicationContext()));

        current_url = getIntent().getExtras().getString(TC.URLS);

        if (savedInstanceState != null)
        {
            mCurrentTransitionEffect = savedInstanceState.getInt(KEY_TRANSITION_EFFECT,
                                                                 JazzyHelper.HELIX);
            setupJazziness(mCurrentTransitionEffect);
        }
        projectsList = helper.getProjectsList();
        adapter = new ProjectAdapter(this, projectsList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(selListener);
        loading.dismiss();

    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_TRANSITION_EFFECT, mCurrentTransitionEffect);
    }

    private void setupJazziness(int effect)
    {
        mCurrentTransitionEffect = effect;
        listView.setTransitionEffect(mCurrentTransitionEffect);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.project, menu);
        return true;
    }

    OnItemClickListener selListener = new OnItemClickListener()
                                    {
                                        @Override
                                        public void onItemClick(AdapterView<?> arg0,
                                                                View view,
                                                                int position,
                                                                long id)
                                        {
                                            String pid = projectsList.get(position)
                                                                     .getId();
                                            String pName = projectsList.get(position)
                                                                       .getName();
                                            String href = projectsList.get(position)
                                                                      .getHref();
                                            Intent projectactivity = new Intent(ProjectsHomePage.this,
                                                                                BuildTypeActivity.class);
                                            projectactivity.putExtra(TC.ID, pid);
                                            projectactivity.putExtra(TC.PROJECT_NAME,
                                                                     pName);
                                            projectactivity.putExtra(TC.PROJECT_HREF,
                                                                     href);
                                            projectactivity.putExtra(TC.URLS,
                                                                     current_url);
                                            startActivity(projectactivity);
                                        }

                                    };

}

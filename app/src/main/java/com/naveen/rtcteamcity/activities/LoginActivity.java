package com.naveen.rtcteamcity.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.naveen.rtcteamcity.R;
import com.naveen.rtcteamcity.helpers.TC;
import com.naveen.rtcteamcity.helpers.TCUtils;
import com.naveen.rtcteamcity.service.PreferenceService;

public class LoginActivity
    extends Activity
{
    private UserLoginTask mAuthTask = null;

    private String        username;

    private String        mPassword;

    private EditText      mEmailView;

    private EditText      mPasswordView;

    private View          mLoginFormView;

    private View          mLoginStatusView;

    private TextView      mLoginStatusMessageView;

    private String        current_url;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_login);

        TextView header = (TextView) findViewById(R.id.headerHomePage);
        header.setTypeface(TCUtils.getTCFont(getApplicationContext()));

        mLoginFormView = findViewById(R.id.login_form);
        mLoginStatusView = findViewById(R.id.login_status);
        mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
        mLoginStatusMessageView.setTypeface(TCUtils.getTCFont(getApplicationContext()));
        showProgress(true);

        mEmailView = (EditText) findViewById(R.id.email);
        mEmailView.setText(username);

        mEmailView.setTypeface(TCUtils.getTCFont(getApplicationContext()));

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setTypeface(TCUtils.getTCFont(getApplicationContext()));
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView,
                                          int id,
                                          KeyEvent keyEvent)
            {
                if (id == R.id.login || id == EditorInfo.IME_NULL)
                {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button signInBtn = (Button) findViewById(R.id.sign_in_button);
        signInBtn.setTypeface(TCUtils.getFAFont(getApplicationContext()));
        signInBtn.setText(String.format(getString(R.string.signin),
                                        getString(R.string.fa_sign_in)));
        signInBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                attemptLogin();
            }
        });

    }

    @Override
    public void onAttachedToWindow()
    {
        String username = PreferenceService.getSharedPreferenceForString(getApplicationContext(),
                TC.USER_NAME);
        String passwrd = PreferenceService.getSharedPreferenceForString(getApplicationContext(),
                                                                        TC.PASSWORD);
        current_url = getIntent().getExtras().getString(TC.URLS);
        if (!TextUtils.isEmpty(username) || !TextUtils.isEmpty(passwrd))
        {
            mEmailView.setText(username);
            mPasswordView.setText(passwrd);
            attemptLogin();
        } else
        {
            showProgress(false);
        }
        super.onAttachedToWindow();
    }

    public void attemptLogin()
    {
        if (mAuthTask != null)
        {
            return;
        }
        mEmailView.setError(null);
        mPasswordView.setError(null);

        username = mEmailView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(mPassword))
        {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (mPassword.length() < 4)
        {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        if (TextUtils.isEmpty(username))
        {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (username.length() != 7)
        {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }
        if (cancel)
        {
            focusView.requestFocus();
        } else
        {
            mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
            showProgress(true);
            mAuthTask = new UserLoginTask(this);
            mAuthTask.execute(username, mPassword, current_url);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
        {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate()
                            .setDuration(shortAnimTime)
                            .alpha(show ? 1 : 0)
                            .setListener(new AnimatorListenerAdapter()
                            {
                                @Override
                                public void onAnimationEnd(Animator animation)
                                {
                                    mLoginStatusView.setVisibility(show
                                                                       ? View.VISIBLE
                                                                       : View.GONE);
                                }
                            });

            mLoginFormView.setVisibility(View.VISIBLE);
            mLoginFormView.animate()
                          .setDuration(shortAnimTime)
                          .alpha(show ? 0 : 1)
                          .setListener(new AnimatorListenerAdapter()
                          {
                              @Override
                              public void onAnimationEnd(Animator animation)
                              {
                                  mLoginFormView.setVisibility(show
                                                                   ? View.GONE
                                                                   : View.VISIBLE);
                              }
                          });
        } else
        {
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void onLoginTaskComplete(String jsonResult)
    {
        mAuthTask = null;
        showProgress(false);
        if (!TextUtils.isEmpty(jsonResult))
        {
            PreferenceService.saveSharedPreference(getApplicationContext(),
                                                   TC.USER_NAME,
                                                   username);
            PreferenceService.saveSharedPreference(getApplicationContext(),
                                                   TC.PASSWORD,
                                                   mPassword);
            Intent homePageIntent = new Intent(LoginActivity.this,
                                               ProjectsHomePage.class);
            homePageIntent.putExtra(TC.URLS, current_url);
            homePageIntent.putExtra("result", jsonResult);
            startActivity(homePageIntent);

        } else
        {
            mPasswordView.setError(getString(R.string.error_incorrect_password));
            Toast.makeText(getApplicationContext(),
                           "Failed to connect to teamcity server/Password is incorrect",
                           Toast.LENGTH_SHORT)
                 .show();
        }

    }

    public void onCancelled()
    {
        mAuthTask = null;
        showProgress(false);

    }
}

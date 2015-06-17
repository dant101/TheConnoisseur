package com.theconnoisseur.android.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.theconnoisseur.R;
import com.theconnoisseur.android.Controller.ContentDownloadController;
import com.theconnoisseur.android.Model.GlobalPreferenceString;

import Database.ConnoisseurDatabase;
import Util.ContentDownloadHelper;
import Util.ToastHelper;


/**
 * Enables users to create an account or sign in to an existing one. Users make also avoid sign in process.
 */
public class LoginActivity extends Activity {
    public static final String TAG = LoginActivity.class.getSimpleName();

    private EditText mEditEmail;
    private EditText mEditUsername;
    private EditText mEditPassword;
    private Button mEnterDetails;
    private TextView mLogin;

    private ImageView mFeedbackEmail;
    private ImageView mFeedbackUsername;
    private ImageView mFeedbackPassword;

    private String mEmail;
    private String mUsername;
    private String mPassword;

    private boolean mCreatingNewAccount = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onStart() {
        super.onStart();

        automaticLogin();

        mEditEmail = (EditText) findViewById(R.id.email);
        mEditUsername = (EditText) findViewById(R.id.username);
        mEditPassword = (EditText) findViewById(R.id.password);
        mEnterDetails = (Button) findViewById(R.id.enter_details);
        mLogin = (TextView) findViewById(R.id.login);

        mFeedbackEmail = (ImageView) findViewById(R.id.feedback_email);
        mFeedbackUsername = (ImageView) findViewById(R.id.feedback_username);
        mFeedbackPassword = (ImageView) findViewById(R.id.feedback_password);

        setListeners();
    }

    // Automatically logs in the user (based on previously saved logging credentials)
    // Alternatively, prompts the user to create an account/sign in or skip process altogether
    private void automaticLogin() {
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(GlobalPreferenceString.SIGNED_IN_PREF, false)) {
            mUsername = PreferenceManager.getDefaultSharedPreferences(this).getString(GlobalPreferenceString.USERNAME_PREF, "-- Username not found! --");
            mEmail = PreferenceManager.getDefaultSharedPreferences(this).getString(GlobalPreferenceString.EMAIL_PREF, "-- Email not found! --");

            if (ConnoisseurDatabase.getInstance().getLoginTable().login(mUsername, mPassword)) {
                Log.d(TAG, "User already logged in: Username(" + mUsername + ") Email(" + mEmail + ")");
                startMainMenuActivity();
            } else {
                Log.d(TAG, "Couldn't log user in: Username(" + mUsername + ") Email(" + mEmail + ")");
                ToastHelper.toast(this, "Sorry, we couldn't log you in at this time");
            }
        }
    }

    //Defines the specific screen element behaviours regarding touch events - in particular, email/username validation
    private void setListeners() {
        mEditEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    String input = ((EditText) v).getText().toString();
                    if(input.isEmpty()) { return; }
                    boolean unique = ConnoisseurDatabase.getInstance().getLoginTable().isEmailUnique(input);
                    boolean valid = ConnoisseurDatabase.getInstance().getLoginTable().isEmailUnique(input);

                    if(unique && valid) {
                        mFeedbackEmail.setImageResource(R.drawable.greentick);
                    } else {
                        mFeedbackEmail.setImageResource(R.drawable.redcross);
                        if (!unique) {
                            ToastHelper.toast(getApplicationContext(), getString(R.string.not_unique_email), Toast.LENGTH_LONG);
                        }
                        if(!valid) {
                            ToastHelper.toast(getApplicationContext(), getString(R.string.invalid_email), Toast.LENGTH_LONG);
                        }
                    }
                    mFeedbackEmail.setVisibility(View.VISIBLE);
                } else {
                    mFeedbackEmail.setVisibility(View.INVISIBLE);
                }
            }
        });

        mEditUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    String input = ((EditText) v).getText().toString();
                    if(input.isEmpty()) { return; }
                    boolean unique = ConnoisseurDatabase.getInstance().getLoginTable().isUserNameUnique(input);

                    if(mCreatingNewAccount) {
                        if(unique) {
                            mFeedbackUsername.setImageResource(R.drawable.greentick);
                        } else {
                            mFeedbackUsername.setImageResource(R.drawable.redcross);
                            ToastHelper.toast(getApplicationContext(), getString(R.string.not_unique_username), Toast.LENGTH_LONG);
                        }
                    } else { //If user is logging in
                        if(unique) {
                            mFeedbackUsername.setImageResource(R.drawable.redcross);
                            ToastHelper.toast(getApplicationContext(), getString(R.string.not_existing_username), Toast.LENGTH_LONG);
                        } else {
                            mFeedbackUsername.setImageResource(R.drawable.greentick);
                        }
                    }
                    mFeedbackUsername.setVisibility(View.VISIBLE);
                } else {
                    mFeedbackUsername.setVisibility(View.INVISIBLE);
                }
            }
        });

        mEditPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mFeedbackPassword.setImageResource(R.drawable.redcross);
                    mFeedbackPassword.setVisibility(View.VISIBLE);
                } else {
                    mFeedbackPassword.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    // User elects to skip the account creation/login step
    public void skipEnterDetails(View v) {
        mUsername = GlobalPreferenceString.GUEST; //TODO: give unique guest username?
        setUserSessionDetails();
        startMainMenuActivity();
    }

    // Enters the user's details, creating a new account or signing in with an existing one
    public void enterDetails(View v) {
        mEmail = mEditEmail.getText().toString();
        mUsername = mEditUsername.getText().toString();
        mPassword = mEditPassword.getText().toString();

        if(mCreatingNewAccount) {
            if(ConnoisseurDatabase.getInstance().getLoginTable().create(mUsername, mPassword, mEmail)) {
                //Successful new user account creation
                setUserSessionDetails();
                Log.d(TAG, "Created new user account: Username(" + mUsername + ") Email(" + mEmail + ")");

                startMainMenuActivity();
            } else {
                //Unable to create account
                Log.d(TAG, "Failed to create new account");
            }
        } else {
            //Logging in
            if(ConnoisseurDatabase.getInstance().getLoginTable().login(mUsername, mPassword)) {
                //Successful user sign in
                Log.d(TAG, "Successfully signed in user: Username(" + mUsername + ") Email(" + mEmail + ")");
                mFeedbackPassword.setImageResource(R.drawable.greentick);
                setUserSessionDetails();
                syncUserInformation(mUsername);
                startMainMenuActivity();
            } else {
                //Unable to login in user
                Log.d(TAG, "Failed to sign in with user credentials");
                mFeedbackPassword.setImageResource(R.drawable.redcross);
                ToastHelper.toast(getApplicationContext(), getString(R.string.wrong_password), Toast.LENGTH_LONG);
            }
        }
    }

    // Stores user's username and email for use within app
    private void setUserSessionDetails() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(GlobalPreferenceString.EMAIL_PREF, mEmail);
        editor.putString(GlobalPreferenceString.USERNAME_PREF, mUsername);
        editor.putBoolean(GlobalPreferenceString.SIGNED_IN_PREF, true);
        editor.commit();
    }

    // Proceeds user to the main menu - offering user visual feedback (?)
    private void startMainMenuActivity() {
        //UI visual feedback?
        startActivity(new Intent(LoginActivity.this, MainMenuActivity.class));
    }

    private void syncUserInformation(String username) {
        new Thread(new ContentDownloadController.syncUserInformation(this, username)).start();
    }


    // User selects to login - changes views appropriately
    public void loginToggle(View v) {
        mCreatingNewAccount = !mCreatingNewAccount;

        if(mCreatingNewAccount) {
            mEditEmail.setVisibility(View.VISIBLE);
            mFeedbackEmail.setVisibility(View.INVISIBLE);
            mEditUsername.setHint(R.string.signup_username_prompt);
            mEditPassword.setHint(R.string.signup_password_prompt);
            mEnterDetails.setText(R.string.create_account);
            mLogin.setText(R.string.login);

        } else {
            mEditEmail.setVisibility(View.GONE);
            mFeedbackEmail.setVisibility(View.GONE);
            mEditUsername.setHint(R.string.login_username_prompt);
            mEditPassword.setHint(R.string.login_password_prompt);
            mEnterDetails.setText(R.string.login);
            mLogin.setText(R.string.create_account);
        }
    }


}

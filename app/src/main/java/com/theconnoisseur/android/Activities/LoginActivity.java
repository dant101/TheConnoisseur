package com.theconnoisseur.android.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.StrictMode;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
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

    private EditText mEditUsername;
    private EditText mEditPassword;
    private EditText mEditPasswordConfirm;
    private Button mEnterDetails;
    private TextView mLogin;

    private ImageView mFeedbackUsername;
    private ImageView mFeedbackPassword;
    private ImageView mFeedbackPasswordConfirmation;

    private String mEmail;
    private String mUsername;
    private String mPassword;
    private String mPasswordConfirm;

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

        mEditPasswordConfirm = (EditText) findViewById(R.id.password_confirm);
        mEditUsername = (EditText) findViewById(R.id.username);
        mEditPassword = (EditText) findViewById(R.id.password);
        mEnterDetails = (Button) findViewById(R.id.enter_details);
        mLogin = (TextView) findViewById(R.id.login);

        mFeedbackUsername = (ImageView) findViewById(R.id.feedback_username);
        mFeedbackPassword = (ImageView) findViewById(R.id.feedback_password);
        mFeedbackPasswordConfirmation = (ImageView) findViewById(R.id.feedback_password_confirmation);

        mEditPassword.setTypeface(Typeface.DEFAULT);
        mEditPasswordConfirm.setTypeface(Typeface.DEFAULT);
        mEditPassword.setTransformationMethod(new PasswordTransformationMethod());
        mEditPasswordConfirm.setTransformationMethod(new PasswordTransformationMethod());

        setListeners();
    }

    // Automatically logs in the user (based on previously saved logging credentials)
    // Alternatively, prompts the user to create an account/sign in or skip process altogether
    private void automaticLogin() {
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(GlobalPreferenceString.SIGNED_IN_PREF, false)) {
            mUsername = PreferenceManager.getDefaultSharedPreferences(this).getString(GlobalPreferenceString.USERNAME_PREF, "-- Username not found! --");
            mEmail = PreferenceManager.getDefaultSharedPreferences(this).getString(GlobalPreferenceString.EMAIL_PREF, "-- Email not found! --");
            mPassword = PreferenceManager.getDefaultSharedPreferences(this).getString(GlobalPreferenceString.PASSWORD_PREF, "-- Password not found --");

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
        mEditUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    String input = ((EditText) v).getText().toString();
                    if(input.isEmpty()) { return; }
                    boolean unique = ConnoisseurDatabase.getInstance().getLoginTable().isUserNameUnique(input);

                    if(mCreatingNewAccount) {
                        if(unique) {
                            mFeedbackUsername.setImageResource(R.drawable.tick_correct);
                        } else {
                            mFeedbackUsername.setImageResource(R.drawable.cross_wrong);
                            ToastHelper.toast(getApplicationContext(), getString(R.string.not_unique_username), Toast.LENGTH_LONG);
                        }
                    } else { //If user is logging in
                        if(unique) {
                            mFeedbackUsername.setImageResource(R.drawable.cross_wrong);
                            ToastHelper.toast(getApplicationContext(), getString(R.string.not_existing_username), Toast.LENGTH_LONG);
                        } else {
                            mFeedbackUsername.setImageResource(R.drawable.tick_correct);
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
                    if (mEditPassword.length() > 0) {
                        //Other password requirements?
                        mFeedbackPassword.setImageResource(R.drawable.tick_correct);
                    } else {
                        mFeedbackPassword.setImageResource(R.drawable.cross_wrong);
                    }
                    mFeedbackPassword.setVisibility(View.VISIBLE);

                } else {
                    mFeedbackPassword.setVisibility(View.INVISIBLE);
                }
            }
        });

        mEditPasswordConfirm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    if(mEditPassword.length() > 0 && mEditPassword.getText().toString().equals(mEditPasswordConfirm.getText().toString())) {
                        mFeedbackPasswordConfirmation.setImageResource(R.drawable.tick_correct);
                    } else {
                        mFeedbackPasswordConfirmation.setImageResource(R.drawable.cross_wrong);
                        ToastHelper.toast(getApplicationContext(), "Your passwords don't match");
                    }
                    mFeedbackPasswordConfirmation.setVisibility(View.VISIBLE);
                } else {
                    mFeedbackPasswordConfirmation.setVisibility(View.INVISIBLE);
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
        mUsername = mEditUsername.getText().toString();
        mPassword = mEditPassword.getText().toString();
        mPasswordConfirm = mEditPasswordConfirm.getText().toString();

        if(mCreatingNewAccount) {
            mEmail = ""; //No emails for now

            if (mPassword.equals(mPasswordConfirm)) {
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
                ToastHelper.toast(getApplicationContext(), "Your passwords do not match");
            }
        } else {
            //Logging in
            if(ConnoisseurDatabase.getInstance().getLoginTable().login(mUsername, mPassword)) {
                //Successful user sign in
                Log.d(TAG, "Successfully signed in user: Username(" + mUsername + ") Email(" + mEmail + ")");
                mFeedbackPassword.setImageResource(R.drawable.tick_correct);
                setUserSessionDetails();
                syncUserInformation(mUsername);
                startMainMenuActivity();
            } else {
                //Unable to login in user
                Log.d(TAG, "Failed to sign in with user credentials");
                mFeedbackPassword.setImageResource(R.drawable.cross_wrong);
                ToastHelper.toast(getApplicationContext(), getString(R.string.wrong_password), Toast.LENGTH_LONG);
            }
        }
    }

    // Stores user's username and email for use within app
    private void setUserSessionDetails() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(GlobalPreferenceString.USERNAME_PREF, mUsername);
        editor.putString(GlobalPreferenceString.PASSWORD_PREF, mPassword);
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
            mEditPasswordConfirm.setVisibility(View.VISIBLE);
            mFeedbackPasswordConfirmation.setVisibility(View.INVISIBLE);
            mEditUsername.setHint(R.string.signup_username_prompt);
            mEditPassword.setHint(R.string.signup_password_prompt);
            mEnterDetails.setText(R.string.create_account);
            mLogin.setText(R.string.login);

        } else {
            mEditPasswordConfirm.setVisibility(View.GONE);
            mFeedbackPasswordConfirmation.setVisibility(View.GONE);
            mEditUsername.setHint(R.string.login_username_prompt);
            mEditPassword.setHint(R.string.login_password_prompt);
            mEnterDetails.setText(R.string.login);
            mLogin.setText(R.string.create_account);
        }
    }


}

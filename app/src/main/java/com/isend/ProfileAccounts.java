package com.isend;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;

public class ProfileAccounts extends Fragment {

    LoginButton loginButton;
    CallbackManager callbackManager;
    SharedPreferences prefs;
    boolean isFacebookSync, isOutlookSync;
    SQLiteDatabase database_account;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.profile_accounts, container, false);

        prefs = getActivity().getSharedPreferences("Profile", MODE_PRIVATE);
        isFacebookSync = prefs.getBoolean("Facebook", false);
        isOutlookSync = prefs.getBoolean("Outlook", false);

        // Create local database to save events
        database_account = getActivity().openOrCreateDatabase("database_app", MODE_PRIVATE, null);

        //  FACEBOOK CALENDAR
        loginButton = v.findViewById(R.id.account_facebook);

        //  OUTLOOK CALENDAR
        outlookCalendar();

        View.OnClickListener buttonListener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.account_facebook:
                        if (!isFacebookSync) {
                            facebookCalendar();
                        } else {
                            //Cancel alarmmanager of facebook and remove all facebook events
                        }
                        break;
                }
            }
        };
        loginButton.setOnClickListener(buttonListener);

        return v;
    }
    void facebookCalendar() {
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("email");
        loginButton.setReadPermissions("user_events");
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getActivity(), "Connection successful:" + loginResult, Toast.LENGTH_SHORT).show();
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/{user-id}/events",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                prefs.edit().putBoolean("Facebook", true).apply();
                                System.out.println(response);
                                getActivity().finish();
                                startActivity(getActivity().getIntent());
                            }
                        }
                ).executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getActivity(), getString(R.string.error_aborted), Toast.LENGTH_SHORT).show();
                prefs.edit().putBoolean("Facebook", false).apply();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(getActivity(), "Connection error: " + e.toString(), Toast.LENGTH_SHORT).show();
                prefs.edit().putBoolean("Facebook", false).apply();
            }
        });
    }

    void outlookCalendar() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        System.out.println(data);
    }
}
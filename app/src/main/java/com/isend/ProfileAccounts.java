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

    ImageView device;
    LoginButton loginButton;
    TextView deviceText, facebookText;
    CallbackManager callbackManager;
    private static final int REQUEST_CALENDAR_READ = 0;
    SharedPreferences prefs;
    boolean isDeviceSync, isFacebookSync, isOutlookSync;
    SQLiteDatabase database_account;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.profile_accounts, container, false);

        prefs = getActivity().getSharedPreferences("Profile", MODE_PRIVATE);
        isDeviceSync = prefs.getBoolean("Device", false);
        isFacebookSync = prefs.getBoolean("Facebook", false);
        isOutlookSync = prefs.getBoolean("Outlook", false);

        // Create local database to save events
        database_account = getActivity().openOrCreateDatabase("database_app", MODE_PRIVATE, null);

        // DEVICE CALENDAR
        device = v.findViewById(R.id.account_device);
        deviceText = v.findViewById(R.id.deviceSync);

        if (isDeviceSync) {
            deviceText.setText(getString(R.string.account_synchronized));
            deviceText.setTextColor(Color.GREEN);
        } else {
            deviceText.setText(getString(R.string.account_not_synchronized));
            deviceText.setTextColor(Color.BLACK);
        }

        //  FACEBOOK CALENDAR
        loginButton = v.findViewById(R.id.account_facebook);
        facebookText = v.findViewById(R.id.facebookSync);

        if (isFacebookSync) {
            facebookText.setText(getString(R.string.account_synchronized));
            facebookText.setTextColor(Color.GREEN);
        } else {
            facebookText.setText(getString(R.string.account_not_synchronized));
            facebookText.setTextColor(Color.BLACK);
        }

        //  OUTLOOK CALENDAR
        outlookCalendar();

        View.OnClickListener buttonListener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.account_device:
                        if (!isDeviceSync) {
                            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CALENDAR}, REQUEST_CALENDAR_READ);
                            } else {
                                deviceCalendar();
                            }
                        } else {
                            Cursor cur = database_account.rawQuery("DELETE FROM events WHERE Source='device'", null);
                            cur.close();
                            Toast.makeText(getActivity(), getString(R.string.permission_calendar_revoked), Toast.LENGTH_SHORT).show();
                            prefs.edit().putBoolean("Device", false).apply();
                            getActivity().finish();
                            startActivity(getActivity().getIntent());
                            //Cancel alarmmanager of device and remove all device events
                        }
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
        device.setOnClickListener(buttonListener);
        loginButton.setOnClickListener(buttonListener);

        return v;
    }

    @SuppressWarnings("MissingPermission")
    void deviceCalendar() {
        Cursor cur;
        ContentResolver cr = getActivity().getContentResolver();

        String[] mProjection =
                {
                        "_id",
                        CalendarContract.Events.TITLE,
                        CalendarContract.Events.DESCRIPTION,
                        CalendarContract.Events.DTSTART,
                        CalendarContract.Events.DTEND,
                        CalendarContract.Events.EVENT_LOCATION,
                        CalendarContract.Events.ORGANIZER,
                        CalendarContract.Events.DISPLAY_COLOR
                };

        Uri uri = CalendarContract.Events.CONTENT_URI;
        cur = cr.query(uri, mProjection, null, null, null);

        if (cur != null) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(CalendarContract.Events._ID));
                String title = cur.getString(cur.getColumnIndex(CalendarContract.Events.TITLE));
                String desc = cur.getString(cur.getColumnIndex(CalendarContract.Events.DESCRIPTION));
                String start = cur.getString(cur.getColumnIndex(CalendarContract.Events.DTSTART));
                String end = cur.getString(cur.getColumnIndex(CalendarContract.Events.DTEND));
                String location = cur.getString(cur.getColumnIndex(CalendarContract.Events.EVENT_LOCATION));
                String owner = cur.getString(cur.getColumnIndex(CalendarContract.Events.ORGANIZER));
                String color = cur.getString(cur.getColumnIndex(CalendarContract.Events.DISPLAY_COLOR));

                System.out.println("ID: " + id + "Etkinlik adı:" + title + "Açıklama:" + desc + "saat:" + start + "-" + end + "konum:" + location + "owner:" + owner + "color:" + color);

                ContentValues values = new ContentValues();
                values.put("ID", id);
                values.put("Title", title);
                values.put("Description", desc);
                values.put("Start", getDate(start));
                values.put("End", getDate(end));
                values.put("Location", location);
                values.put("Owner", owner);
                values.put("Color", color);
                values.put("Source", "Device");
                database_account.insert("events", null, values);
            }
            cur.close();
            Toast.makeText(getActivity(), getString(R.string.permission_calendar_granted), Toast.LENGTH_SHORT).show();
            prefs.edit().putBoolean("Device", true).apply();
            getActivity().finish();
            startActivity(getActivity().getIntent());
        } else {
            prefs.edit().putBoolean("Device", false).apply();
            Toast.makeText(getActivity(), getString(R.string.error_no_calendar), Toast.LENGTH_SHORT).show();
        }
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CALENDAR_READ: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    deviceCalendar();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.error_aborted), Toast.LENGTH_SHORT)
                            .show();
                    prefs.edit().putBoolean("Device", false).apply();
                }
                break;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        System.out.println(data);
    }

    private String getDate(String time) {
        Date date = new Date(Long.parseLong(time));
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm",
                java.util.Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC+3"));
        return sdf.format(date);
    }
}
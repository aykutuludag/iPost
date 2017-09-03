package com.isend;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;

public class ProfileAccounts extends Fragment {

    CallbackManager callbackManager;
    private static final int REQUEST_CALENDAR_READ = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.profile_accounts, container, false);

        // GOOGLE CALENDAR
        SignInButton google = v.findViewById(R.id.account_google);
        View.OnClickListener buttonListener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.account_google:
                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CALENDAR}, REQUEST_CALENDAR_READ);
                        } else {
                            googleCalendar();
                        }
                        break;
                }
            }
        };
        google.setOnClickListener(buttonListener);


        //  FACEBOOK CALENDAR
        callbackManager = CallbackManager.Factory.create();
        final LoginButton loginButton = v.findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getActivity(), "Connection successful:" + loginResult, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getActivity(), getString(R.string.error_aborted), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(getActivity(), "Connection error: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        //  OUTLOOK CALENDAR

        return v;
    }

    @SuppressWarnings("MissingPermission")
    void googleCalendar() {
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
                        CalendarContract.Events.ORGANIZER
                };

        Uri uri = CalendarContract.Events.CONTENT_URI;
        cur = cr.query(uri, mProjection, null, null, null);

        if (cur != null) {
            while (cur.moveToNext()) {
                String title = cur.getString(cur.getColumnIndex(CalendarContract.Events.TITLE));
                String desc = cur.getString(cur.getColumnIndex(CalendarContract.Events.DESCRIPTION));
                String start = cur.getString(cur.getColumnIndex(CalendarContract.Events.DTSTART));
                String end = cur.getString(cur.getColumnIndex(CalendarContract.Events.DTEND));
                String location = cur.getString(cur.getColumnIndex(CalendarContract.Events.EVENT_LOCATION));
                String owner = cur.getString(cur.getColumnIndex(CalendarContract.Events.ORGANIZER));

                System.out.println("Etkinlik adı:" + title + "Açıklama:" + desc + "saat:" + start + "-" + end + "konum:" + location + "owner:" + owner);
            }
            cur.close();
            Toast.makeText(getActivity(), getString(R.string.permission_calendar_granted), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), getString(R.string.error_no_calendar), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CALENDAR_READ: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    googleCalendar();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.error_aborted), Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
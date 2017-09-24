package app.isend;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import static android.content.Context.MODE_PRIVATE;

public class ProfileAccounts extends Fragment {

    SQLiteDatabase database_account;
    Tracker t;

    ImageView facebook, google, outlook, iCalendar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.profile_accounts, container, false);

        // Analytics
        t = ((AnalyticsApplication) getActivity().getApplication()).getDefaultTracker();
        t.setScreenName("Profile - Accounts");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        facebook = v.findViewById(R.id.facebookCalendar);
        google = v.findViewById(R.id.googleCalendar);
        outlook = v.findViewById(R.id.outlookCalendar);
        iCalendar = v.findViewById(R.id.iCalendar);

        if (appInstalledOrNot("com.google.android.calendar")) {
            google.setAlpha(1.0f);
        } else {
            google.setAlpha(0.5f);
        }

        if (appInstalledOrNot("com.microsoft.office.outlook")) {
            outlook.setAlpha(1.0f);
        } else {
            outlook.setAlpha(0.5f);
        }

        if (appInstalledOrNot("com.mike.cal.sync")) {
            iCalendar.setAlpha(1.0f);
        } else {
            iCalendar.setAlpha(0.5f);
        }

        // Create local database to save events
        database_account = getActivity().openOrCreateDatabase("database_app", MODE_PRIVATE, null);

        View.OnClickListener buttonListener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.facebookCalendar:
                        facebookCalendar();
                        break;
                    case R.id.googleCalendar:
                        googleCalendar();
                        break;
                    case R.id.outlookCalendar:
                        outlookCalendar();
                        break;
                    case R.id.iCalendar:
                        iCalendar();
                        break;
                }
            }
        };
        facebook.setOnClickListener(buttonListener);
        google.setOnClickListener(buttonListener);
        outlook.setOnClickListener(buttonListener);
        iCalendar.setOnClickListener(buttonListener);

        return v;
    }

    void facebookCalendar() {
        //Coming soon
    }

    void googleCalendar() {
        if (appInstalledOrNot("com.google.android.calendar")) {
            //Start resync local calendar
            google.setAlpha(1.0f);
            Toast.makeText(getActivity(), "Sync completed.", Toast.LENGTH_LONG).show();
        } else {
            google.setAlpha(0.5f);
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.calendar")));
            Toast.makeText(getActivity(), "After installing the app, your events will be imported to iSend within an hour.", Toast.LENGTH_LONG).show();
        }
    }

    void outlookCalendar() {
        if (appInstalledOrNot("com.microsoft.office.outlook")) {
            //Start resync local calendar
            outlook.setAlpha(1.0f);
            Toast.makeText(getActivity(), "Sync completed.", Toast.LENGTH_LONG).show();
        } else {
            outlook.setAlpha(0.5f);
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.microsoft.office.outlook")));
            Toast.makeText(getActivity(), "After installing the app, your events will be imported to iSend within an hour.", Toast.LENGTH_LONG).show();
        }
    }

    void iCalendar() {
        if (appInstalledOrNot("com.mike.cal.sync")) {
            //Start resync local calendar
            iCalendar.setAlpha(1.0f);
            Toast.makeText(getActivity(), "Sync completed.", Toast.LENGTH_LONG).show();
        } else {
            iCalendar.setAlpha(0.5f);
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.mike.cal.sync")));
            Toast.makeText(getActivity(), "After installing the app, your events will be imported to iSend within an hour.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getActivity().getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
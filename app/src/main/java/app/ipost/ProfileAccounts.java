package app.ipost;

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

    ImageView facebook, google, outlook, apple;
    ImageView facebookI, googleI, outlookI, appleI;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.profile_accounts, container, false);

        // Analytics
        t = ((AnalyticsApplication) getActivity().getApplication()).getDefaultTracker();
        t.setScreenName("Profile - Accounts");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        facebook = v.findViewById(R.id.facebook);
        google = v.findViewById(R.id.google);
        outlook = v.findViewById(R.id.outlook);
        apple = v.findViewById(R.id.apple);

        facebookI = v.findViewById(R.id.facebookIndicator);
        googleI = v.findViewById(R.id.googleIndicator);
        outlookI = v.findViewById(R.id.outlookIndicator);
        appleI = v.findViewById(R.id.appleIndicator);

        if (appInstalledOrNot("com.facebook.katana")) {
            facebookI.setBackgroundResource(R.drawable.actie);
        } else {
            facebookI.setBackgroundResource(R.drawable.dis);
        }

        if (appInstalledOrNot("com.google.android.calendar")) {
            googleI.setBackgroundResource(R.drawable.actie);
        } else {
            googleI.setBackgroundResource(R.drawable.dis);
        }

        if (appInstalledOrNot("com.microsoft.office.outlook")) {
            outlookI.setBackgroundResource(R.drawable.actie);
        } else {
            outlookI.setBackgroundResource(R.drawable.dis);
        }

        if (appInstalledOrNot("com.mike.cal.sync")) {
            appleI.setBackgroundResource(R.drawable.actie);
        } else {
            appleI.setBackgroundResource(R.drawable.dis);
        }

        // Create local database to save events
        database_account = getActivity().openOrCreateDatabase("database_app", MODE_PRIVATE, null);

        View.OnClickListener buttonListener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.facebook:
                        facebookCalendar();
                        break;
                    case R.id.google:
                        googleCalendar();
                        break;
                    case R.id.outlook:
                        outlookCalendar();
                        break;
                    case R.id.apple:
                        iCalendar();
                        break;
                }
            }
        };

        facebook.setOnClickListener(buttonListener);
        google.setOnClickListener(buttonListener);
        outlook.setOnClickListener(buttonListener);
        apple.setOnClickListener(buttonListener);

        return v;
    }

    void facebookCalendar() {
        //Coming soon
    }

    void googleCalendar() {
        if (appInstalledOrNot("com.google.android.calendar")) {
            //Start resync local calendar
            googleI.setBackgroundResource(R.drawable.actie);
            Toast.makeText(getActivity(), "Sync completed.", Toast.LENGTH_LONG).show();
        } else {
            googleI.setBackgroundResource(R.drawable.dis);
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.calendar")));
            Toast.makeText(getActivity(), "After installing the app, your events will be imported to iSend within an hour.", Toast.LENGTH_LONG).show();
        }
    }

    void outlookCalendar() {
        if (appInstalledOrNot("com.microsoft.office.outlook")) {
            //Start resync local calendar
            outlookI.setBackgroundResource(R.drawable.actie);
            Toast.makeText(getActivity(), "Sync completed.", Toast.LENGTH_LONG).show();
        } else {
            outlookI.setBackgroundResource(R.drawable.dis);
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.microsoft.office.outlook")));
            Toast.makeText(getActivity(), "After installing the app, your events will be imported to iSend within an hour.", Toast.LENGTH_LONG).show();
        }
    }

    void iCalendar() {
        if (appInstalledOrNot("com.mike.cal.sync")) {
            //Start resync local calendar
            appleI.setBackgroundResource(R.drawable.actie);
            Toast.makeText(getActivity(), "Sync completed.", Toast.LENGTH_LONG).show();
        } else {
            appleI.setBackgroundResource(R.drawable.dis);
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
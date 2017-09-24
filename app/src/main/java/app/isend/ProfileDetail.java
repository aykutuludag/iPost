package app.isend;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class ProfileDetail extends Fragment {

    Tracker t;
    SharedPreferences prefs;
    boolean isCalendarSync, isContactSync;
    TextView calendarStatus, contactsStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_detail, container, false);

        // Analytics
        t = ((AnalyticsApplication) getActivity().getApplication()).getDefaultTracker();
        t.setScreenName("Profile - Detail");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        calendarStatus = v.findViewById(R.id.calendar_status);
        contactsStatus = v.findViewById(R.id.contacts_status);

        prefs = getActivity().getSharedPreferences("ProfileInformation", Context.MODE_PRIVATE);
        isCalendarSync = prefs.getBoolean("isCalendarSync", false);
        isContactSync = prefs.getBoolean("isContactSync", false);

        if (isCalendarSync) {
            calendarStatus.setText("Sync");
        } else {
            calendarStatus.setText("Not sync");
        }

        if (isContactSync) {
            contactsStatus.setText("Sync");
        } else {
            contactsStatus.setText("Not sync");
        }

        return v;
    }
}

package app.ipost;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class ProfileDetail extends Fragment {

    Tracker t;
    SharedPreferences prefs;
    Button deleteEvents, deleteContacts;
    ExpandableRelativeLayout expandableLayout1, expandableLayout2, expandableLayout3;
    CheckedTextView facebook, google;
    Button expButton1, expButton2, expButton3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_detail, container, false);

        // Analytics
        t = AnalyticsApplication.getDefaultTracker();
        t.setScreenName("Profile - Detail");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        expandableLayout1 = v.findViewById(R.id.expandableLayout1);
        expandableLayout2 = v.findViewById(R.id.expandableLayout2);
        expandableLayout3 = v.findViewById(R.id.expandableLayout3);

        expButton1 = v.findViewById(R.id.expandableButton1);
        expButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandableButton1(view);
            }
        });

        expButton2 = v.findViewById(R.id.expandableButton2);
        expButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandableButton2(view);
            }
        });

        expButton3 = v.findViewById(R.id.expandableButton3);
        expButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandableButton3(view);
            }
        });

        deleteEvents = v.findViewById(R.id.sync_event);
        deleteEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resyncEvents();
            }
        });

        deleteContacts = v.findViewById(R.id.sync_contact);
        deleteContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resyncContacts();
            }
        });

        facebook = v.findViewById(R.id.facebookLogged);
        if (MainActivity.fID != null && !MainActivity.fID.isEmpty()) {
            facebook.setChecked(true);
        } else {
            facebook.setChecked(false);
        }

        google = v.findViewById(R.id.googleLogged);
        if (MainActivity.ID != null && !MainActivity.ID.isEmpty()) {
            google.setChecked(true);
        } else {
            google.setChecked(false);
        }

        return v;
    }

    public void resyncEvents() {

    }

    public void resyncContacts() {

    }

    public void expandableButton1(View view) {
        expandableLayout1.toggle(); // toggle expand and collapse
        expandableLayout2.collapse();
        expandableLayout3.collapse();
    }

    public void expandableButton2(View view) {
        expandableLayout2.toggle(); // toggle expand and collapse
        expandableLayout1.collapse();
        expandableLayout3.collapse();
    }

    public void expandableButton3(View view) {
        expandableLayout3.toggle(); // toggle expand and collapse
        expandableLayout1.collapse();
        expandableLayout2.collapse();
    }

}

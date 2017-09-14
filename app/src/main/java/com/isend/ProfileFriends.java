package com.isend;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFriends extends Fragment {

    Tracker t;
    SQLiteDatabase database_account;
    SharedPreferences prefs;
    Boolean isSync;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_friends, container, false);

        // Analytics
        t = ((AnalyticsApplication) getActivity().getApplication()).getDefaultTracker();
        t.setScreenName("Profile / Friends");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        prefs = getActivity().getSharedPreferences("Profile", Context.MODE_PRIVATE);
        isSync = prefs.getBoolean("isSync", false);

        // Create local database to save contacs
        database_account = getActivity().openOrCreateDatabase("database_app", MODE_PRIVATE, null);
        database_account.execSQL("CREATE TABLE IF NOT EXISTS contacts(ID TEXT, DisplayName TEXT, PhoneNumber VARCHAR, ProfilePhoto VARCHAR, hasWhatsapp VARCHAR, hasMessenger VARCHAR, hasMail VARCHAR);");

        if (isSync) {
//Show friends
        } else {
            //Toast
        }

        return v;
    }
}

package app.ipost;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;

import app.ipost.adapter.ContactAdapter;
import app.ipost.model.ContactItem;

import static android.content.Context.MODE_PRIVATE;

public class ProfileContacts extends Fragment {

    Tracker t;
    SQLiteDatabase database_account;
    Cursor cur;
    SharedPreferences prefs;
    ContactItem item;
    RecyclerView mRecyclerView;
    GridLayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_contacts, container, false);

        // Analytics
        t = AnalyticsApplication.getDefaultTracker();
        t.setScreenName("Profile - Contacts");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        prefs = getActivity().getSharedPreferences("ProfileInformation", Context.MODE_PRIVATE);

        mRecyclerView = v.findViewById(R.id.contactView);

        List<ContactItem> feedsList = new ArrayList<>();
        database_account = getActivity().openOrCreateDatabase("database_app", MODE_PRIVATE, null);
        cur = database_account.rawQuery("SELECT * FROM contacts ORDER BY displayName ASC", null);
        if (cur != null && cur.getCount() != 0) {
            cur.moveToFirst();
            do {
                for (int i = 0; i < cur.getColumnCount(); i++) {
                    switch (i % 7) {
                        case 0:
                            item = new ContactItem();
                            item.setID(cur.getString(i));
                            break;
                        case 1:
                            item.setName(cur.getString(i));
                            break;
                        case 2:
                            item.setPhoneNumber(cur.getString(i));
                            break;
                        case 3:
                            item.setMail(cur.getString(i));
                            break;
                        case 4:
                            item.setWhastaspp(cur.getInt(i));
                            break;
                        case 5:
                            item.setMessenger(cur.getInt(i));
                            break;
                        case 6:
                            item.setContactPhoto(cur.getString(i));
                            feedsList.add(item);
                            break;
                    }
                }
            } while (cur.moveToNext());
            cur.close();
        } else {
            // Error no contact found
            Toast.makeText(getActivity(), "No friends", Toast.LENGTH_LONG).show();
        }

        // Adapter
        mAdapter = new ContactAdapter(getActivity(), feedsList);
        mRecyclerView.setAdapter(mAdapter);

        // The number of Columns
        mLayoutManager = new GridLayoutManager(getActivity(), 1);
        mRecyclerView.setLayoutManager(mLayoutManager);

        return v;
    }
}

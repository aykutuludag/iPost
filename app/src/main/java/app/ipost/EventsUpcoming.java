package app.ipost;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.ipost.adapter.EventAdapter;
import app.ipost.model.EventItem;

import static android.content.Context.MODE_PRIVATE;
import static app.ipost.MainActivity.email;
import static app.ipost.MainActivity.name;
import static app.ipost.MainActivity.photo;

public class EventsUpcoming extends Fragment {

    RecyclerView mRecyclerView;
    RelativeLayout headerProfile;
    GridLayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    Tracker t;
    EventItem item;
    SQLiteDatabase database_account;
    Cursor cur;
    List<EventItem> feedsList;
    SwipeRefreshLayout swipeContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.events_general, container, false);

        // Analytics
        t = AnalyticsApplication.getDefaultTracker();
        t.setScreenName("Events - Upcoming");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        headerProfile = v.findViewById(R.id.header_profile);
        headerProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ProfileActivity.class);
                startActivity(i);
            }
        });

        //Name
        TextView navUsername = v.findViewById(R.id.nav_name);
        navUsername.setText(name);
        //E-mail
        TextView navEmail = v.findViewById(R.id.nav_email);
        navEmail.setText(email);
        //ProfilePicture
        ImageView profilePic = v.findViewById(R.id.nav_picture);
        Picasso.with(getActivity()).load(photo).error(R.drawable.ic_error).placeholder(R.drawable.ic_error)
                .into(profilePic);
        //Background

        //Bunları temaya göre ayarla

        swipeContainer = v.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullEvents();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mRecyclerView = v.findViewById(R.id.eventView);
        feedsList = new ArrayList<>();
        pullEvents();

        return v;
    }

    public void pullEvents() {
        //Clear the list
        feedsList.clear();

        database_account = getActivity().openOrCreateDatabase("database_app", MODE_PRIVATE, null);
        cur = database_account.rawQuery("SELECT * FROM events WHERE sTime >= " + System.currentTimeMillis() + " ORDER BY sTime ASC", null);
        if (cur != null && cur.getCount() != 0) {
            cur.moveToFirst();
            do {
                for (int i = 0; i < cur.getColumnCount(); i++) {
                    switch (i % 12) {
                        case 0:
                            item = new EventItem();
                            item.setID(cur.getInt(i));
                            System.out.println(cur.getInt(i));
                            break;
                        case 1:
                            item.setTitle(cur.getString(i));
                            break;
                        case 2:
                            item.setDescription(cur.getString(i));
                            break;
                        case 3:
                            item.setStartTime(cur.getLong(i));
                            break;
                        case 4:
                            item.setEndTime(cur.getLong(i));
                            break;
                        case 5:
                            item.setLocation(cur.getString(i));
                            break;
                        case 6:
                            item.setOwner(cur.getInt(i));
                            break;
                        case 7:
                            item.setBackground(cur.getInt(i));
                            break;
                        case 8:
                            item.setIsMailActive(cur.getInt(i));
                            break;
                        case 9:
                            item.setIsSMSActive(cur.getInt(i));
                            break;
                        case 10:
                            item.setIsMessengerActive(cur.getInt(i));
                            break;
                        case 11:
                            item.setIsWhatsappActive(cur.getInt(i));
                            feedsList.add(item);
                            break;
                    }
                }
            } while (cur.moveToNext());
            cur.close();
            database_account.close();
        } else {
            //First opening
            Toast.makeText(getActivity(), "There is no events", Toast.LENGTH_LONG).show();
        }

        // Adapter
        mAdapter = new EventAdapter(getActivity(), feedsList);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapter);

        // The number of Columns
        mLayoutManager = new GridLayoutManager(getActivity(), 1);
        mRecyclerView.setLayoutManager(mLayoutManager);

        swipeContainer.setRefreshing(false);
    }
}

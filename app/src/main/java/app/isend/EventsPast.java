package app.isend;

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

import app.isend.adapter.EventsAdapter;
import app.isend.model.EventItem;

import static android.content.Context.MODE_PRIVATE;

public class EventsPast extends Fragment {

    RecyclerView mRecyclerView;
    GridLayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    Tracker t;
    EventItem item;
    SQLiteDatabase database_account;
    Cursor cur;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.events_general, container, false);

        // Analytics
        t = ((AnalyticsApplication) getActivity().getApplication()).getDefaultTracker();
        t.setScreenName("Events - Past");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        mRecyclerView = v.findViewById(R.id.eventView);
        List<EventItem> feedsList = new ArrayList<>();
        database_account = getActivity().openOrCreateDatabase("database_app", MODE_PRIVATE, null);
        cur = database_account.rawQuery("SELECT * FROM events WHERE Start < " + System.currentTimeMillis() + " ORDER BY Start DESC", null);
        if (cur != null && cur.getCount() != 0) {
            cur.moveToFirst();
            do {
                for (int i = 0; i < cur.getColumnCount(); i++) {
                    System.out.println(cur.getString(i));
                    if ((i % 9) == 0) {
                        item = new EventItem();
                        item.setID(cur.getString(i));
                    } else if (((i % 9) == 1)) {
                        item.setTitle(cur.getString(i));
                    } else if (((i % 9) == 2)) {
                        item.setDescription(cur.getString(i));
                    } else if (((i % 9) == 3)) {
                        item.setStartTime(cur.getLong(i));
                    } else if (((i % 9) == 4)) {
                        item.setEndTime(cur.getLong(i));
                    } else if (((i % 9) == 5)) {
                        item.setLocation(cur.getString(i));
                    } else if (((i % 9) == 6)) {
                        item.setOwner(cur.getString(i));
                    } else if (((i % 9) == 7)) {
                        item.setBackground(cur.getString(i));
                    } else if (((i % 9) == 8)) {
                        item.setSource(cur.getString(i));
                        feedsList.add(item);
                    } else {
                        //Do nothing
                    }
                }
            } while (cur.moveToNext());
        } else {
            //First opening
            Toast.makeText(getActivity(), "There is no events", Toast.LENGTH_LONG).show();
        }
        cur.close();

        // Adapter
        mAdapter = new EventsAdapter(getActivity(), feedsList);
        mRecyclerView.setAdapter(mAdapter);

        // The number of Columns
        mLayoutManager = new GridLayoutManager(getActivity(), 1);
        mRecyclerView.setLayoutManager(mLayoutManager);

        return v;
    }
}

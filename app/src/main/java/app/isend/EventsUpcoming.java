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

public class EventsUpcoming extends Fragment {

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
        t.setScreenName("Events - Upcoming");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        mRecyclerView = v.findViewById(R.id.eventView);
        List<EventItem> feedsList = new ArrayList<>();
        database_account = getActivity().openOrCreateDatabase("database_app", MODE_PRIVATE, null);
        cur = database_account.rawQuery("SELECT * FROM events WHERE Start >= " + System.currentTimeMillis() + " ORDER BY Start ASC", null);
        if (cur != null && cur.getCount() != 0) {
            cur.moveToFirst();
            do {
                for (int i = 0; i < cur.getColumnCount(); i++) {
                    switch (i % 13) {
                        case 0:
                            item = new EventItem();
                            item.setID(cur.getString(i));
                            break;
                        case 1:
                            item.setTitle(cur.getString(i));
                            break;
                        case 2:
                            item.setPhoto(cur.getString(i));
                            break;
                        case 3:
                            item.setDescription(cur.getString(i));
                            break;
                        case 4:
                            item.setStartTime(cur.getLong(i));
                            break;
                        case 5:
                            item.setEndTime(cur.getLong(i));
                            break;
                        case 6:
                            item.setLocation(cur.getString(i));
                            break;
                        case 7:
                            item.setOwner(cur.getString(i));
                            break;
                        case 8:
                            item.setBackground(cur.getString(i));
                            break;
                        case 9:
                            item.setIsMailActive(cur.getInt(i));
                            break;
                        case 10:
                            item.setIsSMSActive(cur.getInt(i));
                            break;
                        case 11:
                            item.setIsMessengerActive(cur.getInt(i));
                            break;
                        case 12:
                            item.setIsWhatsappActive(cur.getInt(i));
                            feedsList.add(item);
                            break;
                    }
                }
            } while (cur.moveToNext());
            cur.close();
        } else {
            //First opening
            Toast.makeText(getActivity(), "There is no events", Toast.LENGTH_LONG).show();
        }

        // Adapter
        mAdapter = new EventsAdapter(getActivity(), feedsList);
        mRecyclerView.setAdapter(mAdapter);

        // The number of Columns
        mLayoutManager = new GridLayoutManager(getActivity(), 1);
        mRecyclerView.setLayoutManager(mLayoutManager);

        return v;
    }
}

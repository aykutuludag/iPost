package com.isend;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.isend.adapter.RecyclerViewAdapter;
import com.isend.model.EventItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EventsToday extends Fragment {

    RecyclerView mRecyclerView;
    GridLayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    Tracker t;
    EventItem item;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.events_general, container, false);

        // Analytics
        t = ((AnalyticsApplication) getActivity().getApplication()).getDefaultTracker();
        t.setScreenName("Today's events");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        mRecyclerView = v.findViewById(R.id.recyclerView);
        List<EventItem> feedsList = new ArrayList<>();

        String path = Environment.getExternalStorageDirectory().toString() + "/Günlük Burçlar/Favoriler";
        File f = new File(path);
        File file[] = f.listFiles();
        if (file == null || file.length == 0) {
            Toast.makeText(getActivity(), "Henüz favorilerinize hiç yorum eklememişsiniz.", Toast.LENGTH_SHORT).show();
        } else {
            for (File aFile : file) {
                item = new EventItem();
                item.setThumbnail(BitmapFactory.decodeFile(aFile.getAbsolutePath()));
                item.setTitle(aFile.getName());
                feedsList.add(item);
            }
        }

        // Adapter
        mAdapter = new RecyclerViewAdapter(getActivity(), feedsList);
        mRecyclerView.setAdapter(mAdapter);

        // The number of Columns
        mLayoutManager = new GridLayoutManager(getActivity(), 1);
        mRecyclerView.setLayoutManager(mLayoutManager);

        return v;
    }
}

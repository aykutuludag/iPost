package app.ipost;

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
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;

import app.ipost.adapter.PostAdapter;
import app.ipost.model.PostItem;

import static android.content.Context.MODE_PRIVATE;

public class PostPlanned extends Fragment {

    RecyclerView mRecyclerView;
    GridLayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    Tracker t;
    PostItem item;
    SQLiteDatabase database_account;
    Cursor cur;
    List<PostItem> feedsList;
    SwipeRefreshLayout swipeContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.posts_general, container, false);

        // Analytics
        t = AnalyticsApplication.getDefaultTracker();
        t.setScreenName("Post - Planned");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());

       /* String currentTheme = MainActivity.currentTheme;
        switch (currentTheme) {
            case "Black":
                mainBackground.setBackgroundResource(R.drawable.siyah);
                Picasso.with(getActivity()).load(photo).error(R.drawable.siyahprofil).placeholder(R.drawable.siyahprofil)
                        .into(profilePic);
                profilePic.setBorderColor(Color.parseColor("#232323"));
                break;
            case "Red":
                mainBackground.setBackgroundResource(R.drawable.kirmizi);
                Picasso.with(getActivity()).load(photo).error(R.drawable.kirmiziprofil).placeholder(R.drawable.kirmiziprofil)
                        .into(profilePic);
                profilePic.setBorderColor(Color.parseColor("#B92D2C"));
                break;
            case "Green":
                mainBackground.setBackgroundResource(R.drawable.yesil);
                Picasso.with(getActivity()).load(photo).error(R.drawable.yesilprofil).placeholder(R.drawable.yesilprofil)
                        .into(profilePic);
                profilePic.setBorderColor(Color.parseColor("#619D43"));
                break;
            case "Orange":
                mainBackground.setBackgroundResource(R.drawable.turuncu);
                Picasso.with(getActivity()).load(photo).error(R.drawable.turuncuprofil).placeholder(R.drawable.turuncuprofil)
                        .into(profilePic);
                profilePic.setBorderColor(Color.parseColor("#C47229"));
                break;
            case "Purple":
                mainBackground.setBackgroundResource(R.drawable.mor);
                Picasso.with(getActivity()).load(photo).error(R.drawable.morprofil).placeholder(R.drawable.morprofil)
                        .into(profilePic);
                profilePic.setBorderColor(Color.parseColor("#70469C"));
                break;
        }*/

        swipeContainer = v.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullPosts();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mRecyclerView = v.findViewById(R.id.postView);
        feedsList = new ArrayList<>();
        database_account = getActivity().openOrCreateDatabase("database_app", MODE_PRIVATE, null);
        pullPosts();

        return v;
    }

    public void pullPosts() {
        feedsList.clear();
        cur = database_account.rawQuery("SELECT * FROM posts WHERE postTime >= " + System.currentTimeMillis() + " ORDER BY postTime ASC", null);
        if (cur != null && cur.getCount() != 0) {
            cur.moveToFirst();
            do {
                for (int i = 0; i < cur.getColumnCount(); i++) {
                    switch (i % 12) {
                        case 0:
                            item = new PostItem();
                            item.setID(cur.getInt(i));
                            break;
                        case 1:
                            item.setReceiverName(cur.getString(i));
                            break;
                        case 2:
                            item.setReceiverMail(cur.getString(i));
                            break;
                        case 3:
                            item.setReceiverPhone(cur.getString(i));
                            break;
                        case 4:
                            item.setPostTime(cur.getLong(i));
                            break;
                        case 5:
                            item.setIsSuccess(cur.getInt(i));
                            break;
                        case 6:
                            item.setMailTitle(cur.getString(i));
                            break;
                        case 7:
                            item.setMailContent(cur.getString(i));
                            break;
                        case 8:
                            item.setMailAttachment(cur.getString(i));
                            break;
                        case 9:
                            item.setSmsContent(cur.getString(i));
                            break;
                        case 10:
                            item.setMessengerContent(cur.getString(i));
                            break;
                        case 11:
                            item.setMessengerAttachment(cur.getString(i));
                            if (item.getPostTime() != 0) {
                                feedsList.add(item);
                            }
                            break;
                    }
                }
            } while (cur.moveToNext());
            cur.close();
        } else {
            //First opening
            Toast.makeText(getActivity(), "There is no post", Toast.LENGTH_LONG).show();
        }


        // Adapter
        mAdapter = new PostAdapter(getActivity(), feedsList);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapter);

        // The number of Columns
        mLayoutManager = new GridLayoutManager(getActivity(), 1);
        mRecyclerView.setLayoutManager(mLayoutManager);

        swipeContainer.setRefreshing(false);
    }
}

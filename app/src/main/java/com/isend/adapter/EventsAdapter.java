package com.isend.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.isend.R;
import com.isend.model.EventItem;

import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {
    private List<EventItem> feedItemList;
    private Context mContext;

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView startTime;
        TextView endTime;
        TextView location;
        ImageView background;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txt_title);
            startTime = itemView.findViewById(R.id.txt_startime);
            endTime = itemView.findViewById(R.id.txt_endtime);
            location = itemView.findViewById(R.id.txt_loc);
            background = itemView.findViewById(R.id.img_background);
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            /*ViewHolder holder = (ViewHolder) view.getTag();
            int position = holder.getAdapterPosition();
            long eventID = Long.parseLong(feedItemList.get(position).getID());
            Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
            Intent intent = new Intent(Intent.ACTION_EDIT)
                    .setData(uri);
            mContext.startActivity(intent);*/
        }
    };

    public EventsAdapter(Context context, List<EventItem> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_events, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        EventItem feedItem = feedItemList.get(i);

        // Setting title
        viewHolder.title.setText(feedItem.getTitle());

        // Setting startTime
        viewHolder.startTime.setText(feedItem.getStartTime());

        // Setting endTime
        viewHolder.endTime.setText(feedItem.getEndTime());

        // Setting location
        viewHolder.location.setText(feedItem.getLocation());

        // Setting background
        viewHolder.background.setBackgroundColor(Integer.parseInt(feedItem.getBackground()));

        // Handle click event on image click
        viewHolder.background.setOnClickListener(clickListener);
        viewHolder.background.setTag(viewHolder);
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

}

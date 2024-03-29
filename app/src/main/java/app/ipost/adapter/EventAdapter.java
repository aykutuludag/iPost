package app.ipost.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.ipost.MainActivity;
import app.ipost.R;
import app.ipost.SingleEvent;
import app.ipost.model.EventItem;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    SharedPreferences prefs;
    private List<EventItem> feedItemList;
    private Context mContext;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewHolder holder = (ViewHolder) view.getTag();
            int position = holder.getAdapterPosition();
            int eventID = feedItemList.get(position).getID();

            Intent intent = new Intent(mContext, SingleEvent.class);
            intent.putExtra("EVENT_ID", eventID);
            mContext.startActivity(intent);
        }
    };

    public EventAdapter(Context context, List<EventItem> feedItemList) {
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
        prefs = mContext.getSharedPreferences("ProfileInformation", Context.MODE_PRIVATE);

        EventItem feedItem = feedItemList.get(i);

        // Setting title
        viewHolder.title.setText(feedItem.getTitle());

        // Setting startTime
        if (feedItem.getStartTime() != 0) {
            viewHolder.startTime.setText(getDate(feedItem.getStartTime()));
        } else {
            viewHolder.dateIndicator.setVisibility(View.INVISIBLE);
        }

        // Setting location
        if (feedItem.getLocation() != null && !feedItem.getLocation().isEmpty()) {
            viewHolder.location.setText(feedItem.getLocation());
            viewHolder.placeIndicator.setVisibility(View.VISIBLE);
        } else {
            viewHolder.placeIndicator.setVisibility(View.INVISIBLE);
        }

        // Setting background_splash
        String currentTheme = MainActivity.currentTheme;
        switch (currentTheme) {
            case "Black":
                viewHolder.background.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.siyahbutton));
                break;
            case "Red":
                viewHolder.background.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.kirmizibutton));
                break;
            case "Green":
                viewHolder.background.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.yesilbutton));
                break;
            case "Orange":
                viewHolder.background.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.turuncubutton));
                break;
            case "Purple":
                viewHolder.background.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.morbutton));
                break;
        }

        // setSMSOption
        if (feedItem.getIsSMSActive() == 1) {
            viewHolder.sms.setVisibility(View.VISIBLE);
        } else {
            viewHolder.sms.setVisibility(View.GONE);
        }

        // setMailOption
        if (feedItem.getIsMailActive() == 1) {
            viewHolder.mail.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mail.setVisibility(View.GONE);
        }

        // setMessengerOption
        if (feedItem.getIsMessengerActive() == 1) {
            viewHolder.messenger.setVisibility(View.VISIBLE);
        } else {
            viewHolder.messenger.setVisibility(View.GONE);
        }

        // setWhatsappOption
        if (feedItem.getIsWhatsappActive() == 1) {
            viewHolder.whatsapp.setVisibility(View.VISIBLE);
        } else {
            viewHolder.whatsapp.setVisibility(View.GONE);
        }

        // Handle click event on image click
        viewHolder.background.setOnClickListener(clickListener);
        viewHolder.background.setTag(viewHolder);
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    private String getDate(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy HH:mm", Locale.getDefault());
        return sdf.format(date);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView startTime;
        TextView location;
        ImageView background;
        ImageView sms;
        ImageView mail;
        ImageView messenger;
        ImageView whatsapp;
        ImageView dateIndicator, placeIndicator;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.event_name);
            startTime = itemView.findViewById(R.id.event_time);
            location = itemView.findViewById(R.id.txt_loc);
            background = itemView.findViewById(R.id.event_background);
            sms = itemView.findViewById(R.id.options_sms);
            mail = itemView.findViewById(R.id.options_mail);
            messenger = itemView.findViewById(R.id.options_messenger);
            whatsapp = itemView.findViewById(R.id.options_whatsapp);
            dateIndicator = itemView.findViewById(R.id.iconTime);
            placeIndicator = itemView.findViewById(R.id.iconLocation);
        }
    }
}

package app.isend.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import app.isend.R;
import app.isend.SingleEvent;
import app.isend.model.EventItem;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {
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
        viewHolder.startTime.setText(getDate(feedItem.getStartTime()));

        // Setting location
        viewHolder.location.setText(feedItem.getLocation());

        // Setting background
        viewHolder.background.setBackgroundColor(feedItem.getBackground());

        // setSMSOption
        viewHolder.sms.setAlpha(1.0f);
        if (feedItem.getIsSMSActive() == 1) {
            viewHolder.sms.setAlpha(1.0f);
        } else {
            viewHolder.sms.setAlpha(0.5f);
        }

        // setMailOption
        if (feedItem.getIsMailActive() == 1) {
            viewHolder.mail.setAlpha(1.0f);
        } else {
            viewHolder.mail.setAlpha(0.5f);
        }

        // setMessengerOption
        if (feedItem.getIsMessengerActive() == 1) {
            viewHolder.messenger.setAlpha(1.0f);
        } else {
            viewHolder.messenger.setAlpha(0.5f);
        }

        // setWhatsappOption
        if (feedItem.getIsWhatsappActive() == 1) {
            viewHolder.whatsapp.setAlpha(1.0f);
        } else {
            viewHolder.whatsapp.setAlpha(0.5f);
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
        TimeZone tz = Calendar.getInstance().getTimeZone();
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM YYYY HH:mm", Locale.getDefault());
        return sdf.format(date);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView startTime;
        TextView location;
        RelativeLayout background;
        ImageView sms;
        ImageView mail;
        ImageView messenger;
        ImageView whatsapp;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.contact_name);
            startTime = itemView.findViewById(R.id.txt_startime);
            location = itemView.findViewById(R.id.txt_loc);
            background = itemView.findViewById(R.id.single_event);
            sms = itemView.findViewById(R.id.options_sms);
            mail = itemView.findViewById(R.id.options_mail);
            messenger = itemView.findViewById(R.id.options_messenger);
            whatsapp = itemView.findViewById(R.id.options_whatsapp);
        }
    }

}

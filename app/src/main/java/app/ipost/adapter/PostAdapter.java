package app.ipost.adapter;

import android.content.Context;
import android.content.Intent;
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
import app.ipost.model.PostItem;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<PostItem> feedItemList;
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

    public PostAdapter(Context context, List<PostItem> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_posts, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        PostItem feedItem = feedItemList.get(i);

        //Recipient name
        if (feedItem.getReceiverName() != null && !feedItem.getReceiverName().isEmpty()) {
            viewHolder.recipient.setText(feedItem.getReceiverName());
        } else {
            viewHolder.recipient.setText("-");
        }

        // Post time
        if (feedItem.getPostTime() != 0) {
            viewHolder.startTime.setText(getDate(feedItem.getPostTime()));
        } else {
            viewHolder.startTime.setText("-");
        }

        if (feedItem.getPostTime() - System.currentTimeMillis() > 0) {
            viewHolder.postSituation.setText(R.string.willSent);
        } else {
            viewHolder.postSituation.setText(R.string.hasSent);
        }

        // setSMSOption
        if (feedItem.getSmsContent() != null && !feedItem.getSmsContent().isEmpty()) {
            viewHolder.sms.setVisibility(View.VISIBLE);
        } else {
            viewHolder.sms.setVisibility(View.INVISIBLE);
        }

        // setMailOption
        if (feedItem.getMailTitle() != null && !feedItem.getMailTitle().isEmpty()) {
            viewHolder.mail.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mail.setVisibility(View.INVISIBLE);
        }

        // setMessengerOption
        if (feedItem.getMessengerContent() != null && !feedItem.getMessengerContent().isEmpty()) {
            viewHolder.messenger.setVisibility(View.VISIBLE);
        } else {
            viewHolder.messenger.setVisibility(View.INVISIBLE);
        }

        // setWhatsappOption
        if (feedItem.getWhatsappContent() != null && !feedItem.getWhatsappContent().isEmpty()) {
            viewHolder.whatsapp.setVisibility(View.VISIBLE);
        } else {
            viewHolder.whatsapp.setVisibility(View.INVISIBLE);
        }

        // Setting background_splash
        String currentTheme = MainActivity.currentTheme;
        switch (currentTheme) {
            case "Black":
                viewHolder.background.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.businessbutonplan));
                break;
            case "Red":
                viewHolder.background.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.womanbutonplan));
                break;
            case "Green":
                viewHolder.background.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.fitnessbutonplan));
                break;
            case "Orange":
                viewHolder.background.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.coffebuttonplan));
                break;
            case "Purple":
                viewHolder.background.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.paparaszzibutonplan));
                break;
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
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault());
        return sdf.format(date);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView recipient;
        TextView startTime;
        ImageView background;
        ImageView sms;
        ImageView mail;
        ImageView messenger;
        ImageView whatsapp;
        TextView postSituation;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.post_name);
            recipient = itemView.findViewById(R.id.post_recipient);
            startTime = itemView.findViewById(R.id.post_time);
            background = itemView.findViewById(R.id.post_background);
            sms = itemView.findViewById(R.id.options_sms);
            mail = itemView.findViewById(R.id.options_mail);
            messenger = itemView.findViewById(R.id.options_messenger);
            whatsapp = itemView.findViewById(R.id.options_whatsapp);
            postSituation = itemView.findViewById(R.id.isSent);
        }
    }

}

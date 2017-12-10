package app.ipost.adapter;

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
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

        // Receiver name
        viewHolder.title.setText(feedItem.getReceiverName());

        // Post time
        viewHolder.startTime.setText(getDate(feedItem.getPostTime()));

        // setSMSOption
        if (feedItem.getSmsContent() != null) {
            viewHolder.sms.setAlpha(1.0f);
        } else {
            viewHolder.sms.setAlpha(0.5f);
        }

        // setMailOption
        if (feedItem.getMailTitle() != null) {
            viewHolder.mail.setAlpha(1.0f);
        } else {
            viewHolder.mail.setAlpha(0.5f);
        }

        // setMessengerOption
        if (feedItem.getMessengerContent() != null) {
            viewHolder.messenger.setAlpha(1.0f);
        } else {
            viewHolder.messenger.setAlpha(0.5f);
        }

        // setWhatsappOption
        if (feedItem.getWhatsappContent() != null) {
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
            title = itemView.findViewById(R.id.post_name);
            startTime = itemView.findViewById(R.id.post_time);
            //location = itemView.findViewById(R.id.txt_loc);
            // background = itemView.findViewById(R.id.single_event);
            sms = itemView.findViewById(R.id.options_sms);
            mail = itemView.findViewById(R.id.options_mail);
            messenger = itemView.findViewById(R.id.options_messenger);
            whatsapp = itemView.findViewById(R.id.options_whatsapp);
        }
    }

}

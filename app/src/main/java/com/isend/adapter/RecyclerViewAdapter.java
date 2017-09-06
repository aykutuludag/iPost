package com.isend.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.isend.R;
import com.isend.model.EventItem;

import java.io.File;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<EventItem> feedItemList;
    private Context mContext;

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView text;

        ViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.txt_title);
            image = itemView.findViewById(R.id.img_background);
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewHolder holder = (ViewHolder) view.getTag();
           /* int position = holder.getAdapterPosition();
            Uri photoURI = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", file[position]);

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(photoURI, "image/*");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            mContext.startActivity(intent);*/
        }
    };

    public RecyclerViewAdapter(Context context, List<EventItem> feedItemList) {
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

        // Setting text view title
        viewHolder.text.setText(feedItem.getTitle());

        // Setting image
        viewHolder.image.setBackgroundColor(Integer.parseInt(feedItem.getBackground()));

        // Handle click event on both title and image click
        viewHolder.text.setOnClickListener(clickListener);
        viewHolder.image.setOnClickListener(clickListener);

        viewHolder.text.setTag(viewHolder);
        viewHolder.image.setTag(viewHolder);
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

}

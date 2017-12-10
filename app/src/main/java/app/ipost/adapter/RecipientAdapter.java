package app.ipost.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import app.ipost.R;
import app.ipost.model.ContactItem;

public class RecipientAdapter extends ArrayAdapter {
    private List<ContactItem> feedItemList;
    private Context mContext;

    public RecipientAdapter(Context context, int textViewResourceId, List<ContactItem> feedItemList) {
        super(context, textViewResourceId, feedItemList);
        this.mContext = context;
        this.feedItemList = feedItemList;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = li.inflate(R.layout.card_contacts, parent, false);
            viewHolder = new ViewHolder(convertView);

            viewHolder.name = convertView.findViewById(R.id.post_name);
            viewHolder.textMail = convertView.findViewById(R.id.contact_mail);
            viewHolder.number = convertView.findViewById(R.id.contact_number);
            viewHolder.profilePhoto = convertView.findViewById(R.id.contact_photo);
            viewHolder.SMS = convertView.findViewById(R.id.options_sms);
            viewHolder.whatsapp = convertView.findViewById(R.id.options_whatsapp);
            viewHolder.messenger = convertView.findViewById(R.id.options_messenger);
            viewHolder.mail = convertView.findViewById(R.id.options_mail);
            viewHolder.background = convertView.findViewById(R.id.single_contact);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        myLayoutInflater(position, viewHolder);
        return convertView;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = li.inflate(R.layout.card_contacts, parent, false);
            viewHolder = new ViewHolder(convertView);

            viewHolder.name = convertView.findViewById(R.id.post_name);
            viewHolder.textMail = convertView.findViewById(R.id.contact_mail);
            viewHolder.number = convertView.findViewById(R.id.contact_number);
            viewHolder.profilePhoto = convertView.findViewById(R.id.contact_photo);
            viewHolder.SMS = convertView.findViewById(R.id.options_sms);
            viewHolder.whatsapp = convertView.findViewById(R.id.options_whatsapp);
            viewHolder.messenger = convertView.findViewById(R.id.options_messenger);
            viewHolder.mail = convertView.findViewById(R.id.options_mail);
            viewHolder.background = convertView.findViewById(R.id.single_contact);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        myLayoutInflater(position, viewHolder);
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    private void myLayoutInflater(int position, ViewHolder holder) {

        ContactItem feedItem = feedItemList.get(position);

        // Setting title
        holder.name.setText(feedItem.getName());

        // Setting phoneNumber
        holder.number.setText(feedItem.getPhoneNumber());

        //  Setting mail
        holder.textMail.setText(feedItem.getMail());

        // Setting profilephoto
        Picasso.with(mContext).load(feedItem.getContactPhoto()).error(R.drawable.siyahprofil).placeholder(R.drawable.siyahprofil)
                .into(holder.profilePhoto);

        //  Setting SMS
        if (feedItem.getPhoneNumber() != null) {
            holder.SMS.setVisibility(View.VISIBLE);
        } else {
            holder.SMS.setVisibility(View.GONE);
        }

        // Setting mail
        if (feedItem.getMail() != null) {
            holder.mail.setVisibility(View.VISIBLE);
        } else {
            holder.mail.setVisibility(View.GONE);
        }

        // Setting whatsapp
        if (feedItem.getWhatsapp() == 1) {
            holder.whatsapp.setVisibility(View.VISIBLE);
        } else {
            holder.whatsapp.setVisibility(View.GONE);
        }

        // Setting messenger
        if (feedItem.getMessenger() == 1) {
            holder.messenger.setVisibility(View.VISIBLE);
        } else {
            holder.messenger.setVisibility(View.GONE);
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView number;
        TextView textMail;
        ImageView profilePhoto;
        ImageView whatsapp;
        ImageView messenger;
        ImageView mail;
        ImageView SMS;
        RelativeLayout background;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.post_name);
            textMail = itemView.findViewById(R.id.contact_mail);
            number = itemView.findViewById(R.id.contact_number);
            profilePhoto = itemView.findViewById(R.id.contact_photo);

            SMS = itemView.findViewById(R.id.options_sms);
            whatsapp = itemView.findViewById(R.id.options_whatsapp);
            messenger = itemView.findViewById(R.id.options_messenger);
            mail = itemView.findViewById(R.id.options_mail);

            background = itemView.findViewById(R.id.single_contact);
        }
    }
}

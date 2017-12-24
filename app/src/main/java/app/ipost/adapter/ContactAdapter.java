package app.ipost.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import app.ipost.MainActivity;
import app.ipost.R;
import app.ipost.model.ContactItem;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private List<ContactItem> feedItemList;
    private Context mContext;

    public ContactAdapter(Context context, List<ContactItem> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_contacts, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        ContactItem feedItem = feedItemList.get(i);

        // Setting title
        viewHolder.name.setText(feedItem.getName());

        // Setting phoneNumber
        viewHolder.number.setText(feedItem.getPhoneNumber());

        //  Setting mail
        viewHolder.textMail.setText(feedItem.getMail());

        // Setting background
        String currentTheme = MainActivity.currentTheme;
        switch (currentTheme) {
            case "Black":
                viewHolder.background.setBackgroundColor(Color.parseColor("#424242"));
                break;
            case "Red":
                viewHolder.background.setBackgroundColor(Color.parseColor("#BB2026"));
                break;
            case "Green":
                viewHolder.background.setBackgroundColor(Color.parseColor("#5EB546"));
                break;
            case "Orange":
                viewHolder.background.setBackgroundColor(Color.parseColor("#80472A"));
                break;
            case "Purple":
                viewHolder.background.setBackgroundColor(Color.parseColor("#855BA5"));
                break;
        }


        // Setting profilephoto
        Picasso.with(mContext).load(feedItem.getContactPhoto()).error(R.drawable.siyahprofil).placeholder(R.drawable.siyahprofil)
                .into(viewHolder.profilePhoto);

        //  Setting SMS
        if (feedItem.getPhoneNumber() != null) {
            viewHolder.SMS.setVisibility(View.VISIBLE);
        } else {
            viewHolder.SMS.setVisibility(View.GONE);
        }

        // Setting mail
        if (feedItem.getMail() != null) {
            viewHolder.mail.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mail.setVisibility(View.GONE);
        }

        // Setting whatsapp
        if (feedItem.getWhatsapp() == 1) {
            viewHolder.whatsapp.setVisibility(View.VISIBLE);
        } else {
            viewHolder.whatsapp.setVisibility(View.GONE);
        }

        // Setting messenger
        if (feedItem.getMessenger() == 1) {
            viewHolder.messenger.setVisibility(View.VISIBLE);
        } else {
            viewHolder.messenger.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

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
            name = itemView.findViewById(R.id.contact_name);
            textMail = itemView.findViewById(R.id.contact_mail);
            number = itemView.findViewById(R.id.contact_number);
            profilePhoto = itemView.findViewById(R.id.contact_photo);

            SMS = itemView.findViewById(R.id.options_sms);
            whatsapp = itemView.findViewById(R.id.options_whatsapp);
            messenger = itemView.findViewById(R.id.options_messenger);
            mail = itemView.findViewById(R.id.options_mail);

            background = itemView.findViewById(R.id.single_contact);
            background.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContactItem feedItem = feedItemList.get(getAdapterPosition());
                    String contactID = feedItem.getID();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactID);
                    intent.setData(uri);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
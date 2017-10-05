package app.ipost.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import app.ipost.R;
import app.ipost.model.ContactItem;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private List<ContactItem> feedItemList;
    private Context mContext;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(mContext, "You can update contact info from Contacts. Changes will shown within an hour.", Toast.LENGTH_LONG).show();
        }
    };

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

        // Setting profilephoto
        Picasso.with(mContext).load(feedItem.getContactPhoto()).error(R.drawable.ic_blank_photo).placeholder(R.drawable.ic_blank_photo)
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

        // Handle click event on image click
        viewHolder.background.setOnClickListener(clickListener);
        viewHolder.background.setTag(viewHolder);
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
        }
    }
}

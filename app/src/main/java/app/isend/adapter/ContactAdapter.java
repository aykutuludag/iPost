package app.isend.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import app.isend.R;
import app.isend.model.ContactItem;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private List<ContactItem> feedItemList;
    private Context mContext;
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
        Picasso.with(mContext).load(feedItem.getContactPhoto()).error(R.drawable.com_facebook_profile_picture_blank_square).placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                .into(viewHolder.profilePhoto);

        // Setting whatsapp
        viewHolder.whatsapp.setVisibility(View.VISIBLE);

        // Setting messenger
        viewHolder.messenger.setVisibility(View.VISIBLE);

        // Setting mail
        viewHolder.mail.setVisibility(View.VISIBLE);

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
        RelativeLayout background;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txt_name);
            number = itemView.findViewById(R.id.txt_number);
            profilePhoto = itemView.findViewById(R.id.contactPhoto);
            whatsapp = itemView.findViewById(R.id.whatsapp);
            messenger = itemView.findViewById(R.id.messenger);
            mail = itemView.findViewById(R.id.mail);
            background = itemView.findViewById(R.id.single_contact);
            textMail = itemView.findViewById(R.id.txt_mail);
        }
    }
}

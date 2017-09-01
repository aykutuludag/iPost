package com.isend;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class FragmentProfile extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        String name = MainActivity.name;
        String email = MainActivity.email;
        String photoURL = MainActivity.photoURL;

        //Name
        TextView navUsername = v.findViewById(R.id.textView3);
        navUsername.setText(name);
        //E-mail
        TextView navEmail = v.findViewById(R.id.textView4);
        navEmail.setText(email);
        //ProfilePicture
        ImageView profilePic = v.findViewById(R.id.imageView3);
        Picasso.with(getActivity()).load(photoURL).error(R.drawable.ic_error).placeholder(R.drawable.ic_placeholder)
                .into(profilePic);


        return v;
    }
}

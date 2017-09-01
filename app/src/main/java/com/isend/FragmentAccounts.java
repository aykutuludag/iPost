package com.isend;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;

public class FragmentAccounts extends Fragment {

    CallbackManager callbackManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_accounts, container, false);

        // GOOGLE
        SignInButton google = v.findViewById(R.id.account_google);
        View.OnClickListener buttonListener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.account_google:
                        Toast.makeText(getActivity(), getString(R.string.base_account), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        google.setOnClickListener(buttonListener);


        //  FACEBOOK
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = v.findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getActivity(), getString(R.string.account_connected), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getActivity(), getString(R.string.error_aborted), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(getActivity(), getString(R.string.error_login), Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }
}

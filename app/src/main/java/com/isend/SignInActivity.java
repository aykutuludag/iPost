package com.isend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

public class SignInActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    SQLiteDatabase database_account;
    GoogleApiClient mGoogleApiClient;
    SignInButton signInButton;
    SharedPreferences prefs;
    String name, email, photo;
    boolean isSigned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // Create local database to save contacs
        database_account = this.openOrCreateDatabase("database_app", MODE_PRIVATE, null);
        database_account.execSQL("CREATE TABLE IF NOT EXISTS events(ID TEXT, Title TEXT, Description VARCHAR, Start VARCHAR, End VARCHAR, Location VARCHAR, Owner VARCHAR, Color VARCHAR, Source VARCHAR);");
        database_account.execSQL("CREATE TABLE IF NOT EXISTS contacts(ID TEXT, DisplayName TEXT, PhoneNumber VARCHAR, ProfilePhoto VARCHAR, hasWhatsapp VARCHAR, hasMessenger VARCHAR, hasMail VARCHAR);");

        prefs = this.getSharedPreferences("SignIn", Context.MODE_PRIVATE);
        name = prefs.getString("Name", "");
        email = prefs.getString("Email", "");
        photo = prefs.getString("ProfilePhoto", "");
        isSigned = prefs.getBoolean("isSigned", false);

        // Set the dimensions of the sign-in button.
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(this);

        // Configure sign-in to request the user's ID, email address, and basic profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Check user is already signed
        if (isSigned) {
            signInButton.setVisibility(View.INVISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }, 2000);
        } else {
            signInButton.setVisibility(View.VISIBLE);
        }
    }

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, 9001);
    }

    private void googleHandleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                prefs.edit().putString("Name", acct.getDisplayName()).apply();
                prefs.edit().putString("Email", acct.getEmail()).apply();
                if (acct.getPhotoUrl() != null) {
                    prefs.edit().putString("ProfilePhoto", acct.getPhotoUrl().toString()).apply();
                } else {
                    //BURAYA İLERİDE EL AT
                    prefs.edit().putString("ProfilePhoto", "https://i.stack.imgur.com/34AD2.jpg").apply();
                }
                prefs.edit().putBoolean("isSigned", result.isSuccess()).apply();
                Toast.makeText(this, getString(R.string.account_created), Toast.LENGTH_SHORT).show();
                signInButton.setVisibility(View.INVISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(SignInActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                }, 2000);
            } else {
                Toast.makeText(this, getString(R.string.error_login_no_account), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.error_login_fail), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                googleSignIn();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 9001) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            googleHandleSignInResult(result);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection error: " + connectionResult, Toast.LENGTH_SHORT).show();
    }
}


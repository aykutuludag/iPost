package com.isend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

public class SignInActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    SQLiteDatabase database_account;

    GoogleApiClient mGoogleApiClient;
    SignInButton signInButton;

    SharedPreferences prefs;
    String name, email, photo, gender, birthday, location;
    boolean isSigned;

    LoginButton loginButton;
    CallbackManager callbackManager;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        prefs = this.getSharedPreferences("SignIn", Context.MODE_PRIVATE);
        name = prefs.getString("Name", "");
        email = prefs.getString("Email", "");
        photo = prefs.getString("ProfilePhoto", "");
        gender = prefs.getString("Gender", "");
        birthday = prefs.getString("Birthday", "");
        location = prefs.getString("Location", "");
        isSigned = prefs.getBoolean("isSigned", false);

        pb = findViewById(R.id.progressBar);

         /* Facebook Sign-In */
        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile");
        loginButton.setReadPermissions("user_events");
        loginButton.setReadPermissions("email");

        /* Google Sign-In */
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(this);

        // Build a GoogleApiClient with access to the Google Sign-In API and the options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, GoogleSignInOptions.DEFAULT_SIGN_IN)
                .addApi(Plus.API)
                .build();

        if (isSigned) {
            //signed succesfully
            loginButton.setVisibility(View.INVISIBLE);
            signInButton.setVisibility(View.INVISIBLE);
            pb.setVisibility(View.VISIBLE);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Check user is already granted permissions
                    if (ContextCompat.checkSelfPermission(SignInActivity.this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                        Intent i = new Intent(SignInActivity.this, PermissionsActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Intent i = new Intent(SignInActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            }, 1250);
        } else {
            // Create local database to save contacs
            database_account = this.openOrCreateDatabase("database_app", MODE_PRIVATE, null);
            database_account.execSQL("CREATE TABLE IF NOT EXISTS events(ID TEXT, Title TEXT, Description VARCHAR, Start INTEGER, End INTEGER, Location VARCHAR, Owner VARCHAR, Color VARCHAR, Source VARCHAR);");
            database_account.execSQL("CREATE TABLE IF NOT EXISTS contacts(ID TEXT, DisplayName TEXT, PhoneNumber VARCHAR, Email VARCHAR, hasWhatsapp VARCHAR, hasMessenger VARCHAR);");

            loginButton.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.VISIBLE);
        }
    }

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, 9001);
    }

    private void facebookLogin() {
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println(loginResult);

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                System.out.println(response.toString());
                                try {
                                    name = object.getString("name");
                                    email = object.getString("email");
                                    photo = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                    gender = object.getString("gender");
                                    birthday = object.getString("user_birthday");
                                    location = object.getString("location");

                                    prefs.edit().putString("Name", name).apply();
                                    prefs.edit().putString("Email", email).apply();
                                    prefs.edit().putString("ProfilePhoto", photo).apply();
                                    prefs.edit().putString("Gender", gender).apply();
                                    prefs.edit().putString("Birthday", birthday).apply();
                                    prefs.edit().putString("Location", location).apply();
                                    prefs.edit().putBoolean("isSigned", true).apply();


                                    Toast.makeText(SignInActivity.this, getString(R.string.account_created), Toast.LENGTH_SHORT).show();

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent i = new Intent(SignInActivity.this, PermissionsActivity.class);
                                            startActivity(i);
                                            finish();
                                        }
                                    }, 1250);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location, events");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                prefs.edit().putBoolean("isSigned", false).apply();
            }

            @Override
            public void onError(FacebookException exception) {
                prefs.edit().putBoolean("isSigned", false).apply();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                googleSignIn();
                break;
            case R.id.login_button:
                facebookLogin();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Facebook
        callbackManager.onActivityResult(requestCode, resultCode, data);

        //Google
        if (requestCode == 9001) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                if (acct != null) {
                    System.out.println(acct.getGrantedScopes());
                    prefs.edit().putString("Name", acct.getDisplayName()).apply();
                    prefs.edit().putString("Email", acct.getEmail()).apply();
                    if (acct.getPhotoUrl() != null) {
                        prefs.edit().putString("ProfilePhoto", acct.getPhotoUrl().toString()).apply();
                    } else {
                        //BURAYA İLERİDE EL AT
                        prefs.edit().putString("ProfilePhoto", "https://i.stack.imgur.com/34AD2.jpg").apply();
                    }
                    // G+
                    Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                    if (person.getGender() == 0) {
                        prefs.edit().putString("Gender", "male").apply();
                    } else if (person.getGender() == 1) {
                        prefs.edit().putString("Gender", "female").apply();
                    } else {
                        prefs.edit().putString("Gender", "transsexual").apply();
                    }
                    prefs.edit().putString("Birthday", person.getBirthday()).apply();
                    prefs.edit().putString("Location", person.getCurrentLocation()).apply();
                    prefs.edit().putBoolean("isSigned", result.isSuccess()).apply();
                    Toast.makeText(this, getString(R.string.account_created), Toast.LENGTH_SHORT).show();
                    loginButton.setVisibility(View.INVISIBLE);
                    signInButton.setVisibility(View.INVISIBLE);
                    pb.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(SignInActivity.this, PermissionsActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }, 1250);
                } else {
                    Toast.makeText(this, getString(R.string.error_login_no_account), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.error_login_fail), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection error: " + connectionResult, Toast.LENGTH_SHORT).show();
    }
}


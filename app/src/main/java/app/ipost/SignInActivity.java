package app.ipost;

import android.Manifest;
import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.ViewSwitcher;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.util.Timer;
import java.util.TimerTask;

public class SignInActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    SQLiteDatabase database_account;
    GoogleApiClient mGoogleApiClient;
    SignInButton signInButton;
    SharedPreferences prefs;
    String name, email, photo, gender, birthday, location;
    boolean isSigned;
    TextSwitcher slogan;
    ProgressBar pb;

    Account mAuthorizedAccount;
    int googleSign = 9001;
    int element = 0;
    String slogans[] = {"This is the slogan area. We can write whatever we want!", "Totally we can do it!", "Yeah I'm doing it right now!"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // Analytics
        Tracker t = ((AnalyticsApplication) this.getApplication()).getDefaultTracker();
        t.setScreenName("Sign-In");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        prefs = this.getSharedPreferences("ProfileInformation", Context.MODE_PRIVATE);
        name = prefs.getString("Name", "-");
        email = prefs.getString("Email", "-");
        photo = prefs.getString("ProfilePhoto", "");
        gender = prefs.getString("Gender", "Other");
        birthday = prefs.getString("Birthday", "-");
        location = prefs.getString("Location", "-");
        isSigned = prefs.getBoolean("isSigned", false);

        String fileName = "android.resource://" + getPackageName() + "/raw/background_singin";
        VideoView vv = this.findViewById(R.id.videoView);
        vv.setVideoURI(Uri.parse(fileName));
        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.setLooping(true);

            }
        });
        vv.start();

        slogan = findViewById(R.id.textSwitcher);
        slogan.setInAnimation(SignInActivity.this, android.R.anim.slide_in_left);
        slogan.setOutAnimation(SignInActivity.this, android.R.anim.slide_out_right);
        slogan.setFactory(new ViewSwitcher.ViewFactory() {
            public View makeView() {
                // TODO Auto-generated method stub
                // create new textView and set the properties like clolr, size etc
                TextView myText = new TextView(SignInActivity.this);
                myText.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                myText.setTextSize(36);
                myText.setTextColor(Color.BLACK);
                return myText;
            }
        });

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SignInActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        element++;
                        if ((element % 3) == 0) {
                            slogan.setText(slogans[0]);
                        } else if ((element % 3) == 1) {
                            slogan.setText(slogans[1]);
                        } else {
                            slogan.setText(slogans[2]);
                        }
                    }
                });
            }
        }, 0, 4000);

        // ProgressBar
        pb = findViewById(R.id.progressBar);

        /* Google Sign-In */
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Plus.SCOPE_PLUS_LOGIN)
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
                .build();

        // Check the user is signed or not
        if (isSigned) {
            //signed already
            signInButton.setVisibility(View.INVISIBLE);
            pb.setVisibility(View.VISIBLE);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Check user is already granted permissions
                    if (ContextCompat.checkSelfPermission(SignInActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
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
            database_account.execSQL("CREATE TABLE IF NOT EXISTS events(ID INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, description VARCHAR, start INTEGER, end INTEGER, location VARCHAR, owner VARCHAR, color INTEGER, isMail INTEGER, isSMS INTEGER, isMessenger INTEGER, isWhatsapp INTEGER);");
            database_account.execSQL("CREATE TABLE IF NOT EXISTS contacts(ID INTEGER, displayName TEXT, phoneNumber VARCHAR, userMail VARCHAR, hasWhatsapp INTEGER, hasMessenger INTEGER, contactPhoto VARCHAR);");
            database_account.execSQL("CREATE TABLE IF NOT EXISTS posts(ID INTEGER PRIMARY KEY AUTOINCREMENT, receiverName TEXT, receiverMail VARCHAR, receiverPhone VARCHAR, postTime INTEGER, isDelivered INTEGER, mailTitle VARCHAR, mailContent TEXT, mailAttachment VARCHAR, smsContent TEXT, messengerContent TEXT, messengerAttachment VARCHAR, whatsappContent TEXT, whatsappAttachment VARCHAR);");
            signInButton.setVisibility(View.VISIBLE);
        }
    }

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, googleSign);
    }

    private void facebookLogin() {
        //Coming soon
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
        if (requestCode == googleSign) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                if (acct != null) {
                    System.out.println(acct.getGrantedScopes());
                    mAuthorizedAccount = acct.getAccount();
                    prefs.edit().putString("GoogleAccountID", acct.getId()).apply();
                    prefs.edit().putString("Name", acct.getDisplayName()).apply();
                    prefs.edit().putString("Email", acct.getEmail()).apply();
                    if (acct.getPhotoUrl() != null) {
                        prefs.edit().putString("ProfilePhoto", acct.getPhotoUrl().toString()).apply();
                    } else {
                        prefs.edit().putString("ProfilePhoto", "android.resource://app.isend/R.drawable.ic_blank_photo").apply();
                    }
                    // G+
                    Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                    if (person.getGender() == 0) {
                        prefs.edit().putString("Gender", "Male").apply();
                    } else if (person.getGender() == 1) {
                        prefs.edit().putString("Gender", "Female").apply();
                    } else {
                        prefs.edit().putString("Gender", "Other").apply();
                    }


                    if (person.getBirthday() != null) {
                        prefs.edit().putString("Birthday", person.getBirthday()).apply();
                    }

                    if (person.getCurrentLocation() != null) {
                        prefs.edit().putString("Location", person.getCurrentLocation()).apply();
                    }

                    prefs.edit().putBoolean("isSigned", result.isSuccess()).apply();
                    Toast.makeText(this, getString(R.string.account_created), Toast.LENGTH_SHORT).show();
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


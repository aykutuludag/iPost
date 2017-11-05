package app.ipost;

import android.Manifest;
import android.accounts.Account;
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
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.Calendar;

public class SignInActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    SQLiteDatabase database_account;
    GoogleApiClient mGoogleApiClient;
    SignInButton signInButton;
    SharedPreferences prefs;
    String name, email, photo, gender, birthday, location;
    boolean isSigned;

    Account mAuthorizedAccount;
    int googleSign = 9001;

    RelativeLayout layoutLogin, layoutWelcome;
    TextView welcomeText;

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

        layoutLogin = findViewById(R.id.login_screen);
        layoutWelcome = findViewById(R.id.welcome_Screen);

        // Check the user is signed or not
        if (isSigned) {
            //signed already
            layoutLogin.setVisibility(View.INVISIBLE);
            layoutWelcome.setVisibility(View.VISIBLE);

            welcomeText = findViewById(R.id.textView2);

            Calendar rightNow = Calendar.getInstance();
            int hour = rightNow.get(Calendar.HOUR_OF_DAY);
            System.out.println("hour is:" + hour);
            if (hour < 6) {
                welcomeText.setText("Good nights" + name + ", welcome back!");
            } else if (hour < 12) {
                welcomeText.setText("Good morning" + name + ", welcome back!");
            } else if (hour < 18) {
                welcomeText.setText("Have a nice day" + name + ", welcome back!");
            } else {
                welcomeText.setText("Good afternoon" + name + ", welcome back!");
            }

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
            }, 3000);
        } else {
            // Create local database to save contacs
            database_account = this.openOrCreateDatabase("database_app", MODE_PRIVATE, null);
            database_account.execSQL("CREATE TABLE IF NOT EXISTS events(ID INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, description VARCHAR, sTime INTEGER, eTime INTEGER, location VARCHAR, owner VARCHAR, color INTEGER, isMail INTEGER, isSMS INTEGER, isMessenger INTEGER, isWhatsapp INTEGER);");
            database_account.execSQL("CREATE TABLE IF NOT EXISTS contacts(ID INTEGER, displayName TEXT, phoneNumber VARCHAR, userMail VARCHAR, hasWhatsapp INTEGER, hasMessenger INTEGER, contactPhoto VARCHAR);");
            database_account.execSQL("CREATE TABLE IF NOT EXISTS posts(ID INTEGER, receiverName TEXT, receiverMail VARCHAR, receiverPhone VARCHAR, postTime INTEGER, isDelivered INTEGER, mailTitle VARCHAR, mailContent TEXT, mailAttachment VARCHAR, smsContent TEXT, messengerContent TEXT, messengerAttachment VARCHAR, whatsappContent TEXT, whatsappAttachment VARCHAR);");

            layoutLogin.setVisibility(View.VISIBLE);
            layoutWelcome.setVisibility(View.INVISIBLE);

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
        }
    }

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, googleSign);
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
                    layoutLogin.setVisibility(View.INVISIBLE);
                    layoutWelcome.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(SignInActivity.this, PermissionsActivity.class);
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
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection error: " + connectionResult, Toast.LENGTH_SHORT).show();
    }
}


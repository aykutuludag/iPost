package app.isend;

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
import android.widget.ProgressBar;
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

public class SignInActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    SQLiteDatabase database_account;

    GoogleApiClient mGoogleApiClient;
    SignInButton signInButton;

    SharedPreferences prefs;
    String name, email, photo, gender, birthday, location;
    boolean isSigned;

    ProgressBar pb;

    int googleSign = 9001;

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

        // ProgressBar
        pb = findViewById(R.id.progressBar);

        /* Google Sign-In */
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
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
            database_account.execSQL("CREATE TABLE IF NOT EXISTS events(ID TEXT, Title TEXT, Description VARCHAR, Start INTEGER, End INTEGER, Location VARCHAR, Owner VARCHAR, Color VARCHAR);");
            database_account.execSQL("CREATE TABLE IF NOT EXISTS contacts(ID TEXT, DisplayName TEXT, PhoneNumber VARCHAR, UserMail VARCHAR, ContactPhoto VARCHAR);");

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
                        prefs.edit().putString("Gender", "other").apply();
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


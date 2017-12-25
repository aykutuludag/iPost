package app.ipost;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    FrameLayout frame;
    FragmentTransaction transaction;
    RelativeLayout header;
    SharedPreferences prefs;
    Toolbar toolbar;
    Window window;
    String name, email, photo, gender, birthday, location;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_detail:
                    transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content, new ProfileDetail());
                    transaction.commit();
                    return true;
                case R.id.navigation_accounts:
                    transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content, new ProfileAccounts());
                    transaction.commit();
                    return true;
                case R.id.navigation_friends:
                    transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content, new ProfileContacts());
                    transaction.commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        prefs = this.getSharedPreferences("ProfileInformation", Context.MODE_PRIVATE);
        name = prefs.getString("Name", "-");
        email = prefs.getString("Email", "-");
        photo = prefs.getString("ProfilePhoto", "");
        gender = prefs.getString("Gender", "Other");
        birthday = prefs.getString("Birthday", "-");
        location = prefs.getString("Location", "-");

        // Initializing Toolbar and setting it as the actionbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //ColoredBars
        window = this.getWindow();

        //Name
        TextView navUsername = findViewById(R.id.profile_name);
        navUsername.setText(name);
        //E-mail
        TextView navEmail = findViewById(R.id.profile_mail);
        navEmail.setText(email);
        //ProfilePicture
        ImageView profilePic = findViewById(R.id.profile_pic);
        Picasso.with(this).load(photo).error(R.drawable.siyahprofil).placeholder(R.drawable.siyahprofil)
                .into(profilePic);
        //Age
        TextView birthtext = findViewById(R.id.profile_birthday);
        birthtext.setText(birthday);

        //Location
        TextView loc = findViewById(R.id.profile_loc);
        loc.setText(location);

        //Gender
        TextView sex = findViewById(R.id.profile_gender);
        sex.setText(gender);

        header = findViewById(R.id.header_profile);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileActivity.this, ProfileEditActivity.class);
                startActivity(i);
            }
        });

        frame = findViewById(R.id.content);
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, new ProfileDetail());
        transaction.commit();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        switch (MainActivity.currentTheme) {
            case "Black":
                coloredBars(Color.parseColor("#000000"), Color.parseColor("#212121"));
                header.setBackgroundColor(Color.parseColor("#212121"));
                break;
            case "Red":
                coloredBars(Color.parseColor("#D32F2F"), Color.parseColor("#F44336"));
                header.setBackgroundColor(Color.parseColor("#F44336"));
                break;
            case "Green":
                coloredBars(Color.parseColor("#388E3C"), Color.parseColor("#4CAF50"));
                header.setBackgroundColor(Color.parseColor("#4CAF50"));
                break;
            case "Orange":
                coloredBars(Color.parseColor("#F57C00"), Color.parseColor("#FF9800"));
                header.setBackgroundColor(Color.parseColor("#FF9800"));
                break;
            case "Purple":
                coloredBars(Color.parseColor("#7B1FA2"), Color.parseColor("#9C27B0"));
                header.setBackgroundColor(Color.parseColor("#9C27B0"));
                break;
        }
    }


    public void coloredBars(int color1, int color2) {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color1);
            toolbar.setBackgroundColor(color2);
        } else {
            toolbar.setBackgroundColor(color2);
        }
    }
}

package app.isend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kobakei.ratethisapp.RateThisApp;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static String name, email, photo, gender, birthday, location;
    SharedPreferences prefs;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefs = this.getSharedPreferences("ProfileInformation", Context.MODE_PRIVATE);
        name = prefs.getString("Name", "-");
        email = prefs.getString("Email", "-");
        photo = prefs.getString("ProfilePhoto", "");
        gender = prefs.getString("Gender", "0");
        birthday = prefs.getString("Birthday", "-");
        location = prefs.getString("Location", "-");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //NavigationView
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //HeaderView
        View headerView = navigationView.getHeaderView(0);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(i);
            }
        });

        //Name
        TextView navUsername = headerView.findViewById(R.id.nav_name);
        navUsername.setText(name);
        //E-mail
        TextView navEmail = headerView.findViewById(R.id.nav_email);
        navEmail.setText(email);
        //ProfilePicture
        ImageView profilePic = headerView.findViewById(R.id.nav_picture);
        Picasso.with(this).load(photo).error(R.drawable.ic_error).placeholder(R.drawable.ic_placeholder)
                .into(profilePic);

        if (savedInstanceState == null) {
            Fragment fragment = new EventsUpcoming();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "EventsUpcoming").commit();
            navigationView.setCheckedItem(R.id.nav_eventupcoming);
            toolbar.setTitle(R.string.events_upcoming);
        }

        // AppRater
        RateThisApp.onCreate(this);
        RateThisApp.showRateDialogIfNeeded(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            Intent i = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment;
        switch (item.getItemId()) {
            case R.id.nav_eventupcoming:
                fragment = new EventsUpcoming();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "EventsUpComing").commit();
                navigationView.setCheckedItem(R.id.nav_eventupcoming);
                toolbar.setTitle(R.string.events_upcoming);
                break;
            case R.id.nav_eventpast:
                fragment = new EventsPast();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "EventsPast").commit();
                navigationView.setCheckedItem(R.id.nav_eventpast);
                toolbar.setTitle(R.string.events_past);
                break;
            case R.id.nav_postplanned:
                Toast.makeText(this, "Not ready", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_postsent:
                Toast.makeText(this, "Not ready", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_settings:
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.nav_help:
                Intent i2 = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(i2);
                break;
            case R.id.nav_privacy:
                Intent i3 = new Intent(MainActivity.this, PrivacyActivity.class);
                startActivity(i3);
                break;
            case R.id.nav_rate:
                Intent intent4 = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=app.isend"));
                startActivity(intent4);
                break;
            case R.id.nav_subscriptions:
                Toast.makeText(this, "Coming soon...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_feedback:
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"iinvestmentinc@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "iSend Feedback");
                startActivity(Intent.createChooser(intent, "Email via..."));
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

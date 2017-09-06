package com.isend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static String name;
    public static String email;
    public static String photoURL;
    SharedPreferences prefs;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefs = this.getSharedPreferences("SignIn", Context.MODE_PRIVATE);
        name = prefs.getString("Name", "");
        email = prefs.getString("Email", "");
        photoURL = prefs.getString("ProfilePhoto", "");

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
        TextView navUsername = headerView.findViewById(R.id.name);
        navUsername.setText(name);
        //E-mail
        TextView navEmail = headerView.findViewById(R.id.email);
        navEmail.setText(email);
        //ProfilePicture
        ImageView profilePic = headerView.findViewById(R.id.profile_pic);
        Picasso.with(this).load(photoURL).error(R.drawable.ic_error).placeholder(R.drawable.ic_placeholder)
                .into(profilePic);

        if (savedInstanceState == null) {
            Fragment fragment = new EventsToday();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "EventsToday").commit();
            navigationView.setCheckedItem(R.id.nav_eventtoday);
        }
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
        if (id == R.id.action_settings) {
            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_eventtoday) {
            Fragment fragment = new EventsToday();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "EventsToday").commit();
            navigationView.setCheckedItem(R.id.nav_eventtoday);
        } else if (id == R.id.nav_eventupcoming) {
            Fragment fragment = new EventsUpcoming();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "EventsUpComing").commit();
            navigationView.setCheckedItem(R.id.nav_eventupcoming);
        } else if (id == R.id.nav_eventpast) {
            Fragment fragment = new EventsPast();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "EventsPast").commit();
            navigationView.setCheckedItem(R.id.nav_eventpast);
        } else {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

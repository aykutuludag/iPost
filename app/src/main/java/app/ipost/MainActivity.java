package app.ipost;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.kobakei.ratethisapp.RateThisApp;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static String currentTheme;
    static String ID;
    static String fID;
    static String name;
    static String email;
    static String photo;
    static String gender;
    static String birthday;
    static String location;
    static SharedPreferences prefs;
    NavigationView navigationView;
    Toolbar toolbar;
    Window window;
    boolean doubleBackToExitPressedOnce;

    public static void getStoredValues() {
        ID = prefs.getString("GoogleAccountID", "");
        fID = prefs.getString("FacebookAccountID", "");
        name = prefs.getString("Name", "");
        email = prefs.getString("Email", "");
        photo = prefs.getString("ProfilePhoto", "");
        gender = prefs.getString("Gender", "Other");
        birthday = prefs.getString("Birthday", "");
        location = prefs.getString("Location", "");
        currentTheme = prefs.getString("DefaultTheme", "Black");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = this.getSharedPreferences("ProfileInformation", Context.MODE_PRIVATE);
        getStoredValues();

        // Initializing Toolbar and setting it as the actionbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //ColoredBars
        window = this.getWindow();

        switch (currentTheme) {
            case "Black":
                coloredBars(Color.parseColor("#000000"), Color.parseColor("#212121"));
                break;
            case "Red":
                coloredBars(Color.parseColor("#D32F2F"), Color.parseColor("#F44336"));
                break;
            case "Green":
                coloredBars(Color.parseColor("#388E3C"), Color.parseColor("#4CAF50"));
                break;
            case "Orange":
                coloredBars(Color.parseColor("#5D4037"), Color.parseColor("#795548"));
                break;
            case "Purple":
                coloredBars(Color.parseColor("#7B1FA2"), Color.parseColor("#9C27B0"));
                break;
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, SingleEvent.class);
                startActivity(i);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //Initializing NavigationView
        navigationView = findViewById(R.id.nav_view);

        //Add Navigation header and its ClickListener
        View headerView = getLayoutInflater().inflate(R.layout.nav_header_main, navigationView, false);
        navigationView.addHeaderView(headerView);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "iPost v1.0", Toast.LENGTH_LONG).show();
            }
        });
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            Fragment fragment = new EventsUpcoming();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "Upcoming").commit();
            navigationView.setCheckedItem(R.id.nav_eventupcoming);
            toolbar.setTitle(R.string.events_upcoming);
        }

        // AppRater
        RateThisApp.onCreate(this);
        RateThisApp.showRateDialogIfNeeded(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment;
        switch (item.getItemId()) {
            case R.id.nav_eventupcoming:
                fragment = new EventsUpcoming();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "Upcoming").commit();
                navigationView.setCheckedItem(R.id.nav_eventupcoming);
                toolbar.setTitle(R.string.events_upcoming);
                break;
            case R.id.nav_eventpast:
                fragment = new EventsPast();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "Past").commit();
                navigationView.setCheckedItem(R.id.nav_eventpast);
                toolbar.setTitle(R.string.events_past);
                break;
            case R.id.nav_postplanned:
                fragment = new PostPlanned();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "Planned").commit();
                navigationView.setCheckedItem(R.id.nav_postplanned);
                toolbar.setTitle(R.string.posts_planned);
                break;
            case R.id.nav_postsent:
                fragment = new PostSent();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "Sent").commit();
                navigationView.setCheckedItem(R.id.nav_postsent);
                toolbar.setTitle(R.string.posts_sent);
                break;
            case R.id.nav_help:
                Intent i2 = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(i2);
                break;
            case R.id.nav_privacy:
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                builder.enableUrlBarHiding();
                builder.setShowTitle(true);
                builder.setToolbarColor(Color.parseColor("#212121"));
                customTabsIntent.launchUrl(MainActivity.this, Uri.parse("http://themorngroup.com/privacy"));
                break;
            case R.id.nav_themes:
                Intent i4 = new Intent(MainActivity.this, ThemesActivity.class);
                startActivity(i4);
                break;
            case R.id.nav_rate:
                CustomTabsIntent.Builder builder2 = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent2 = builder2.build();
                builder2.enableUrlBarHiding();
                builder2.setShowTitle(true);
                builder2.setToolbarColor(Color.parseColor("#212121"));
                customTabsIntent2.launchUrl(MainActivity.this, Uri.parse("market://details?id=app.ipost"));
                break;
            case R.id.nav_feedback:
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"contact@themorngroup.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "iPost Feedback");
                startActivity(Intent.createChooser(intent, "Email via..."));
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        navigationView.setCheckedItem(R.id.nav_eventupcoming);

        // Fragments
        EventsUpcoming fragment0 = (EventsUpcoming) getSupportFragmentManager().findFragmentByTag("Upcoming");
        EventsPast fragment1 = (EventsPast) getSupportFragmentManager().findFragmentByTag("Past");
        PostPlanned fragment2 = (PostPlanned) getSupportFragmentManager().findFragmentByTag("Planned");
        PostSent fragment3 = (PostSent) getSupportFragmentManager().findFragmentByTag("Sent");

        // FragmentHome OnBackPressed
        if (fragment0 != null) {
            if (fragment0.isVisible()) {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    return;
                }

                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, getString(R.string.exit), Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }
        }

        // FragmentSecond OnBackPressed
        if (fragment1 != null) {
            if (fragment1.isVisible()) {
                Fragment fragment = new EventsUpcoming();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "Upcoming").commit();
                toolbar.setTitle(R.string.events_upcoming);
            }
        }

        // FragmentThird OnBackPressed
        if (fragment2 != null) {
            if (fragment2.isVisible()) {
                Fragment fragment = new EventsUpcoming();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "Upcoming").commit();
                toolbar.setTitle(R.string.events_upcoming);
            }
        }

        // FragmentFourth OnBackPressed
        if (fragment3 != null) {
            if (fragment3.isVisible()) {
                Fragment fragment = new EventsUpcoming();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "Upcoming").commit();
                toolbar.setTitle(R.string.events_upcoming);
            }
        }
    }
}

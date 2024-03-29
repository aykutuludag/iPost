package app.ipost;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class PermissionsActivity extends AppCompatActivity {

    public static final int REQUEST_CALENDAR_READ = 0;
    public static final int REQUEST_CONTACTS_READ = 1;
    public static final int REQUEST_SMS_SEND = 2;
    static boolean isMessenger;
    Tracker t;
    FrameLayout frame;
    FragmentTransaction transaction;
    boolean isCalendar;
    boolean isContact;
    boolean isSMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Analytics
        t = AnalyticsApplication.getDefaultTracker();
        t.setScreenName("Permissions");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        //FrameLayout
        frame = findViewById(R.id.content);

        if (ActivityCompat.checkSelfPermission(PermissionsActivity.this, android.Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, new PermissionCalendar());
            transaction.commit();
        } else if (ContextCompat.checkSelfPermission(PermissionsActivity.this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, new PermissionContacts());
            transaction.commit();
        } else if (ContextCompat.checkSelfPermission(PermissionsActivity.this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, new PermissionSMS());
            transaction.commit();
        } else if (AccessToken.getCurrentAccessToken() == null) {
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, new PermissionMessenger());
            transaction.commit();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(PermissionsActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }, 1250);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CALENDAR_READ:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isCalendar = true;
                } else {
                    Toast.makeText(PermissionsActivity.this, getString(R.string.error_aborted), Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case REQUEST_CONTACTS_READ:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isContact = true;
                } else {
                    Toast.makeText(PermissionsActivity.this, getString(R.string.error_aborted), Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_SMS_SEND:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isSMS = true;
                } else {
                    Toast.makeText(PermissionsActivity.this, getString(R.string.error_aborted), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isCalendar) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);
            ((PermissionCalendar) fragment).getEvents();

            FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, new PermissionContacts());
            transaction.commit();
            isCalendar = false;
        }

        if (isContact) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);
            ((PermissionContacts) fragment).getContacts();

            transaction = this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, new PermissionSMS());
            transaction.commit();
            isContact = false;
        }

        if (isSMS) {
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, new PermissionMessenger());
            transaction.commit();
            isSMS = false;
        }

        if (isMessenger) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(PermissionsActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }, 1250);

            isMessenger = false;
        }
    }
}
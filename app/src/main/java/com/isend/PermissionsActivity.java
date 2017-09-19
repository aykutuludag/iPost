package com.isend;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class PermissionsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CALENDAR_READ = 0;
    private static final int REQUEST_CONTACTS_READ = 1;
    private static final int REQUEST_SMS_SEND = 2;
    Tracker t;
    RelativeLayout layoutCalendar, layoutContacts, layoutSMS;
    Button buttonCalendar, buttonContacts, buttonSMS;
    SQLiteDatabase database_account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Analytics
        t = ((AnalyticsApplication) this.getApplication()).getDefaultTracker();
        t.setScreenName("Permissions");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        // Create local database to save contacs
        database_account = this.openOrCreateDatabase("database_app", MODE_PRIVATE, null);

        layoutCalendar = findViewById(R.id.permission_calendar);
        layoutContacts = findViewById(R.id.permission_contacts);
        layoutSMS = findViewById(R.id.permission_sms);

        buttonCalendar = findViewById(R.id.permission_calendar_button);
        buttonContacts = findViewById(R.id.permission_contacts_button);
        buttonSMS = findViewById(R.id.permission_sms_button);

        buttonCalendar.setOnClickListener(PermissionsActivity.this);
        buttonContacts.setOnClickListener(PermissionsActivity.this);
        buttonSMS.setOnClickListener(PermissionsActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.permission_calendar_button:
                if (ActivityCompat.checkSelfPermission(PermissionsActivity.this, android.Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PermissionsActivity.this, new String[]{android.Manifest.permission.READ_CALENDAR}, REQUEST_CALENDAR_READ);
                } else {
                    getEvents();
                }
                break;
            case R.id.permission_contacts_button:
                if (ContextCompat.checkSelfPermission(PermissionsActivity.this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PermissionsActivity.this, new String[]{android.Manifest.permission.READ_CONTACTS}, REQUEST_CONTACTS_READ);
                } else {
                    getContacts();
                }
                break;
            case R.id.permission_sms_button:
                if (ContextCompat.checkSelfPermission(PermissionsActivity.this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PermissionsActivity.this, new String[]{android.Manifest.permission.SEND_SMS}, REQUEST_SMS_SEND);
                } else {
                    Toast.makeText(PermissionsActivity.this, "SMS permission granted. Moving on...", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(PermissionsActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }, 1250);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CALENDAR_READ:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getEvents();
                } else {
                    Toast.makeText(PermissionsActivity.this, getString(R.string.error_aborted), Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case REQUEST_CONTACTS_READ:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContacts();
                } else {
                    Toast.makeText(PermissionsActivity.this, getString(R.string.error_aborted), Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_SMS_SEND:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(PermissionsActivity.this, "SMS permission granted. Moving on...", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(PermissionsActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }, 1250);
                } else {
                    Toast.makeText(PermissionsActivity.this, getString(R.string.error_aborted), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getEvents() {
        Cursor cur;
        ContentResolver cr = this.getContentResolver();

        String[] mProjection =
                {
                        "_id",
                        CalendarContract.Events.TITLE,
                        CalendarContract.Events.DESCRIPTION,
                        CalendarContract.Events.DTSTART,
                        CalendarContract.Events.DTEND,
                        CalendarContract.Events.EVENT_LOCATION,
                        CalendarContract.Events.ORGANIZER,
                        CalendarContract.Events.DISPLAY_COLOR
                };

        Uri uri = CalendarContract.Events.CONTENT_URI;
        cur = cr.query(uri, mProjection, null, null, null);

        if (cur != null) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(CalendarContract.Events._ID));
                String title = cur.getString(cur.getColumnIndex(CalendarContract.Events.TITLE));
                String desc = cur.getString(cur.getColumnIndex(CalendarContract.Events.DESCRIPTION));
                String start = cur.getString(cur.getColumnIndex(CalendarContract.Events.DTSTART));
                String end = cur.getString(cur.getColumnIndex(CalendarContract.Events.DTEND));
                String location = cur.getString(cur.getColumnIndex(CalendarContract.Events.EVENT_LOCATION));
                String owner = cur.getString(cur.getColumnIndex(CalendarContract.Events.ORGANIZER));
                String color = cur.getString(cur.getColumnIndex(CalendarContract.Events.DISPLAY_COLOR));

                System.out.println("ID: " + id + "Etkinlik adı:" + title + "Açıklama:" + desc + "saat:" + start + "-" + end + "konum:" + location + "owner:" + owner + "color:" + color);

                ContentValues values = new ContentValues();
                values.put("ID", id);
                values.put("Title", title);
                values.put("Description", desc);
                values.put("Start", start);
                values.put("End", end);
                values.put("Location", location);
                values.put("Owner", owner);
                values.put("Color", color);
                values.put("Source", "Device");
                database_account.insert("events", null, values);
            }
            cur.close();
            layoutCalendar.setVisibility(View.GONE);
            layoutContacts.setVisibility(View.VISIBLE);
            Toast.makeText(this, getString(R.string.permission_calendar_granted), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.error_no_calendar), Toast.LENGTH_SHORT).show();
        }
    }

    private void getContacts() {
        ContentValues values = new ContentValues();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cur != null) {
            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    values.put("ID", id);

                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    values.put("DisplayName", name);

                    if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                        if (pCur != null) {
                            while (pCur.moveToNext()) {
                                String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                values.put("PhoneNumber", phoneNo);
                            }
                            pCur.close();
                        }
                    }
                    database_account.insert("contacts", null, values);
                }
            }
            cur.close();
            layoutContacts.setVisibility(View.GONE);
            layoutSMS.setVisibility(View.VISIBLE);
            Toast.makeText(PermissionsActivity.this, getString(R.string.contact_sync_completed), Toast.LENGTH_SHORT).show();
        }
    }
}

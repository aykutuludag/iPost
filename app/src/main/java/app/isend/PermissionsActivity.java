package app.isend;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    SharedPreferences prefs;

    String contactID, contactName, contactPhone, contactMail, contactPhoto;
    boolean hasNumber, hasMail, hasMessenger, hasWhatsapp;

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

        prefs = this.getSharedPreferences("ProfileInformation", Context.MODE_PRIVATE);

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
        ContentValues values = new ContentValues();

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

                values.put("ID", id);
                values.put("Title", title);
                values.put("Description", desc);
                values.put("Start", start);
                values.put("End", end);
                values.put("Location", location);
                values.put("Owner", owner);
                values.put("Color", color);
                database_account.insert("events", null, values);
            }
            cur.close();
            prefs.edit().putBoolean("isCalendarSync", true).apply();
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
                    contactID = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String[] contactMails = getEmail(contactID).split(";");
                    contactMail = contactMails[0];
                    values.put("ID", contactID);
                    values.put("UserMail", contactMail);
                    contactName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    values.put("DisplayName", contactName);

                    if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{contactID}, null);
                        if (pCur != null) {
                            while (pCur.moveToNext()) {
                                contactPhone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                values.put("PhoneNumber", contactPhone);
                                contactPhoto = getContactPhoto(PermissionsActivity.this, contactPhone);
                                values.put("ContactPhoto", contactPhoto);
                            }
                            pCur.close();
                        }
                    }
                    database_account.insert("contacts", null, values);
                }
            }
            cur.close();

            prefs.edit().putBoolean("isContactSync", true).apply();
            layoutContacts.setVisibility(View.GONE);
            layoutSMS.setVisibility(View.VISIBLE);
            Toast.makeText(PermissionsActivity.this, getString(R.string.contact_sync_completed), Toast.LENGTH_SHORT).show();
        }
    }

    public String getEmail(String contactId) {
        String emailStr = "";
        final String[] projection = new String[]{ContactsContract.CommonDataKinds.Email.DATA,
                ContactsContract.CommonDataKinds.Email.TYPE};

        Cursor emailq = managedQuery(ContactsContract.CommonDataKinds.Email.CONTENT_URI, projection, ContactsContract.Data.CONTACT_ID + "=?", new String[]{contactId}, null);

        if (emailq.moveToFirst()) {
            final int contactEmailColumnIndex = emailq.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);

            while (!emailq.isAfterLast()) {
                emailStr = emailStr + emailq.getString(contactEmailColumnIndex) + ";";
                emailq.moveToNext();
            }
        }
        return emailStr;
    }

    public static String getContactPhoto(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.PHOTO_URI}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactImage = null;
        if (cursor.moveToFirst()) {
            contactImage = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_URI));
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }
        return contactImage;
    }
}

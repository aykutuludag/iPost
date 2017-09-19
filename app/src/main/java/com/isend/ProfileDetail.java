package com.isend;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import static android.content.Context.MODE_PRIVATE;

public class ProfileDetail extends Fragment {


    private static final int REQUEST_CALENDAR_READ = 0;
    private static final int REQUEST_CONTACT_READ = 1;
    Tracker t;
    SQLiteDatabase database_account;
    SharedPreferences prefs;
    Boolean isContactSync, isCalendarSync;
    TextView calendarText, contactsText;
    ImageView calendarButton, contactsButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_detail, container, false);

        // Analytics
        t = ((AnalyticsApplication) getActivity().getApplication()).getDefaultTracker();
        t.setScreenName("Profile / Detail");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        prefs = getActivity().getSharedPreferences("ProfileInformation", Context.MODE_PRIVATE);
        isContactSync = prefs.getBoolean("isContactSync", false);
        isCalendarSync = prefs.getBoolean("isCalendarSync", false);

        // Create local database to save contacs
        database_account = getActivity().openOrCreateDatabase("database_app", MODE_PRIVATE, null);

        // CALENDAR
        calendarButton = v.findViewById(R.id.calendar_button);
        calendarText = v.findViewById(R.id.calendar_title);

        if (isCalendarSync) {
            calendarText.setTextColor(Color.GREEN);
        } else {
            calendarText.setTextColor(Color.BLACK);
        }

        // CONTACTS
        contactsButton = v.findViewById(R.id.contacts_button);
        contactsText = v.findViewById(R.id.contacts_title);

        if (isContactSync) {
            contactsText.setTextColor(Color.GREEN);
        } else {
            contactsText.setTextColor(Color.BLACK);
        }

        View.OnClickListener buttonListener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.calendar_button:
                        if (!isCalendarSync) {
                            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_CALENDAR}, REQUEST_CALENDAR_READ);
                            } else {
                                getEvents();
                            }
                        } else {
                            deleteEvents();
                        }
                    case R.id.contacts_button:
                        if (isContactSync) {
                            deleteContacts();
                        } else {
                            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_CONTACTS}, 0);
                            } else {
                                getContacts();
                            }
                        }
                        break;
                }
            }
        };
        calendarButton.setOnClickListener(buttonListener);
        contactsButton.setOnClickListener(buttonListener);

        return v;
    }

    private void getContacts() {
        ContentResolver cr = getActivity().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                ContentValues values = new ContentValues();

                //  Contact ID
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                values.put("ID", id);

                //  CONTACT NAME
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                values.put("DisplayName", name);

                // PHONE NUMBER
                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    if (pCur != null) {
                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                            values.put("PhoneNumber", phoneNo);
                        }
                        pCur.close();
                    }
                }

                values.put("ProfilePhoto", "1");
                values.put("hasWhatsapp", "1");
                values.put("hasMessenger", "1");
                values.put("hasMail", "1");
                database_account.insert("contacts", null, values);

                prefs.edit().putBoolean("isContactSync", true).apply();
                Toast.makeText(getActivity(), getString(R.string.contact_sync_completed), Toast.LENGTH_SHORT).show();
            }
        }
        if (cur != null) {
            cur.close();
        }
    }

    private void deleteContacts() {

    }

    @SuppressWarnings("MissingPermission")
    private void getEvents() {
        Cursor cur;
        ContentResolver cr = getActivity().getContentResolver();

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
            Toast.makeText(getActivity(), getString(R.string.permission_calendar_granted), Toast.LENGTH_SHORT).show();
            prefs.edit().putBoolean("isCalendarSync", true).apply();
            getActivity().finish();
            startActivity(getActivity().getIntent());
        } else {
            prefs.edit().putBoolean("isCalendarSync", false).apply();
            Toast.makeText(getActivity(), getString(R.string.error_no_calendar), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteEvents() {
        Cursor cur = database_account.rawQuery("DELETE FROM events WHERE Source='device'", null);
        cur.close();
        Toast.makeText(getActivity(), getString(R.string.permission_calendar_revoked), Toast.LENGTH_SHORT).show();
        prefs.edit().putBoolean("isCalendarSync", false).apply();
        getActivity().finish();
        startActivity(getActivity().getIntent());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CALENDAR_READ: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getEvents();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.error_aborted), Toast.LENGTH_SHORT)
                            .show();
                    prefs.edit().putBoolean("isCalendarSync", false).apply();
                }
                break;
            }
            case REQUEST_CONTACT_READ: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContacts();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.error_aborted), Toast.LENGTH_SHORT).show();
                    prefs.edit().putBoolean("isContactSync", false).apply();
                }
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}

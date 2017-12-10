package app.ipost;

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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import static app.ipost.PermissionsActivity.REQUEST_CALENDAR_READ;

public class PermissionCalendar extends Fragment {

    Tracker t;
    SharedPreferences prefs;
    Button buttonCalendar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.permission_calendar, container, false);

        // Analytics
        t = AnalyticsApplication.getDefaultTracker();
        t.setScreenName("Permissions - Calendar");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());


        prefs = getActivity().getSharedPreferences("ProfileInformation", Context.MODE_PRIVATE);
        buttonCalendar = v.findViewById(R.id.permission_calendar_button);
        buttonCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_CALENDAR}, REQUEST_CALENDAR_READ);
                    } else {
                        getEvents();
                    }
                } else {
                    getEvents();
                }
            }
        });

        return v;
    }

    @SuppressWarnings("MissingPermission")
    public void getEvents() {
        Cursor cur;
        ContentResolver cr = getActivity().getContentResolver();
        ContentValues values = new ContentValues();
        ContentValues values2 = new ContentValues();

        SQLiteDatabase database_account = getActivity().openOrCreateDatabase("database_app", Context.MODE_PRIVATE, null);

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
                // String id = cur.getString(cur.getColumnIndex(CalendarContract.Events._ID));
                String title = cur.getString(cur.getColumnIndex(CalendarContract.Events.TITLE));
                String desc = cur.getString(cur.getColumnIndex(CalendarContract.Events.DESCRIPTION));
                String start = cur.getString(cur.getColumnIndex(CalendarContract.Events.DTSTART));
                String end = cur.getString(cur.getColumnIndex(CalendarContract.Events.DTEND));
                String location = cur.getString(cur.getColumnIndex(CalendarContract.Events.EVENT_LOCATION));
                // String color = cur.getString(cur.getColumnIndex(CalendarContract.Events.DISPLAY_COLOR));

                values.put("title", title);
                values.put("description", desc);
                values.put("sTime", start);
                values.put("eTime", end);
                values.put("location", location);
                values.put("owner", "");
                values.put("color", Color.parseColor("#777777"));
                values.put("isMail", 0);
                values.put("isSMS", 0);
                values.put("isMessenger", 0);
                values.put("isWhatsapp", 0);
                database_account.insert("events", null, values);

                values2.put("receiverName", "");
                values2.put("receiverMail", "");
                values2.put("receiverPhone", "");
                values2.put("postTime", "");
                values2.put("isDelivered", 0);
                values2.put("mailTitle", "");
                values2.put("mailContent", "");
                values2.put("mailAttachment", "");
                values2.put("smsContent", "");
                values2.put("messengerContent", "");
                values2.put("messengerAttachment", "");
                values2.put("whatsappContent", "");
                values2.put("whatsappAttachment", "");
                database_account.insert("posts", null, values2);
            }
            cur.close();
            database_account.close();
            prefs.edit().putBoolean("isCalendarSync", true).apply();
            Toast.makeText(getActivity(), getString(R.string.permission_calendar_granted), Toast.LENGTH_SHORT).show();
        } else {
            prefs.edit().putBoolean("isCalendarSync", false).apply();
            Toast.makeText(getActivity(), getString(R.string.error_no_calendar), Toast.LENGTH_SHORT).show();
        }

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, new PermissionContacts());
        transaction.commit();
    }
}

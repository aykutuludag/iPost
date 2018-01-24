package app.ipost;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import static app.ipost.PermissionsActivity.REQUEST_CONTACTS_READ;


public class PermissionContacts extends Fragment {

    Tracker t;
    Button buttonContacts;
    SQLiteDatabase database_account;
    SharedPreferences prefs;

    FrameLayout frame;
    FragmentTransaction transaction;

    String contactID, contactName, contactPhone, contactMail, contactPhoto;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.permission_contacts, container, false);

        // Analytics
        t = AnalyticsApplication.getDefaultTracker();
        t.setScreenName("Permissions - Contacts");
        t.enableAdvertisingIdCollection(true);
        t.send(new HitBuilders.ScreenViewBuilder().build());

        prefs = getActivity().getSharedPreferences("ProfileInformation", Context.MODE_PRIVATE);

        // Create local database to save contacs
        database_account = getActivity().openOrCreateDatabase("database_app", Context.MODE_PRIVATE, null);

        buttonContacts = v.findViewById(R.id.permission_contacts_button);
        buttonContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_CONTACTS}, REQUEST_CONTACTS_READ);
                    } else {
                        getContacts();
                    }
                } else {
                    getContacts();
                }
            }
        });

        frame = v.findViewById(R.id.content);

        return v;
    }

    void getContacts() {
        ContentValues values = new ContentValues();
        ContentResolver cr = getActivity().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (cur != null) {
            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    contactID = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    values.put("ID", contactID);

                    contactName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    values.put("displayName", contactName);
                    values.put("userMail", getEmail(contactID));
                    values.put("hasWhatsapp", hasWhatsApp(contactID));
                    values.put("hasMessenger", hasMessenger(contactID));

                    if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{contactID}, null);
                        if (pCur != null) {
                            while (pCur.moveToNext()) {
                                contactPhone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                values.put("phoneNumber", contactPhone);

                                values.put("contactPhoto", getContactPhoto(getActivity(), contactPhone));
                            }
                            pCur.close();
                        }
                    }
                    database_account.insert("contacts", null, values);
                }
            }
            cur.close();

            prefs.edit().putBoolean("isContactSync", true).apply();
        }
    }

    public String getEmail(String contactId) {
        String emailStr = null;
        final String[] projection = new String[]{ContactsContract.CommonDataKinds.Email.DATA,
                ContactsContract.CommonDataKinds.Email.TYPE};

        Cursor emailq = getActivity().managedQuery(ContactsContract.CommonDataKinds.Email.CONTENT_URI, projection, ContactsContract.Data.CONTACT_ID + "=?", new String[]{contactId}, null);

        if (emailq.moveToFirst()) {
            final int contactEmailColumnIndex = emailq.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);

            while (!emailq.isAfterLast()) {
                emailStr = emailq.getString(contactEmailColumnIndex);
                emailq.moveToNext();
            }
        }
        return emailStr;
    }

    public int hasWhatsApp(String contactID) {
        int whatsAppExists = 0;
        boolean hasWhatsApp;

        String[] projection = new String[]{ContactsContract.RawContacts._ID};
        String selection = ContactsContract.Data.CONTACT_ID + " = ? AND account_type IN (?)";
        String[] selectionArgs = new String[]{contactID, "com.whatsapp"};
        Cursor cursor = getActivity().getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, selectionArgs, null);
        if (cursor != null) {
            hasWhatsApp = cursor.moveToNext();
            if (hasWhatsApp) {
                whatsAppExists = 1;
            }
            cursor.close();
        }
        return whatsAppExists;
    }

    public int hasMessenger(String contactID) {
        int messengerExists = 0;
        boolean hasMessenger;

        String[] projection = new String[]{ContactsContract.RawContacts._ID};
        String selection = ContactsContract.Data.CONTACT_ID + " = ? AND account_type IN (?)";
        String[] selectionArgs = new String[]{contactID, "com.facebook.katana"};
        Cursor cursor = getActivity().getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI, projection, selection, selectionArgs, null);
        if (cursor != null) {
            hasMessenger = cursor.moveToNext();
            if (hasMessenger) {
                messengerExists = 1;
            }
            cursor.close();
        }
        return messengerExists;
    }
}

package app.ipost;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.ipost.adapter.RecipientAdapter;
import app.ipost.model.ContactItem;
import app.ipost.receiver.AlarmReceiver;

public class SingleEvent extends AppCompatActivity {

    ExpandableRelativeLayout expandableLayout1, expandableLayout2, expandableLayout3, expandableLayout4, expandableLayout5, expandableLayout6, expandableLayout7;

    // Databese connection
    SQLiteDatabase db, database_account, database_account2, database_account3;
    Cursor cur0, cur, cur2, cur3;
    List<ContactItem> feedsList = new ArrayList<>();

    // Some variables
    boolean newEvent;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    int userChoice;
    //Event settings
    TextView title, description, location, timeStart, timeEnd;
    //tableEvent
    int eventID, eventColor, isMail, isSMS, isMessenger, isWhatsapp;
    long startTime, endTime;
    String eventName, eventDescription, eventLocation, eventOwner;
    //tablePost
    int isDelivered;
    long postTime;
    String receiverName, receiverMail, receiverPhone, mailTitle, mailContent, mailAttachment, smsContent, messengerContent, messengerAttachment, whatsappContent, whatsappAttachment;
    // User selection & Post channels
    ContactItem item;
    Spinner mySpinner;
    EditText smsContentHolder, mailTitleHolder, mailContentHolder, messengerContentHolder, whatsappContentHolder;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat mFormatter = new SimpleDateFormat("dd-MMMM-yyyy HH:mm");
    //Listener for startTime
    SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            timeStart.setText(mFormatter.format(date));
            startTime = date.getTime();
        }

        // Optional cancel listener
        @Override
        public void onDateTimeCancel() {

        }
    };
    //Listener for endTime
    SlideDateTimeListener listener2 = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            timeEnd.setText(mFormatter.format(date));
            endTime = date.getTime();
        }

        // Optional cancel listener
        @Override
        public void onDateTimeCancel() {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_event);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        expandableLayout1 = findViewById(R.id.expandableLayout1);
        expandableLayout2 = findViewById(R.id.expandableLayout2);
        expandableLayout3 = findViewById(R.id.expandableLayout3);
        expandableLayout4 = findViewById(R.id.expandableLayout4);
        expandableLayout5 = findViewById(R.id.expandableLayout5);
        expandableLayout6 = findViewById(R.id.expandableLayout6);
        expandableLayout7 = findViewById(R.id.expandableLayout7);

        prefs = this.getSharedPreferences("SINGLE_EVENT", Context.MODE_PRIVATE);
        editor = prefs.edit();
        eventID = getIntent().getIntExtra("EVENT_ID", 0);

        if (eventID == 0) {
            eventColor = Color.BLACK;
            startTime = Calendar.getInstance().getTimeInMillis() + 60000 * 10;
            endTime = Calendar.getInstance().getTimeInMillis() + 60000 * 60;
            newEvent = true;
            db = this.openOrCreateDatabase("database_app", MODE_PRIVATE, null);
            cur0 = db.rawQuery("SELECT MAX(ID) FROM events", new String[]{});
            if (cur0 != null) {
                if (cur0.moveToFirst()) {
                    eventID = cur0.getInt(0) + 1;
                    System.out.println("Yeni ID:" + eventID);
                }
                cur0.close();
            }
            db.close();
        }
        userChoice = prefs.getInt("userChoiceSpinner" + eventID, 0);

        // Database connection
        getEventInfo();
        getContactInfo();
        getPostInfo();

        //update UserInterface
        updateUI();
    }

    public void expandableButton1(View view) {
        expandableLayout1.toggle(); // toggle expand and collapse
        expandableLayout2.collapse();
        expandableLayout3.collapse();
    }

    public void expandableButton2(View view) {
        expandableLayout2.toggle(); // toggle expand and collapse
        expandableLayout1.collapse();
        expandableLayout3.collapse();
    }

    public void expandableButton3(View view) {
        expandableLayout3.toggle(); // toggle expand and collapse
        expandableLayout1.collapse();
        expandableLayout2.collapse();
    }

    public void expandableButton4(View view) {
        expandableLayout4.toggle(); // toggle expand and collapse
        expandableLayout5.collapse();
        expandableLayout6.collapse();
        expandableLayout7.collapse();
    }

    public void expandableButton5(View view) {
        expandableLayout5.toggle(); // toggle expand and collapse
        expandableLayout4.collapse();
        expandableLayout6.collapse();
        expandableLayout7.collapse();
    }

    public void expandableButton6(View view) {
        expandableLayout6.toggle(); // toggle expand and collapse
        expandableLayout4.collapse();
        expandableLayout5.collapse();
        expandableLayout7.collapse();
    }

    public void expandableButton7(View view) {
        expandableLayout7.toggle(); // toggle expand and collapse
        expandableLayout4.collapse();
        expandableLayout5.collapse();
        expandableLayout6.collapse();
    }

    public void getEventInfo() {
        database_account = this.openOrCreateDatabase("database_app", MODE_PRIVATE, null);
        cur = database_account.rawQuery("SELECT * FROM events WHERE ID=?", new String[]{eventID + ""});
        if (cur != null && cur.getCount() != 0) {
            cur.moveToFirst();
            for (int i = 0; i < cur.getColumnCount(); i++) {
                switch (i % 13) {
                    case 0:
                        eventID = cur.getInt(i);
                        break;
                    case 1:
                        eventName = cur.getString(i);
                        break;
                    case 2:
                        eventDescription = cur.getString(i);
                        break;
                    case 3:
                        startTime = cur.getLong(i);
                        break;
                    case 4:
                        endTime = cur.getLong(i);
                        break;
                    case 5:
                        eventLocation = cur.getString(i);
                        break;
                    case 6:
                        eventOwner = cur.getString(i);
                        break;
                    case 7:
                        eventColor = cur.getInt(i);
                        break;
                    case 8:
                        isMail = cur.getInt(i);
                        break;
                    case 9:
                        isSMS = cur.getInt(i);
                        break;
                    case 10:
                        isMessenger = cur.getInt(i);
                        break;
                    case 12:
                        isWhatsapp = cur.getInt(i);
                        break;
                }
            }
            cur.close();
        } else {
            Toast.makeText(SingleEvent.this, "You are creating a new event!", Toast.LENGTH_LONG).show();
            getSupportActionBar().setTitle("Add new event");
        }
    }

    public void getContactInfo() {
        database_account3 = this.openOrCreateDatabase("database_app", MODE_PRIVATE, null);
        cur3 = database_account3.rawQuery("SELECT * FROM contacts ORDER BY DisplayName ASC", null);
        if (cur3 != null && cur3.getCount() != 0) {
            cur3.moveToFirst();
            do {
                for (int i = 0; i < cur3.getColumnCount(); i++) {
                    switch (i % 7) {
                        case 0:
                            item = new ContactItem();
                            item.setID(cur3.getString(i));
                            break;
                        case 1:
                            item.setName(cur3.getString(i));
                            break;
                        case 2:
                            item.setPhoneNumber(cur3.getString(i));
                            break;
                        case 3:
                            item.setMail(cur3.getString(i));
                            break;
                        case 4:
                            item.setWhastaspp(cur3.getInt(i));
                            break;
                        case 5:
                            item.setMessenger(cur3.getInt(i));
                            break;
                        case 6:
                            item.setContactPhoto(cur3.getString(i));
                            feedsList.add(item);
                            break;
                    }
                }
            } while (cur3.moveToNext());
            cur3.close();
            database_account3.close();
        }
    }

    public void getPostInfo() {
        database_account2 = this.openOrCreateDatabase("database_app", MODE_PRIVATE, null);
        cur2 = database_account2.rawQuery("SELECT * FROM posts WHERE ID=? ", new String[]{eventID + ""});
        if (cur2 != null && cur2.getCount() != 0) {
            cur2.moveToFirst();
            for (int i = 0; i < cur2.getColumnCount(); i++) {
                switch (i % 14) {
                    case 0:
                        eventID = cur2.getInt(i);
                        break;
                    case 1:
                        receiverName = cur2.getString(i);
                        break;
                    case 2:
                        receiverMail = cur2.getString(i);
                        break;
                    case 3:
                        receiverPhone = cur2.getString(i);
                        break;
                    case 4:
                        postTime = cur2.getLong(i);
                        break;
                    case 5:
                        isDelivered = cur2.getInt(i);
                        break;
                    case 6:
                        mailTitle = cur2.getString(i);
                        break;
                    case 7:
                        mailContent = cur2.getString(i);
                        break;
                    case 8:
                        mailAttachment = cur2.getString(i);
                        break;
                    case 9:
                        smsContent = cur2.getString(i);
                        break;
                    case 10:
                        messengerContent = cur2.getString(i);
                        break;
                    case 11:
                        messengerAttachment = cur2.getString(i);
                        break;
                    case 12:
                        whatsappContent = cur2.getString(i);
                        break;
                    case 13:
                        whatsappAttachment = cur2.getString(i);
                        break;
                }
            }
            cur2.close();
        }
    }

    public void updateUI() {
        title = findViewById(R.id.editEventName);
        title.setText(eventName);
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    eventName = editable.toString();
                }
            }
        });

        description = findViewById(R.id.editTextDesc);
        description.setText(eventDescription);
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    eventDescription = editable.toString();
                }
            }
        });

        location = findViewById(R.id.editTextLocation);
        location.setText(eventLocation);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =
                        null;
                try {
                    intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(SingleEvent.this);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        timeStart = findViewById(R.id.editTextStartTime);
        timeStart.setText(getDate(startTime));
        timeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener)
                        .setIs24HourTime(true)
                        .build()
                        .show();
            }
        });

        timeEnd = findViewById(R.id.editTextEndTime);
        timeEnd.setText(getDate(endTime));
        timeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener2)
                        .setIs24HourTime(true)
                        .build()
                        .show();
            }
        });

        //Choose user and create post options based on user
        smsContentHolder = findViewById(R.id.sms_content);
        mailTitleHolder = findViewById(R.id.mail_title);
        mailContentHolder = findViewById(R.id.mail_content);
        messengerContentHolder = findViewById(R.id.messenger_content);
        whatsappContentHolder = findViewById(R.id.whatsapp_content);

        mySpinner = findViewById(R.id.spinner);
        mySpinner.setAdapter(new RecipientAdapter(SingleEvent.this, R.id.spinner, feedsList));
        mySpinner.setSelection(userChoice);
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v,
                                       int position, long arg3) {
                //GET VALUES
                item = feedsList.get(position);

                //Receiver name
                receiverName = item.getName();

                //Receiver phone
                if (item.getPhoneNumber() != null) {
                    receiverPhone = item.getPhoneNumber();
                    isSMS = 1;
                } else {
                    isSMS = 0;
                }

                if (item.getMail() != null && !item.getMail().contains("null")) {
                    receiverMail = item.getMail();
                    isMail = 1;
                } else {
                    isMail = 0;
                }

                isMessenger = item.getMessenger();
                isWhatsapp = item.getWhatsapp();

                //CREATE UI BASED ON VALUES
                if (isSMS == 0) {
                    smsContentHolder.setEnabled(false);
                } else {
                    smsContentHolder.setEnabled(true);
                }

                if (isMail == 0) {
                    mailContentHolder.setEnabled(false);
                } else {
                    mailContentHolder.setEnabled(true);
                }

                if (isMessenger == 0) {
                    messengerContentHolder.setEnabled(false);
                } else {
                    messengerContentHolder.setEnabled(true);
                }

                if (isWhatsapp == 0) {
                    whatsappContentHolder.setEnabled(false);
                } else {
                    whatsappContentHolder.setEnabled(true);
                }

                //SAVE ROW NUMBER
                userChoice = mySpinner.getSelectedItemPosition();
                editor.putInt("userChoiceSpinner" + eventID, userChoice);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                //Do nothing
            }
        });

        //SMS
        smsContentHolder.setText(smsContent);
        smsContentHolder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    smsContent = editable.toString();
                }
            }
        });


        //MAİL
        mailTitleHolder.setText(mailTitle);
        mailTitleHolder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    mailTitle = editable.toString();
                }
            }
        });

        mailContentHolder.setText(mailContent);
        mailContentHolder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    mailContent = editable.toString();
                }
            }
        });

        //MESSENGER
        messengerContentHolder.setText(messengerContent);
        messengerContentHolder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    messengerContent = editable.toString();
                }
            }
        });


        //WHATSAPP
        whatsappContentHolder.setText(whatsappContent);
        whatsappContentHolder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    whatsappContent = editable.toString();
                }
            }
        });
    }

    public void addEvent() {
        ContentValues values = new ContentValues();
        values.put("title", eventName);
        values.put("description", eventDescription);
        values.put("sTime", startTime);
        values.put("eTime", endTime);
        values.put("location", eventLocation);
        values.put("owner", eventOwner);
        values.put("color", eventColor);
        values.put("isMail", isMail);
        values.put("isSMS", isSMS);
        values.put("isMessenger", isMessenger);
        values.put("isWhatsapp", isWhatsapp);
        database_account.insert("events", null, values);
        database_account.close();
        newEvent = false;

        ContentValues values2 = new ContentValues();
        values2.put("receiverName", receiverName);
        values2.put("receiverMail", receiverMail);
        values2.put("receiverPhone", receiverPhone);
        values2.put("postTime", postTime);
        values2.put("isDelivered", 0);
        values2.put("mailTitle", mailTitle);
        values2.put("mailContent", mailContent);
        values2.put("mailAttachment", mailAttachment);
        values2.put("smsContent", smsContent);
        values2.put("messengerContent", messengerContent);
        values2.put("messengerAttachment", messengerAttachment);
        values2.put("whatsappContent", whatsappContent);
        values2.put("whatsappAttachment", whatsappAttachment);

        values2.put("receiverName", item.getName());
        values2.put("receiverMail", item.getMail());
        values2.put("receiverPhone", item.getPhoneNumber());
        values2.put("postTime", startTime);
        values2.put("isDelivered", 0);
        values2.put("mailTitle", mailTitle);
        values2.put("mailContent", mailContent);
        values2.put("mailAttachment", mailAttachment);
        values2.put("smsContent", smsContent);
        values2.put("messengerContent", messengerContent);
        values2.put("messengerAttachment", messengerAttachment);
        values2.put("whatsappContent", whatsappContent);
        values2.put("whatsappAttachment", whatsappAttachment);
        database_account2.insert("posts", null, values2);
        database_account2.close();

        Toast.makeText(SingleEvent.this,
                "Event created", Toast.LENGTH_SHORT).show();
    }

    public void updateEvent() {
        ContentValues values = new ContentValues();
        values.put("title", eventName);
        values.put("description", eventDescription);
        values.put("sTime", startTime);
        values.put("eTime", endTime);
        values.put("location", eventLocation);
        values.put("owner", eventOwner);
        values.put("color", eventColor);
        values.put("isMail", isMail);
        values.put("isSMS", isSMS);
        values.put("isMessenger", isMessenger);
        values.put("isWhatsapp", isWhatsapp);
        String[] selectionArgs = {String.valueOf(eventID)};
        database_account.update("events", values, "ID=?", selectionArgs);
        database_account.close();

        ContentValues values2 = new ContentValues();
        values2.put("receiverName", item.getName());
        values2.put("receiverMail", item.getMail());
        values2.put("receiverPhone", item.getPhoneNumber());
        values2.put("postTime", startTime);
        values2.put("isDelivered", 0);
        values2.put("mailTitle", mailTitle);
        values2.put("mailContent", mailContent);
        values2.put("mailAttachment", mailAttachment);
        values2.put("smsContent", smsContent);
        values2.put("messengerContent", messengerContent);
        values2.put("messengerAttachment", messengerAttachment);
        values2.put("whatsappContent", whatsappContent);
        values2.put("whatsappAttachment", whatsappAttachment);
        String[] selectionArgs2 = {String.valueOf(eventID)};
        database_account2.update("posts", values2, "ID=?", selectionArgs2);
        database_account2.close();

        Toast.makeText(SingleEvent.this,
                "Event updated", Toast.LENGTH_SHORT).show();
    }

    public void createPost() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent myIntent = new Intent(SingleEvent.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(SingleEvent.this, eventID, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.set(AlarmManager.RTC, postTime, pendingIntent);
    }

    private String getDate(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy HH:mm", Locale.getDefault());
        return sdf.format(date);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            if (newEvent) {
                addEvent();
            } else {
                updateEvent();
            }
            if (postTime >= System.currentTimeMillis()) {
                createPost();
            }
            editor.apply();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                eventLocation = place.getName().toString();
                location.setText(eventLocation);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i("Error", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        database_account.close();
        database_account2.close();
        finish();
    }
}



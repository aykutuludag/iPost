package app.ipost;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.ipost.adapter.RecipientAdapter;
import app.ipost.model.ContactItem;
import app.ipost.receiver.AlarmReceiver;


public class SingleEvent extends AppCompatActivity implements ColorPickerDialogListener {

    // Databese connection
    SQLiteDatabase database_account, database_account2, database_account3;
    Cursor cur, cur2, cur3;

    // Some variables
    boolean newEvent;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    int userChoice;

    //Event settings
    TextView title, description, location, timeStart, timeEnd;
    ImageView colorPicker;

    //tableEvent
    int eventID, eventColor, isMail, isSMS, isMessenger, isWhatsapp;
    long startTime, endTime;
    String eventName, eventDescription, eventLocation, eventOwner;

    //tablePost
    int postID, isDelivered;
    long postTime;
    String receiverName, receiverMail, receiverPhone, mailTitle, mailContent, mailAttachment, smsContent, messengerContent, messengerAttachment, whatsappContent, whatsappAttachment;

    // User selection & Post channels
    ContactItem item;
    Spinner mySpinner;
    EditText smsContentHolder, mailContentHolder, messengerContentHolder, whatsappContentHolder;
    private CheckBox cb1;
    private CheckBox cb2;
    private CheckBox cb3;
    private CheckBox cb4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_event);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = this.getSharedPreferences("SINGLE_EVENT", Context.MODE_PRIVATE);
        editor = prefs.edit();
        eventID = getIntent().getIntExtra("EVENT_ID", 0);
        if (eventID == 0) {
            eventColor = Color.BLACK;
            startTime = Calendar.getInstance().getTimeInMillis() + 600000;
            endTime = Calendar.getInstance().getTimeInMillis() + 3600000;
            newEvent = true;
        }
        userChoice = prefs.getInt("userChoiceSpinner" + eventID, 0);

        database_account = this.openOrCreateDatabase("database_app", MODE_PRIVATE, null);
        cur = database_account.rawQuery("SELECT * FROM events WHERE ID=?", new String[]{eventID + ""});
        if (cur != null && cur.getCount() != 0) {
            cur.moveToFirst();
            for (int i = 0; i < cur.getColumnCount(); i++) {
                switch (i % 13) {
                    case 0:
                        eventID = cur.getInt(i);
                        postID = eventID;
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
                // Initialize
                SwitchDateTimeDialogFragment dateTimeDialogFragment = SwitchDateTimeDialogFragment.newInstance(
                        "Choose start time",
                        "OK",
                        "Cancel"
                );

                Date start = new Date(startTime);

                dateTimeDialogFragment.startAtCalendarView();
                dateTimeDialogFragment.set24HoursMode(true);
                dateTimeDialogFragment.setMinimumDateTime(start);
                dateTimeDialogFragment.setDefaultDateTime(start);

                // Define new day and month format
                try {
                    dateTimeDialogFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MMMM dd", Locale.getDefault()));
                } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
                    Log.e("Error", e.getMessage());
                }

                // Set listener
                dateTimeDialogFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Date date) {
                        final SimpleDateFormat myDateFormat = new SimpleDateFormat("d MMM yyyy HH:mm", java.util.Locale.getDefault());
                        timeStart.setText(myDateFormat.format(date));
                        startTime = date.getTime();
                    }

                    @Override
                    public void onNegativeButtonClick(Date date) {
                        final SimpleDateFormat myDateFormat = new SimpleDateFormat("d MMM yyyy HH:mm", java.util.Locale.getDefault());
                        timeStart.setText(myDateFormat.format(date));
                        startTime = date.getTime();
                    }
                });

                // Show
                dateTimeDialogFragment.show(getSupportFragmentManager(), "dialog_time");
            }
        });

        timeEnd = findViewById(R.id.editTextEndTime);
        timeEnd.setText(getDate(endTime));
        timeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Initialize
                SwitchDateTimeDialogFragment dateTimeDialogFragment = SwitchDateTimeDialogFragment.newInstance(
                        "Choose start time",
                        "OK",
                        "Cancel"
                );

                Date end = new Date(endTime);

                dateTimeDialogFragment.startAtCalendarView();
                dateTimeDialogFragment.set24HoursMode(true);
                dateTimeDialogFragment.setMinimumDateTime(end);
                dateTimeDialogFragment.setDefaultDateTime(end);

                // Define new day and month format
                try {
                    dateTimeDialogFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MMMM dd", Locale.getDefault()));
                } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
                    Log.e("Error", e.getMessage());
                }

                // Set listener
                dateTimeDialogFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Date date) {
                        final SimpleDateFormat myDateFormat = new SimpleDateFormat("d MMM yyyy HH:mm", java.util.Locale.getDefault());
                        timeEnd.setText(myDateFormat.format(date));
                        endTime = date.getTime();
                    }

                    @Override
                    public void onNegativeButtonClick(Date date) {
                        final SimpleDateFormat myDateFormat = new SimpleDateFormat("d MMM yyyy HH:mm", java.util.Locale.getDefault());
                        timeEnd.setText(myDateFormat.format(date));
                        endTime = date.getTime();
                    }
                });

                // Show
                dateTimeDialogFragment.show(getSupportFragmentManager(), "dialog_time");
            }
        });

        colorPicker = findViewById(R.id.colorPicker);
        colorPicker.setBackgroundColor(eventColor);
        colorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialog.newBuilder().setColor(eventColor).setDialogId(1).show(SingleEvent.this);
            }
        });


        //Choose user and create post options based on user
        cb1 = findViewById(R.id.checkBoxSMS);
        cb2 = findViewById(R.id.checkBoxMail);
        cb3 = findViewById(R.id.checkBoxMessenger);
        cb4 = findViewById(R.id.checkBoxWhatsapp);
        smsContentHolder = findViewById(R.id.sms_content);
        mailContentHolder = findViewById(R.id.mail_content);
        messengerContentHolder = findViewById(R.id.messenger_content);
        whatsappContentHolder = findViewById(R.id.whatsapp_content);

        final List<ContactItem> feedsList = new ArrayList<>();
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

        //BURADA POST ÇEKİYORUZ
        database_account2 = this.openOrCreateDatabase("database_app", MODE_PRIVATE, null);
        cur2 = database_account2.rawQuery("SELECT * FROM posts WHERE ID=? ", new String[]{eventID + ""});
        System.out.println("anan");
        if (cur2 != null && cur2.getCount() != 0) {
            cur2.moveToFirst();
            System.out.println("anan2");
            for (int i = 0; i < cur2.getColumnCount(); i++) {
                System.out.println(cur2.getInt(i));
                System.out.println(cur2.getString(i));
                switch (i % 14) {
                    case 0:
                        postID = cur2.getInt(i);
                        break;
                    case 1:
                        receiverMail = cur2.getString(i);
                        break;
                    case 2:
                        receiverPhone = cur2.getString(i);
                        break;
                    case 3:
                        postTime = cur2.getLong(i);
                        break;
                    case 4:
                        isDelivered = cur2.getInt(i);
                        break;
                    case 5:
                        mailTitle = cur2.getString(i);
                        break;
                    case 6:
                        mailContent = cur2.getString(i);
                        break;
                    case 7:
                        mailAttachment = cur2.getString(i);
                        break;
                    case 8:
                        smsContent = cur2.getString(i);
                        break;
                    case 9:
                        messengerContent = cur2.getString(i);
                        break;
                    case 10:
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
                    cb1.setEnabled(false);
                    smsContentHolder.setEnabled(false);
                } else {
                    cb1.setEnabled(true);
                    smsContentHolder.setEnabled(true);
                }

                if (isMail == 0) {
                    cb2.setEnabled(false);
                    mailContentHolder.setEnabled(false);
                } else {
                    cb2.setEnabled(true);
                    mailContentHolder.setEnabled(true);
                }

                if (isMessenger == 0) {
                    cb3.setEnabled(false);
                    messengerContentHolder.setEnabled(false);
                } else {
                    cb3.setEnabled(true);
                    messengerContentHolder.setEnabled(true);
                }

                if (isWhatsapp == 0) {
                    cb4.setEnabled(false);
                    whatsappContentHolder.setEnabled(false);
                } else {
                    cb4.setEnabled(true);
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

        if (smsContent != null) {
            cb1.setChecked(true);
        } else {
            cb1.setChecked(false);
        }
        cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cb1.isChecked()) {
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
                                smsPlanner();
                            }
                        }
                    });
                }
            }
        });
        smsContentHolder.setText(smsContent);


        if (mailContent != null) {
            cb2.setChecked(true);
        } else {
            cb2.setChecked(false);
        }
        cb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cb2.isChecked()) {
                    //CREATE MESSAGE DB
                } else {
                    // DELETE IT
                }
            }
        });

        if (messengerContent != null) {
            cb3.setChecked(true);
        } else {
            cb3.setChecked(false);
        }
        cb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cb3.isChecked()) {
                    //CREATE MESSAGE DB
                } else {
                    // DELETE IT
                }
            }
        });

        if (whatsappContent != null) {
            cb4.setChecked(true);
        } else {
            cb4.setChecked(false);
        }
        cb4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cb4.isChecked()) {
                    //CREATE MESSAGE DB
                } else {
                    // DELETE IT
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
    }

    public void createPost() {
        ContentValues values2 = new ContentValues();
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
        database_account2.insert("posts", null, values2);
        database_account2.close();
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
    }

    public void updatePost() {
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
        values2.put("whatsappContent", whatsappAttachment);
        values2.put("whatsappAttachment", whatsappAttachment);
        String[] selectionArgs = {String.valueOf(postID)};
        database_account2.update("posts", values2, "ID=?", selectionArgs);
        database_account2.close();
    }

    public void smsPlanner() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent myIntent = new Intent(SingleEvent.this, AlarmReceiver.class);
        myIntent.putExtra("type", "SMS");
        myIntent.putExtra("phone", receiverPhone);
        myIntent.putExtra("message", smsContent);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(SingleEvent.this, 0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar = Calendar.getInstance();

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(startTime);

        if (calendar.getTimeInMillis() < calendar2.getTimeInMillis()) {
            alarmManager.set(AlarmManager.RTC, calendar2.getTimeInMillis(), pendingIntent);
        } else {
            Toast.makeText(SingleEvent.this, "Your post will not launch because you set starting time in past.", Toast.LENGTH_LONG).show();
        }
    }

    private String getDate(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM YYYY HH:mm", Locale.getDefault());
        return sdf.format(date);
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        switch (dialogId) {
            case 1:
                colorPicker.setBackgroundColor(color);
                eventColor = color;
                break;
        }
    }

    @Override
    public void onDialogDismissed(int i) {

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
                createPost();
            } else {
                updateEvent();
                updatePost();
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

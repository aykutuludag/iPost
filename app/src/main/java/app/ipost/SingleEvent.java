package app.ipost;

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


public class SingleEvent extends AppCompatActivity implements ColorPickerDialogListener {


    SQLiteDatabase database_account;
    Cursor cur;
    TextView title, description, location, timeStart, timeEnd;
    ImageView colorPicker;

    int id;
    String eventName, eventDescription, eventLocation, eventOwner;
    int eventColor;
    long startTime, endTime;
    int isMail, isSMS, isMessenger, isWhatsapp;
    ContentValues values;
    ContactItem item;
    boolean newEvent;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    SharedPreferences prefs;
    int selectedPosition;
    EditText smsContent, mailContent, messengerContent, whatsappContent;
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

        prefs = this.getSharedPreferences("EventInfo", Context.MODE_PRIVATE);

        id = getIntent().getIntExtra("EVENT_ID", 0);
        if (id == 0) {
            eventColor = Color.BLACK;
            startTime = Calendar.getInstance().getTimeInMillis() + 600000;
            endTime = Calendar.getInstance().getTimeInMillis() + 3600000;
            newEvent = true;
        }

        prefs = this.getSharedPreferences("EventInfo", Context.MODE_PRIVATE);
        selectedPosition = prefs.getInt("SelectedPosition" + id, 0);

        database_account = this.openOrCreateDatabase("database_app", MODE_PRIVATE, null);
        cur = database_account.rawQuery("SELECT * FROM events WHERE ID=? ", new String[]{id + ""});
        if (cur != null && cur.getCount() != 0) {
            cur.moveToFirst();
            for (int i = 0; i < cur.getColumnCount(); i++) {
                switch (i % 13) {
                    case 0:
                        id = cur.getInt(i);
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

        values = new ContentValues();

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

        // Choose contact
        final List<ContactItem> feedsList = new ArrayList<>();
        database_account = this.openOrCreateDatabase("database_app", MODE_PRIVATE, null);
        cur = database_account.rawQuery("SELECT * FROM contacts ORDER BY DisplayName ASC", null);
        if (cur != null && cur.getCount() != 0) {
            cur.moveToFirst();
            do {
                for (int i = 0; i < cur.getColumnCount(); i++) {
                    switch (i % 7) {
                        case 0:
                            item = new ContactItem();
                            item.setID(cur.getString(i));
                            break;
                        case 1:
                            item.setName(cur.getString(i));
                            break;
                        case 2:
                            item.setPhoneNumber(cur.getString(i));
                            break;
                        case 3:
                            item.setMail(cur.getString(i));
                            break;
                        case 4:
                            item.setWhastaspp(cur.getInt(i));
                            break;
                        case 5:
                            item.setMessenger(cur.getInt(i));
                            break;
                        case 6:
                            item.setContactPhoto(cur.getString(i));
                            feedsList.add(item);
                            break;
                    }
                }
            } while (cur.moveToNext());
            cur.close();
        } else {
            // Error no contact found
            Toast.makeText(SingleEvent.this, "No friends", Toast.LENGTH_LONG).show();
        }


        //KULLANICI SEÇİMİ VE ALAKALI İŞLER
        //Tasarımdaki Checkbox'ları çekiyoruz.
        cb1 = findViewById(R.id.checkBoxSMS);
        cb2 = findViewById(R.id.checkBoxMail);
        cb3 = findViewById(R.id.checkBoxMessenger);
        cb4 = findViewById(R.id.checkBoxWhatsapp);

        smsContent = findViewById(R.id.sms_content);
        mailContent = findViewById(R.id.mail_content);
        messengerContent = findViewById(R.id.messenger_content);
        whatsappContent = findViewById(R.id.whatsapp_content);

        Spinner mySpinner = findViewById(R.id.spinner);

        mySpinner.setSelection(selectedPosition);
        mySpinner.setAdapter(new RecipientAdapter(SingleEvent.this, R.id.spinner, feedsList));
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v,
                                       int position, long arg3) {
                selectedPosition = position;
                prefs.edit().putInt("SelectedPosition" + id, selectedPosition);

                //GET VALUES
                ContactItem feedItem = feedsList.get(selectedPosition);
                System.out.println(feedItem.getMail());

                if (feedItem.getPhoneNumber() != null) {
                    isSMS = 1;
                } else {
                    isSMS = 0;
                }

                if (feedItem.getMail() != null && !feedItem.getMail().contains("null")) {
                    isMail = 1;
                } else {
                    isMail = 0;
                }

                isMessenger = feedItem.getMessenger();
                isWhatsapp = feedItem.getWhatsapp();

                //CREATE UI BASED ON VALUES
                if (isSMS == 0) {
                    cb1.setEnabled(false);
                    smsContent.setEnabled(false);
                } else {
                    cb1.setEnabled(true);
                    smsContent.setEnabled(true);
                }

                if (isMail == 0) {
                    cb2.setEnabled(false);
                    mailContent.setEnabled(false);
                } else {
                    cb2.setEnabled(true);
                    mailContent.setEnabled(true);
                }

                if (isMessenger == 0) {
                    cb3.setEnabled(false);
                    messengerContent.setEnabled(false);
                } else {
                    cb3.setEnabled(true);
                    messengerContent.setEnabled(true);
                }

                if (isWhatsapp == 0) {
                    cb4.setEnabled(false);
                    whatsappContent.setEnabled(false);
                } else {
                    cb4.setEnabled(true);
                    whatsappContent.setEnabled(true);
                }

                //WRITE ON DATABASE
                values.put("isMail", isMail);
                values.put("isSMS", isSMS);
                values.put("isMessenger", isMessenger);
                values.put("isWhatsapp", isWhatsapp);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                //Do nothing
            }
        });

        cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cb1.isChecked()) {
                    //CREATE MESSAGE DB
                } else {
                    // DELETE IT
                }
            }
        });
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

    public void updateEvent() {
        values.put("description", eventDescription);
        values.put("start", startTime);
        values.put("end", endTime);
        values.put("location", eventLocation);
        values.put("owner", eventOwner);
        values.put("color", eventColor);
        values.put("isMail", isMail);
        values.put("isSMS", isSMS);
        values.put("isMessenger", isMessenger);
        values.put("isWhatsapp", isWhatsapp);
        String[] selectionArgs = {String.valueOf(id)};
        database_account.update("events", values, "ID=?", selectionArgs);
    }

    public void addEvent() {
        values.put("title", eventName);
        values.put("description", eventDescription);
        values.put("start", startTime);
        values.put("end", endTime);
        values.put("location", eventLocation);
        values.put("owner", eventOwner);
        values.put("color", eventColor);
        values.put("isMail", isMail);
        values.put("isSMS", isSMS);
        values.put("isMessenger", isMessenger);
        values.put("isWhatsapp", isWhatsapp);
        database_account.insert("events", null, values);
        newEvent = false;
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
            } else {
                updateEvent();
            }
            prefs.edit().apply();
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
        finish();
    }
}

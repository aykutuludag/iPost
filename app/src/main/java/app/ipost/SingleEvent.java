package app.ipost;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
    String eventName, eventPhotoURI, eventDescription, eventLocation, eventOwner;
    int eventColor;
    long startTime, endTime;
    int hasMail, hasSMS, hasMessenger, hasWhatsapp;

    ContactItem item;

    boolean newEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_event);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        id = getIntent().getIntExtra("EVENT_ID", 0);
        if (id == 0) {
            eventColor = Color.BLACK;
            startTime = Calendar.getInstance().getTimeInMillis() + 60000;
            endTime = Calendar.getInstance().getTimeInMillis() + 3600000;
            newEvent = true;
        }

        database_account = this.openOrCreateDatabase("database_app", MODE_PRIVATE, null);
        cur = database_account.rawQuery("SELECT * FROM events WHERE ID=? ", new String[]{id + ""});
        if (cur != null && cur.getCount() != 0) {
            cur.moveToFirst();
            for (int i = 0; i < cur.getColumnCount(); i++) {
                switch (i % 13) {
                    case 0:
                        System.out.println(cur.getString(i));
                        break;
                    case 1:
                        eventName = cur.getString(i);
                        break;
                    case 2:
                        eventPhotoURI = cur.getString(i);
                        break;
                    case 3:
                        eventDescription = cur.getString(i);
                        break;
                    case 4:
                        startTime = cur.getLong(i);
                        break;
                    case 5:
                        endTime = cur.getLong(i);
                        break;
                    case 6:
                        eventLocation = cur.getString(i);
                        break;
                    case 7:
                        eventOwner = cur.getString(i);
                        break;
                    case 8:
                        eventColor = cur.getInt(i);
                        break;
                    case 9:
                        hasMail = cur.getInt(i);
                        break;
                    case 10:
                        hasSMS = cur.getInt(i);
                        break;
                    case 11:
                        hasMessenger = cur.getInt(i);
                        break;
                    case 12:
                        hasWhatsapp = cur.getInt(i);
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
                    if (newEvent) {
                        addEvent();
                    } else {
                        updateEvent();
                    }
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
                    if (newEvent) {
                        addEvent();
                    } else {
                        updateEvent();
                    }
                }
            }
        });

        location = findViewById(R.id.editTextLocation);
        location.setText(eventLocation);
        location.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    eventLocation = editable.toString();
                    if (newEvent) {
                        addEvent();
                    } else {
                        updateEvent();
                    }
                }
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

                final Date start = new Date(startTime);

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
                        if (newEvent) {
                            addEvent();
                        } else {
                            updateEvent();
                        }
                    }

                    @Override
                    public void onNegativeButtonClick(Date date) {
                        final SimpleDateFormat myDateFormat = new SimpleDateFormat("d MMM yyyy HH:mm", java.util.Locale.getDefault());
                        timeEnd.setText(myDateFormat.format(date));
                        endTime = date.getTime();
                        if (newEvent) {
                            addEvent();
                        } else {
                            updateEvent();
                        }
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
        List<ContactItem> feedsList = new ArrayList<>();
        database_account = this.openOrCreateDatabase("database_app", MODE_PRIVATE, null);
        cur = database_account.rawQuery("SELECT * FROM contacts ORDER BY DisplayName ASC", null);
        if (cur != null && cur.getCount() != 0) {
            cur.moveToFirst();
            do {
                for (int i = 0; i < cur.getColumnCount(); i++) {
                    switch (i % 7) {
                        case 0:
                            item = new ContactItem();
                            item.setID(cur.getInt(i));
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


        Spinner mySpinner = findViewById(R.id.spinner);
        mySpinner.setAdapter(new RecipientAdapter(SingleEvent.this, R.id.spinner, feedsList));
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v,
                                       int position, long arg3) {
                // TODO Auto-generated method stub
                String SpinerValue3 = parent.getItemAtPosition(position).toString();

                Toast.makeText(getBaseContext(),
                        "You have selected: " + SpinerValue3,
                        Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        switch (dialogId) {
            case 1:
                colorPicker.setBackgroundColor(color);
                eventColor = color;
                if (newEvent) {
                    addEvent();
                } else {
                    updateEvent();
                }
                break;
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

    public void updateEvent() {
        ContentValues values = new ContentValues();
        values.put("title", eventName);
        values.put("photoURI", eventPhotoURI);
        values.put("description", eventDescription);
        values.put("start", startTime);
        values.put("end", endTime);
        values.put("location", eventLocation);
        values.put("owner", eventOwner);
        values.put("color", eventColor);
        values.put("isMail", hasMail);
        values.put("isSMS", hasSMS);
        values.put("isMessenger", hasMessenger);
        values.put("isWhatsapp", hasWhatsapp);
        String[] selectionArgs = {String.valueOf(id)};
        database_account.update("events", values, "ID=?", selectionArgs);
        System.out.println(id);
    }

    public void addEvent() {
        ContentValues values = new ContentValues();
        values.put("title", eventName);
        values.put("description", eventDescription);
        values.put("start", startTime);
        values.put("end", endTime);
        values.put("location", eventLocation);
        values.put("owner", eventOwner);
        values.put("color", eventColor);
        values.put("isMail", hasMail);
        values.put("isSMS", hasSMS);
        values.put("isMessenger", hasMessenger);
        values.put("isWhatsapp", hasWhatsapp);
        database_account.insert("events", null, values);
        System.out.println(id);
        newEvent = false;
    }

    private String getDate(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM YYYY HH:mm", Locale.getDefault());
        return sdf.format(date);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

package app.ipost;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.ipost.adapter.ContactAdapter;
import app.ipost.model.ContactItem;
import app.ipost.receiver.AlarmReceiver;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;

public class SingleEvent extends AppCompatActivity {

    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public static final int REQUEST_EXTERNAL_STORAGE = 2;
    //tableEvent
    public static int eventID, eventColor, isMail, isSMS, isMessenger, isWhatsapp;
    public static String receiverName, receiverMail, receiverPhone, mailTitle, mailContent, mailAttachment, smsContent, messengerContent, messengerAttachment, whatsappContent, whatsappAttachment;
    public static boolean isEventEditing;
    private static String[] PERMISSIONS_STORAGE = {android.Manifest.permission.READ_EXTERNAL_STORAGE};
    ExpandableRelativeLayout expandableLayout1, expandableLayout2, expandableLayout3, expandableLayout4, expandableLayout5, expandableLayout6, expandableLayout7;
    Button expandableButton1, expandableButton2, expandableButton3;
    // Databese connection
    SQLiteDatabase db, database_account, database_account2, database_account3;
    Cursor cur0, cur, cur2, cur3;
    ContactItem item;
    // Some variables
    boolean newEvent;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String userChoice;
    //Event settings
    TextView title, description, location, timeStart;
    long startTime;
    String eventName, eventDescription, eventLocation, eventOwner;
    //tablePost
    int isDelivered;
    // Post channels
    EditText smsContentHolder, mailTitleHolder, mailContentHolder, messengerContentHolder, whatsappContentHolder;
    Button emailFileChooser, messengerFileChooser, whatsappFileChooser;
    TextView emailFileHolder, messengerFileHolder, whatsappFileHolder;
    boolean mailIsChoosing, messengerIsChoosing, whatsappIsChoosing;
    String currentTheme;
    Toolbar toolbar;
    Window window;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_event);

        // Initializing Toolbar and setting it as the actionbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        isEventEditing = true;

        //ColoredBars
        window = this.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        prefs = this.getSharedPreferences("SINGLE_EVENT", Context.MODE_PRIVATE);
        editor = prefs.edit();
        eventID = getIntent().getIntExtra("EVENT_ID", 0);
        if (eventID == 0) {
            newEvent = true;
            startTime = System.currentTimeMillis() + 60000 * 5;
            db = this.openOrCreateDatabase("database_app", MODE_PRIVATE, null);
            cur0 = db.rawQuery("SELECT MAX(ID) FROM events", new String[]{});
            if (cur0 != null) {
                if (cur0.moveToFirst()) {
                    eventID = cur0.getInt(0) + 1;
                }
                cur0.close();
            }
            db.close();
        }
        userChoice = prefs.getString("userName" + eventID, "");

        expandableLayout1 = findViewById(R.id.expandableLayout1);
        expandableLayout2 = findViewById(R.id.expandableLayout2);
        expandableLayout3 = findViewById(R.id.expandableLayout3);
        expandableLayout4 = findViewById(R.id.expandableLayout4);
        expandableLayout5 = findViewById(R.id.expandableLayout5);
        expandableLayout6 = findViewById(R.id.expandableLayout6);
        expandableLayout7 = findViewById(R.id.expandableLayout7);

        expandableButton1 = findViewById(R.id.expandableButton1);
        expandableButton2 = findViewById(R.id.expandableButton2);
        expandableButton3 = findViewById(R.id.expandableButton3);

        currentTheme = MainActivity.currentTheme;
        switch (currentTheme) {
            case "Black":
                coloredBars(Color.parseColor("#000000"), Color.parseColor("#212121"));
                expandableButton1.setBackgroundColor(Color.parseColor("#212121"));
                expandableButton2.setBackgroundColor(Color.parseColor("#212121"));
                expandableButton3.setBackgroundColor(Color.parseColor("#212121"));
                break;
            case "Red":
                coloredBars(Color.parseColor("#D32F2F"), Color.parseColor("#F44336"));
                expandableButton1.setBackgroundColor(Color.parseColor("#F44336"));
                expandableButton2.setBackgroundColor(Color.parseColor("#F44336"));
                expandableButton3.setBackgroundColor(Color.parseColor("#F44336"));
                break;
            case "Green":
                coloredBars(Color.parseColor("#388E3C"), Color.parseColor("#4CAF50"));
                expandableButton1.setBackgroundColor(Color.parseColor("#4CAF50"));
                expandableButton2.setBackgroundColor(Color.parseColor("#4CAF50"));
                expandableButton3.setBackgroundColor(Color.parseColor("#4CAF50"));
                break;
            case "Orange":
                coloredBars(Color.parseColor("#5D4037"), Color.parseColor("#795548"));
                expandableButton1.setBackgroundColor(Color.parseColor("#795548"));
                expandableButton2.setBackgroundColor(Color.parseColor("#795548"));
                expandableButton3.setBackgroundColor(Color.parseColor("#795548"));
                break;
            case "Purple":
                coloredBars(Color.parseColor("#7B1FA2"), Color.parseColor("#9C27B0"));
                expandableButton1.setBackgroundColor(Color.parseColor("#9C27B0"));
                expandableButton2.setBackgroundColor(Color.parseColor("#9C27B0"));
                expandableButton3.setBackgroundColor(Color.parseColor("#9C27B0"));
                break;
        }

        // Database connection
        getEventInfo();
        getContactInfo(userChoice);
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
        expandableLayout2.toggle();// toggle expand and collapse
        expandableLayout1.collapse();
        expandableLayout3.collapse();
    }

    public void expandableButton3(View view) {
        expandableLayout3.toggle();// toggle expand and collapse
        expandableLayout1.collapse();
        expandableLayout2.collapse();
    }

    public void expandableButton4(View view) {
        expandableLayout4.toggle();
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
                        //EndTime
                        break;
                    case 5:
                        eventLocation = cur.getString(i);
                        System.out.println("Mekan: " + eventLocation);
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
            getSupportActionBar().setTitle("New event");
        }
    }

    public void getContactInfo(String parameter) {
        List<ContactItem> feedsList = new ArrayList<>();
        database_account3 = this.openOrCreateDatabase("database_app", MODE_PRIVATE, null);
        String[] selectionArgs = {parameter + "%"};
        cur3 = database_account3.rawQuery("SELECT * FROM contacts WHERE DisplayName LIKE ? ORDER BY displayName ASC", selectionArgs);
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

        // Adapter
        RecyclerView mRecyclerView = findViewById(R.id.search_result);
        RecyclerView.Adapter mAdapter = new ContactAdapter(SingleEvent.this, feedsList);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapter);

        // The number of Columns
        GridLayoutManager mLayoutManager = new GridLayoutManager(SingleEvent.this, 1);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    public void getPostInfo() {
        database_account2 = this.openOrCreateDatabase("database_app", MODE_PRIVATE, null);
        cur2 = database_account2.rawQuery("SELECT * FROM posts WHERE ID=? ", new String[]{eventID + ""});
        if (cur2 != null && cur2.getCount() != 0) {
            cur2.moveToFirst();
            for (int i = 0; i < cur2.getColumnCount(); i++) {
                switch (i % 14) {
                    case 0:
                        //eventID fetched already
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
                        //startTime fetched already
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
                try {
                    AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                            .build();

                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setFilter(typeFilter)
                            .build(SingleEvent.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
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
                        .setInitialDate(new Date((startTime)))
                        .build()
                        .show();
            }
        });

        //Choose user and create post options based on user
        smsContentHolder = findViewById(R.id.sms_content);
        mailTitleHolder = findViewById(R.id.mail_title);
        mailContentHolder = findViewById(R.id.mail_content);
        emailFileChooser = findViewById(R.id.mail_addfile);
        messengerFileChooser = findViewById(R.id.messenger_addfile);
        whatsappFileChooser = findViewById(R.id.whatsapp_addfile);
        messengerContentHolder = findViewById(R.id.messenger_content);
        whatsappContentHolder = findViewById(R.id.whatsapp_content);
        emailFileHolder = findViewById(R.id.mail_filename);
        messengerFileHolder = findViewById(R.id.messenger_filename);
        whatsappFileHolder = findViewById(R.id.whatsapp_filename);

        SearchView search = findViewById(R.id.search_recipient);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                getContactInfo(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        //CREATE UI BASED ON VALUES
        if (isSMS == 0) {
            smsContentHolder.setEnabled(false);
        } else {
            smsContentHolder.setEnabled(true);
        }

        if (isMail == 0) {
            mailTitleHolder.setEnabled(false);
            mailContentHolder.setEnabled(false);
            emailFileChooser.setEnabled(false);
        } else {
            mailTitleHolder.setEnabled(true);
            mailContentHolder.setEnabled(true);
            emailFileChooser.setEnabled(true);
        }

        if (isMessenger == 0) {
            messengerContentHolder.setEnabled(false);
            messengerFileChooser.setEnabled(false);
        } else {
            messengerContentHolder.setEnabled(false);
            messengerFileChooser.setEnabled(false);
        }

        if (isWhatsapp == 0) {
            whatsappContentHolder.setEnabled(false);
            whatsappFileChooser.setEnabled(false);
        } else {
            whatsappContentHolder.setEnabled(true);
            whatsappFileChooser.setEnabled(true);
        }

        prefs.edit().putString("userName" + eventID, receiverName).apply();

       /* mySpinner = findViewById(R.id.spinner);
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
                    mailTitleHolder.setEnabled(false);
                    mailContentHolder.setEnabled(false);
                    emailFileChooser.setEnabled(false);
                } else {
                    mailTitleHolder.setEnabled(true);
                    mailContentHolder.setEnabled(true);
                    emailFileChooser.setEnabled(true);
                }

                if (isMessenger == 0) {
                    messengerContentHolder.setEnabled(false);
                    messengerFileChooser.setEnabled(false);
                } else {
                    messengerContentHolder.setEnabled(false);
                    messengerFileChooser.setEnabled(false);
                }

                if (isWhatsapp == 0) {
                    whatsappContentHolder.setEnabled(false);
                    whatsappFileChooser.setEnabled(false);
                } else {
                    whatsappContentHolder.setEnabled(true);
                    whatsappFileChooser.setEnabled(true);
                }

                //SAVE ROW NUMBER
                userChoice = mySpinner.getSelectedItemPosition();
                editor.putInt("userChoiceSpinner" + eventID, userChoice);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                //Do nothing
            }
        });*/

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


        emailFileChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verifyStoragePermissions()) {
                    FilePickerBuilder.getInstance().setMaxCount(1)
                            .setActivityTheme(R.style.AppTheme)
                            .addFileSupport("ZIP", new String[]{".zip", ".rar"})
                            .pickFile(SingleEvent.this);
                    mailIsChoosing = true;
                } else {
                    ActivityCompat.requestPermissions(SingleEvent.this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
                }
            }
        });
        emailFileHolder.setText(mailAttachment);

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

        messengerFileChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verifyStoragePermissions()) {
                    FilePickerBuilder.getInstance().setMaxCount(1)
                            .setActivityTheme(R.style.AppTheme)
                            .pickPhoto(SingleEvent.this);
                    messengerIsChoosing = true;
                } else {
                    ActivityCompat.requestPermissions(SingleEvent.this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
                }
            }
        });
        messengerFileHolder.setText(messengerAttachment);


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

        whatsappFileChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verifyStoragePermissions()) {
                    FilePickerBuilder.getInstance().setMaxCount(1)
                            .setActivityTheme(R.style.AppTheme)
                            .pickPhoto(SingleEvent.this);
                    whatsappIsChoosing = true;
                } else {
                    ActivityCompat.requestPermissions(SingleEvent.this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
                }
            }
        });

        whatsappFileHolder.setText(whatsappAttachment);
    }

    public boolean verifyStoragePermissions() {
        boolean hasStorage;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            int permission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            hasStorage = permission == PackageManager.PERMISSION_GRANTED;
        } else {
            hasStorage = true;
        }
        return hasStorage;
    }

    public void addEvent() {
        ContentValues values = new ContentValues();
        values.put("title", eventName);
        values.put("description", eventDescription);
        values.put("sTime", startTime);
        values.put("eTime", 0);
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
        values.put("eTime", 0);
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
        myIntent.putExtra("EVENT_ID", eventID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(SingleEvent.this, eventID, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);


        System.out.println("PostTime" + startTime);

        System.out.println("Şimdiki zaman: " + Calendar.getInstance().getTimeInMillis());

        if (Calendar.getInstance().getTimeInMillis() < startTime) {
            alarmManager.set(AlarmManager.RTC, startTime, pendingIntent);
        } else {
            Toast.makeText(SingleEvent.this, "You chose past time. Post will not be triggered.", Toast.LENGTH_LONG).show();
        }
    }

    private String getDate(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy HH:mm", Locale.getDefault());
        return sdf.format(date);
    }

    public void coloredBars(int color1, int color2) {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color1);
            toolbar.setBackgroundColor(color2);
        } else {
            toolbar.setBackgroundColor(color2);
        }
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
            createPost();
            editor.apply();
            isEventEditing = false;
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Settings saved...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error 001: Permission request rejected by user...", Toast.LENGTH_LONG)
                            .show();
                }
                break;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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
                Toast.makeText(SingleEvent.this, status.toString(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(SingleEvent.this, R.string.error_aborted, Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == FilePickerConst.REQUEST_CODE_PHOTO) {
            if (resultCode == RESULT_OK && data != null) {
                if (mailIsChoosing) {
                    ArrayList<String> aq = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
                    mailAttachment = aq.get(0);
                    emailFileHolder.setText(mailAttachment);
                    mailIsChoosing = false;
                } else if (messengerIsChoosing) {
                    ArrayList<String> aq = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
                    messengerAttachment = aq.get(0);
                    messengerFileHolder.setText(messengerAttachment);
                    messengerIsChoosing = false;
                } else if (whatsappIsChoosing) {
                    ArrayList<String> aq = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
                    whatsappAttachment = aq.get(0);
                    whatsappFileHolder.setText(whatsappAttachment);
                    whatsappIsChoosing = false;
                }
            }
        }
        if (requestCode == FilePickerConst.REQUEST_CODE_DOC) {
            if (resultCode == RESULT_OK && data != null) {
                if (mailIsChoosing) {
                    ArrayList<String> aq = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS);
                    mailAttachment = aq.get(0);
                    emailFileHolder.setText(mailAttachment);
                    mailIsChoosing = false;
                } else if (messengerIsChoosing) {
                    ArrayList<String> aq = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS);
                    messengerAttachment = aq.get(0);
                    messengerFileHolder.setText(messengerAttachment);
                    messengerIsChoosing = false;
                } else if (whatsappIsChoosing) {
                    ArrayList<String> aq = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS);
                    whatsappAttachment = aq.get(0);
                    whatsappFileHolder.setText(whatsappAttachment);
                    whatsappIsChoosing = false;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        database_account.close();
        database_account2.close();
        isEventEditing = false;
        finish();
    }
}



package app.ipost;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.squareup.picasso.Picasso;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

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
    public static final int CONTACT_PICKER_REQUEST = 3;
    //tableEvent
    public static int eventID, eventColor, isMail, isSMS, isMessenger, isWhatsapp;
    public static String receiverName, receiverMail, receiverPhone, mailTitle, mailContent, mailAttachment, smsContent, messengerContent, messengerAttachment, whatsappContent, whatsappAttachment = null;
    // Some variables
    public static boolean isInSingleEvent;
    private static String[] PERMISSIONS_STORAGE = {android.Manifest.permission.READ_EXTERNAL_STORAGE};
    boolean newEvent;
    ExpandableRelativeLayout expandableLayout1, expandableLayout2, expandableLayout3, expandableLayout4, expandableLayout5, expandableLayout6, expandableLayout7, expandableLayout8, expandableLayout9;
    Button expandableButton1, expandableButton2, expandableButton3, expandableButton4, expandableButton5, expandableButton6, expandableButton7, expandableButton8, expandableButton9;
    // Databese connection
    SQLiteDatabase db, database_account, database_account2, database_account3;
    Cursor cur0, cur, cur2, cur3;
    ContactItem item;
    //Event settings
    TextView title, description, location, timeStart, timeStart2;
    RadioGroup retentionChooser;
    RadioButton sunday, monday, tuesday, wednesday, thursday, friday, saturday;
    Calendar retcal;
    long startTime;
    String eventName, eventDescription, eventLocation, eventOwner;
    //tablePost
    int isDelivered;
    // Post channels
    EditText smsContentHolder, mailTitleHolder, mailContentHolder, messengerContentHolder, whatsappContentHolder;
    Button emailFileChooser, messengerFileChooser, whatsappFileChooser;
    ImageView emailFileHolder, messengerFileHolder, whatsappFileHolder;
    boolean mailIsChoosing, messengerIsChoosing, whatsappIsChoosing;
    String currentTheme;
    Toolbar toolbar;
    Window window;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    List<ContactItem> feedsList;
    boolean isTekrarli;

    //Listener for startTime
    SlideDateTimeListener listener = new SlideDateTimeListener() {
        SimpleDateFormat mFormatter = new SimpleDateFormat("dd-MMMM-yyyy HH:mm");

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

        isInSingleEvent = true;

        // Initializing Toolbar and setting it as the actionbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.single_activity_edit_event);

        //ColoredBars
        window = this.getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        eventID = getIntent().getIntExtra("EVENT_ID", 0);
        if (eventID == 0) {
            newEvent = true;

            getSupportActionBar().setTitle(R.string.single_activity_add_event);
            startTime = System.currentTimeMillis() + 60000 * 60;
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

        expandableButton1 = findViewById(R.id.expandableButton1);
        expandableButton2 = findViewById(R.id.expandableButton2);
        expandableButton3 = findViewById(R.id.expandableButton3);
        expandableButton4 = findViewById(R.id.expandableButton4);
        expandableButton5 = findViewById(R.id.expandableButton5);
        expandableButton6 = findViewById(R.id.expandableButton6);
        expandableButton7 = findViewById(R.id.expandableButton7);
        expandableButton8 = findViewById(R.id.expandableButton8);
        expandableButton9 = findViewById(R.id.expandableButton9);

        expandableLayout1 = findViewById(R.id.expandableLayout1);
        expandableLayout2 = findViewById(R.id.expandableLayout2);
        expandableLayout3 = findViewById(R.id.expandableLayout3);
        expandableLayout4 = findViewById(R.id.expandableLayout4);
        expandableLayout5 = findViewById(R.id.expandableLayout5);
        expandableLayout6 = findViewById(R.id.expandableLayout6);
        expandableLayout7 = findViewById(R.id.expandableLayout7);
        expandableLayout8 = findViewById(R.id.expandableLayout8);
        expandableLayout9 = findViewById(R.id.expandableLayout9);

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

        feedsList = new ArrayList<>();
        mRecyclerView = findViewById(R.id.search_result);
        mAdapter = new ContactAdapter(SingleEvent.this, feedsList);

        // Database connection
        getEventInfo();
        getPostInfo();
        getContactInfo(receiverName);

        //update UserInterface
        updateUI();
    }

    public void buttonClick1(View view) {
        expandableLayout1.toggle(); // toggle expand and collapse
        expandableLayout2.collapse();
        expandableLayout3.collapse();
    }

    public void buttonClick2(View view) {
        expandableLayout2.toggle();// toggle expand and collapse
        expandableLayout1.collapse();
        expandableLayout3.collapse();
    }

    public void buttonClick3(View view) {
        expandableLayout3.toggle();// toggle expand and collapse
        expandableLayout1.collapse();
        expandableLayout2.collapse();
    }

    public void buttonClick4(View view) {
        expandableLayout4.toggle();
        expandableLayout5.collapse();
        expandableLayout6.collapse();
        expandableLayout7.collapse();
    }

    public void buttonClick5(View view) {
        expandableLayout5.toggle(); // toggle expand and collapse
        expandableLayout4.collapse();
        expandableLayout6.collapse();
        expandableLayout7.collapse();
    }

    public void buttonClick6(View view) {
        expandableLayout6.toggle(); // toggle expand and collapse
        expandableLayout4.collapse();
        expandableLayout5.collapse();
        expandableLayout7.collapse();
    }

    public void buttonClick7(View view) {
        expandableLayout7.toggle(); // toggle expand and collapse
        expandableLayout4.collapse();
        expandableLayout5.collapse();
        expandableLayout6.collapse();
    }

    public void buttonClick8(View view) {
        expandableLayout8.toggle(); // toggle expand and collapse
        expandableLayout9.collapse();
        expandableButton8.setAlpha(1.0f);
        expandableButton9.setAlpha(0.5f);
        isTekrarli = false;
    }

    public void buttonClick9(View view) {
        expandableLayout9.toggle(); // toggle expand and collapse
        expandableLayout8.collapse();
        expandableButton9.setAlpha(1.0f);
        expandableButton8.setAlpha(0.5f);
        isTekrarli = true;
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
                        //EndTime depraceted
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

    public void getContactInfo(String parameter) {
        feedsList.clear();
        System.out.println("PARAMETER" + parameter);
        String[] names = null;
        String[] selectionArgs = null;
        StringBuilder query;

        if (parameter != null && parameter.length() > 0) {
            names = parameter.split(";");
        }
        if (names == null) {
            query = new StringBuilder("SELECT * FROM contacts WHERE displayName LIKE ? ORDER BY displayName ASC");
        } else {
            query = new StringBuilder("SELECT * FROM contacts WHERE");
            selectionArgs = new String[names.length];
            for (int i = 0; i < names.length; i++) {
                if (i == 0) {
                    query.append(" displayName LIKE ?");
                } else {
                    query.append(" OR displayName LIKE ?");
                }
                selectionArgs[i] = names[i] + "%";
            }
            query.append(" ORDER BY displayName ASC");
        }

        database_account3 = this.openOrCreateDatabase("database_app", MODE_PRIVATE, null);
        cur3 = database_account3.rawQuery(query.toString(), selectionArgs);
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
                            if (cur3.getString(i) != null) {
                                isSMS = 1;
                            } else {
                                isSMS = 0;
                            }
                            break;
                        case 3:
                            item.setMail(cur3.getString(i));
                            if (cur3.getString(i) != null) {
                                isMail = 1;
                            } else {
                                isMail = 0;
                            }
                            break;
                        case 4:
                            item.setWhastaspp(cur3.getInt(i));
                            if (cur3.getInt(i) == 1) {
                                isWhatsapp = 1;
                            } else {
                                isWhatsapp = 0;
                            }
                            break;
                        case 5:
                            item.setMessenger(cur3.getInt(i));
                            if (cur3.getInt(i) == 1) {
                                isMessenger = 1;
                            } else {
                                isMessenger = 0;
                            }
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
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapter);

        // The number of Columns
        GridLayoutManager mLayoutManager = new GridLayoutManager(SingleEvent.this, 1);
        mRecyclerView.setLayoutManager(mLayoutManager);

        updateUI();
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

        //TEKLİ
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

        //MULTİPLE
        //isDelivered haftanın retention kontrolü ve haftanın günüün tutmak için kullanılıyor
        retcal = Calendar.getInstance();
        retentionChooser = findViewById(R.id.radioGroupRetention);
        sunday = findViewById(R.id.radioSunday);
        monday = findViewById(R.id.radioMonday);
        tuesday = findViewById(R.id.radioTuesday);
        wednesday = findViewById(R.id.radioWednesday);
        thursday = findViewById(R.id.radioThursday);
        friday = findViewById(R.id.radioFriday);
        saturday = findViewById(R.id.radioSaturday);

        if (isDelivered == 0) {
            isTekrarli = false;
            expandableLayout8.expand();
            expandableLayout9.collapse();
            expandableButton8.setAlpha(1.0f);
            expandableButton9.setAlpha(0.5f);
        } else {
            isTekrarli = true;
            expandableLayout8.collapse();
            expandableLayout9.expand();
            expandableButton8.setAlpha(0.5f);
            expandableButton9.setAlpha(1.0f);
            if (isDelivered == 1) {
                sunday.setChecked(true);
            } else if (isDelivered == 2) {
                monday.setChecked(true);
            } else if (isDelivered == 3) {
                tuesday.setChecked(true);
            } else if (isDelivered == 4) {
                wednesday.setChecked(true);
            } else if (isDelivered == 5) {
                thursday.setChecked(true);
            } else if (isDelivered == 6) {
                friday.setChecked(true);
            } else if (isDelivered == 7) {
                saturday.setChecked(true);
            }
        }

        retentionChooser.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                if (checkedId == R.id.radioMonday) {
                    retcal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                } else if (checkedId == R.id.radioTuesday) {
                    retcal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                } else if (checkedId == R.id.radioWednesday) {
                    retcal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                } else if (checkedId == R.id.radioThursday) {
                    retcal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                } else if (checkedId == R.id.radioFriday) {
                    retcal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                } else if (checkedId == R.id.radioSaturday) {
                    retcal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                } else if (checkedId == R.id.radioSunday) {
                    retcal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                }
                isDelivered = retcal.get(Calendar.DAY_OF_WEEK);
            }
        });

        timeStart2 = findViewById(R.id.editTextStartTime2);
        timeStart2.setText(getTime(startTime));
        timeStart2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(SingleEvent.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        retcal.set(Calendar.HOUR_OF_DAY, selectedHour);
                        retcal.set(Calendar.MINUTE, selectedMinute);
                        timeStart2.setText(getTime(retcal.getTimeInMillis()));
                        startTime = retcal.getTimeInMillis();
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setCancelable(true);
                mTimePicker.setTitle(getString(R.string.pls_choose_time));
                mTimePicker.show();
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

        ImageView search = findViewById(R.id.search_recipient);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MultiContactPicker.Builder(SingleEvent.this)
                        .showPickerForResult(CONTACT_PICKER_REQUEST);
            }
        });

        //CREATE UI BASED ON VALUES
        if (isSMS == 0) {
            expandableButton4.setAlpha(0.5f);
            expandableButton4.setEnabled(false);

            smsContentHolder.setEnabled(false);
        } else {
            expandableButton4.setAlpha(1.0f);
            expandableButton4.setEnabled(true);

            smsContentHolder.setEnabled(true);
        }

        if (isMail == 0) {
            expandableButton5.setAlpha(0.5f);
            expandableButton5.setEnabled(false);

            mailTitleHolder.setEnabled(false);
            mailContentHolder.setEnabled(false);
            emailFileChooser.setEnabled(false);
        } else {
            expandableButton5.setAlpha(1.0f);
            expandableButton5.setEnabled(true);

            mailTitleHolder.setEnabled(true);
            mailContentHolder.setEnabled(true);
            emailFileChooser.setEnabled(true);
        }

        if (isMessenger == 0) {
            expandableButton6.setAlpha(0.5f);
            expandableButton6.setEnabled(false);

            messengerContentHolder.setEnabled(false);
            messengerFileChooser.setEnabled(false);
        } else {
            expandableButton6.setAlpha(1.0f);
            expandableButton6.setEnabled(true);

            messengerContentHolder.setEnabled(false);
            messengerFileChooser.setEnabled(false);
        }

        if (isWhatsapp == 0) {
            expandableButton7.setAlpha(0.5f);
            expandableButton7.setEnabled(false);

            whatsappContentHolder.setEnabled(false);
            whatsappFileChooser.setEnabled(false);
        } else {
            expandableButton7.setAlpha(1.0f);
            expandableButton7.setEnabled(true);

            whatsappContentHolder.setEnabled(true);
            whatsappFileChooser.setEnabled(true);
        }

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
                } else {
                    smsContent = "";
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
                } else {
                    mailTitle = "";
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
                } else {
                    mailContent = "";
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
        if (mailAttachment != null) {
            Picasso.with(SingleEvent.this).load(Uri.parse(mailAttachment)).error(R.drawable.ic_file).placeholder(R.drawable.ic_file)
                    .into(emailFileHolder);
        }

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
                } else {
                    messengerContent = "";
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
        if (messengerAttachment != null) {
            Picasso.with(SingleEvent.this).load(Uri.parse(messengerAttachment)).error(R.drawable.ic_file).placeholder(R.drawable.ic_file)
                    .into(messengerFileHolder);
        }

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
                } else {
                    whatsappContent = "";
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
        if (whatsappAttachment != null) {
            Picasso.with(SingleEvent.this).load(Uri.parse(whatsappAttachment)).error(R.drawable.ic_file).placeholder(R.drawable.ic_file)
                    .into(whatsappFileHolder);
        }
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
        if (mailTitle != null && mailTitle.length() > 0) {
            values.put("isMail", 1);
        } else {
            values.put("isMail", 0);
        }
        if (smsContent != null && smsContent.length() > 0) {
            values.put("isSMS", 1);
        } else {
            values.put("isSMS", 0);
        }
        if (messengerContent != null && messengerContent.length() > 0) {
            values.put("isMessenger", 1);
        } else {
            values.put("isMessenger", 0);
        }
        if (whatsappContent != null && whatsappContent.length() > 0) {
            values.put("isWhatsapp", 1);
        } else {
            values.put("isWhatsapp", 0);
        }
        database_account.insert("events", null, values);
        database_account.close();
        newEvent = false;

        ContentValues values2 = new ContentValues();
        values2.put("receiverName", receiverName);
        values2.put("receiverMail", receiverMail);
        values2.put("receiverPhone", receiverPhone);
        values2.put("postTime", startTime);
        values2.put("isDelivered", isDelivered);
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
                R.string.event_created, Toast.LENGTH_SHORT).show();
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
        if (mailTitle != null && mailTitle.length() > 0) {
            values.put("isMail", 1);
        } else {
            values.put("isMail", 0);
        }
        if (smsContent != null && smsContent.length() > 0) {
            values.put("isSMS", 1);
        } else {
            values.put("isSMS", 0);
        }
        if (messengerContent != null && messengerContent.length() > 0) {
            values.put("isMessenger", 1);
        } else {
            values.put("isMessenger", 0);
        }
        if (whatsappContent != null && whatsappContent.length() > 0) {
            values.put("isWhatsapp", 1);
        } else {
            values.put("isWhatsapp", 0);
        }
        String[] selectionArgs = {String.valueOf(eventID)};
        database_account.update("events", values, "ID=?", selectionArgs);
        database_account.close();

        ContentValues values2 = new ContentValues();
        values2.put("receiverName", receiverName);
        values2.put("receiverMail", receiverMail);
        values2.put("receiverPhone", receiverPhone);
        values2.put("postTime", startTime);
        values2.put("isDelivered", isDelivered);
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
                R.string.event_updated, Toast.LENGTH_SHORT).show();
    }

    public void createPost() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent myIntent = new Intent(SingleEvent.this, AlarmReceiver.class);
        myIntent.putExtra("EVENT_ID", eventID);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(SingleEvent.this, eventID, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        if (!isTekrarli) {
            if (Calendar.getInstance().getTimeInMillis() < startTime) {
                alarmManager.set(AlarmManager.RTC, startTime, pendingIntent);
            } else {
                Toast.makeText(SingleEvent.this, getString(R.string.past_time), Toast.LENGTH_LONG).show();
            }
            isDelivered = 0;
            System.out.println("PostTime" + getDate(startTime));
        } else {
            if (Calendar.getInstance().getTimeInMillis() < retcal.getTimeInMillis()) {
                alarmManager.setInexactRepeating(AlarmManager.RTC, retcal.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
            } else {
                retcal.setTimeInMillis(retcal.getTimeInMillis() + 1000 * 60 * 60 * 24 * 7);
                alarmManager.setInexactRepeating(AlarmManager.RTC, retcal.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
            }
            startTime = retcal.getTimeInMillis();
            System.out.println("PostTime" + getDate(startTime));
        }
    }

    private String getDate(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy HH:mm", Locale.getDefault());
        return sdf.format(date);
    }

    private String getTime(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
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
            createPost();
            if (newEvent) {
                addEvent();
            } else {
                updateEvent();
            }
            System.out.println("alıcı numaralar SMS: " + receiverName);
            System.out.println("alıcı numaralar SMS: " + receiverPhone);
            System.out.println("alıcı numaralar SMS: " + receiverMail);
            newEvent = false;
            isInSingleEvent = false;
            finish();
            return true;
        } else if (id == android.R.id.home) {
            database_account.close();
            database_account2.close();
            newEvent = false;
            isInSingleEvent = false;
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Settings saved...", Toast.LENGTH_SHORT).show();
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
                eventLocation = place.getAddress().toString();
                location.setText(eventLocation);
            }
        }

        if (requestCode == FilePickerConst.REQUEST_CODE_PHOTO) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> aq = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
                if (mailIsChoosing) {
                    mailAttachment = aq.get(0);
                    if (!mailAttachment.contains("file://")) {
                        mailAttachment = "file://" + mailAttachment;
                    }
                    Picasso.with(SingleEvent.this).load(Uri.parse(mailAttachment)).error(R.drawable.ic_file).placeholder(R.drawable.ic_file)
                            .into(emailFileHolder);
                    mailIsChoosing = false;
                } else if (messengerIsChoosing) {
                    messengerAttachment = aq.get(0);
                    if (!messengerAttachment.contains("file://")) {
                        messengerAttachment = "file://" + messengerAttachment;
                    }
                    Picasso.with(SingleEvent.this).load(Uri.parse(messengerAttachment)).error(R.drawable.ic_file).placeholder(R.drawable.ic_file)
                            .into(messengerFileHolder);
                    messengerIsChoosing = false;
                } else if (whatsappIsChoosing) {
                    whatsappAttachment = aq.get(0);
                    if (!whatsappAttachment.contains("file://")) {
                        whatsappAttachment = "file://" + whatsappAttachment;
                    }
                    Picasso.with(SingleEvent.this).load(Uri.parse(whatsappAttachment)).error(R.drawable.ic_file).placeholder(R.drawable.ic_file)
                            .into(whatsappFileHolder);
                    whatsappIsChoosing = false;
                }
            }
        }
        if (requestCode == FilePickerConst.REQUEST_CODE_DOC) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> aq = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS);
                if (mailIsChoosing) {
                    mailAttachment = aq.get(0);
                    if (!mailAttachment.contains("file://")) {
                        mailAttachment = "file://" + mailAttachment;
                    }
                    Picasso.with(SingleEvent.this).load(Uri.parse(mailAttachment)).error(R.drawable.ic_file).placeholder(R.drawable.ic_file)
                            .into(emailFileHolder);
                    mailIsChoosing = false;
                } else if (messengerIsChoosing) {
                    messengerAttachment = aq.get(0);
                    if (!messengerAttachment.contains("file://")) {
                        messengerAttachment = "file://" + messengerAttachment;
                    }
                    Picasso.with(SingleEvent.this).load(Uri.parse(messengerAttachment)).error(R.drawable.ic_file).placeholder(R.drawable.ic_file)
                            .into(messengerFileHolder);
                    messengerIsChoosing = false;
                } else if (whatsappIsChoosing) {
                    whatsappAttachment = aq.get(0);
                    if (!whatsappAttachment.contains("file://")) {
                        whatsappAttachment = "file://" + whatsappAttachment;
                    }
                    Picasso.with(SingleEvent.this).load(Uri.parse(whatsappAttachment)).error(R.drawable.ic_file).placeholder(R.drawable.ic_file)
                            .into(whatsappFileHolder);
                    whatsappIsChoosing = false;
                }
            }
        }

        if (requestCode == CONTACT_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                receiverName = "";
                receiverPhone = "";
                receiverMail = "";
                List<ContactResult> results = MultiContactPicker.obtainResult(data);
                for (int i = 0; i < results.size(); i++) {
                    receiverName = receiverName + results.get(i).getDisplayName() + ";";
                    if (results.get(i).getPhoneNumbers().size() > 0) {
                        receiverPhone = receiverPhone + results.get(i).getPhoneNumbers().get(0) + ";";
                    }
                    if (results.get(i).getEmails().size() > 0) {
                        receiverMail = receiverMail + results.get(i).getEmails().get(0) + ";";
                    }
                }
                getContactInfo(receiverName);
            } else if (resultCode == RESULT_CANCELED) {
                System.out.println("User closed the picker without selecting items.");
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        database_account.close();
        database_account2.close();
        newEvent = false;
        isInSingleEvent = false;
        finish();
    }
}



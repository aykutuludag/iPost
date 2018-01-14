package app.ipost;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ProfileEditActivity extends AppCompatActivity {

    EditText editName, editMail, editLocation, editBirthday;
    RadioGroup editGender;
    RadioButton bMale, bFemale, bOther;

    SharedPreferences prefs;

    String name, email, photo, gender, birthday, location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = this.getSharedPreferences("ProfileInformation", Context.MODE_PRIVATE);
        name = prefs.getString("Name", "-");
        email = prefs.getString("Email", "-");
        photo = prefs.getString("ProfilePhoto", "");
        gender = prefs.getString("Gender", "Male");
        birthday = prefs.getString("Birthday", "-");
        location = prefs.getString("Location", "-");

        editName = findViewById(R.id.editEventName);
        editMail = findViewById(R.id.editTextDesc);
        editLocation = findViewById(R.id.editTextLocation);
        editBirthday = findViewById(R.id.editTextBirthday);
        editGender = findViewById(R.id.radioGroupGender);
        bMale = findViewById(R.id.genderMale);
        bFemale = findViewById(R.id.genderFemale);
        bOther = findViewById(R.id.genderOther);

        // Setting name
        editName.setText(name);

        // Setting email
        editMail.setText(email);

        // Setting photo
        ImageView profilePic = findViewById(R.id.event_photo);
        Picasso.with(this).load(photo).error(R.drawable.siyahprofil).placeholder(R.drawable.siyahprofil)
                .into(profilePic);

        //  Setting location and retrieving changes
        editLocation.setText(location);
        editLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(ProfileEditActivity.this);
                    startActivityForResult(intent, 1320);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        //  Setting birthday and retrieving changes
        editBirthday.setText(birthday);
        editBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int calendarYear = 1999;
                int calendarMonth = 11;
                int calendarDay = 31;

                if (!birthday.contains("-")) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/YYYY", Locale.GERMAN);
                        Date birthDateasDate = sdf.parse(birthday);
                        calendarDay = Calendar.DAY_OF_MONTH;
                        calendarMonth = Calendar.MONTH;
                        calendarYear = Calendar.YEAR;
                        Toast.makeText(ProfileEditActivity.this, birthDateasDate.toString(), Toast.LENGTH_SHORT);
                        System.out.print(calendarYear);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        System.out.println(e);
                    }
                }

                DatePickerDialog datePicker = new DatePickerDialog(ProfileEditActivity.this, AlertDialog.THEME_HOLO_DARK, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        editBirthday.setText(date);
                        prefs.edit().putString("Birthday", date).apply();
                    }
                }, calendarYear, calendarMonth, calendarDay);

                datePicker.setTitle("Choose a date");
                datePicker.setButton(DatePickerDialog.BUTTON_POSITIVE, "Set", datePicker);
                datePicker.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancel", datePicker);
                datePicker.show();

                SimpleDateFormat dateForm = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    Date convertedDate = dateForm.parse(birthday);
                    System.out.println(convertedDate);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        //  Set gender and retrieve changes
        if (gender.equals("Male")) {
            bMale.setChecked(true);
        } else if (gender.equals("Female")) {
            bFemale.setChecked(true);
        } else {
            bOther.setChecked(true);
        }
        editGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                if (checkedId == R.id.genderMale) {
                    prefs.edit().putString("Gender", "Male").apply();
                } else if (checkedId == R.id.genderFemale) {
                    prefs.edit().putString("Gender", "Female").apply();
                } else {
                    prefs.edit().putString("Gender", "Other").apply();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1320) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                editLocation.setText(place.getName().toString());
                prefs.edit().putString("Location", place.getName().toString()).apply();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i("Error", status.getStatusMessage());
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}


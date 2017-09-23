package app.isend;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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

        editName = findViewById(R.id.editTextName);
        editMail = findViewById(R.id.editTextMail);
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
        ImageView profilePic = findViewById(R.id.profile_photo);
        Picasso.with(this).load(photo).error(R.drawable.ic_error).placeholder(R.drawable.ic_placeholder)
                .into(profilePic);

        //  Setting location and retrieving changes
        editLocation.setText(location);
        editLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    prefs.edit().putString("Location", editable.toString()).apply();
                }
            }
        });


        //  Setting birthday and retrieving changes
        editBirthday.setText(birthday);
        editBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int calendarYear = 2005;
                int calendarMonth = 1;
                int calendarDay = 1;

                if (!birthday.contains("-")) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        Date birthDateasDate = sdf.parse(birthday);
                        calendarDay = Calendar.DAY_OF_MONTH;
                        calendarMonth = Calendar.MONTH;
                        calendarYear = Calendar.YEAR;
                        Toast.makeText(ProfileEditActivity.this, birthDateasDate.toString(), Toast.LENGTH_SHORT);
                        System.out.print(birthDateasDate);
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
}


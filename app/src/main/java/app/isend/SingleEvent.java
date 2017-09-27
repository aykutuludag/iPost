package app.isend;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;


public class SingleEvent extends AppCompatActivity implements ColorPickerDialogListener {

    private static final int REQUEST_EXTERNAL_STORAGE = 2;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    SQLiteDatabase database_account;
    Cursor cur;
    TextView title, description, location, timeStart, timeEnd;
    ImageView colorPicker;
    ImageView imagePicker;

    String id, eventName, eventPhotoURI, eventDescription, eventLocation, eventOwner, eventColor;
    long startTime, endTime;
    int hasMail, hasSMS, hasMessenger, hasWhatsapp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_event);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        id = getIntent().getStringExtra("EVENT_ID");

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
                        eventColor = cur.getString(i);
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
            Toast.makeText(SingleEvent.this, "There is no events", Toast.LENGTH_LONG).show();
        }

        title = findViewById(R.id.editEventName);
        title.setText(eventName);

        imagePicker = findViewById(R.id.event_photo);
        imagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.create(SingleEvent.this)
                        .single()
                        .start(1);
            }
        });

        description = findViewById(R.id.editTextDesc);
        description.setText(eventDescription);

        location = findViewById(R.id.editTextLocation);
        location.setText(eventLocation);

        timeStart = findViewById(R.id.editTextStartTime);
        timeStart.setText(getDate(startTime));

        timeEnd = findViewById(R.id.editTextEndTime);
        timeEnd.setText(getDate(endTime));

        colorPicker = findViewById(R.id.colorPicker);
        colorPicker.setBackgroundColor(Integer.parseInt(eventColor));
        colorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialog.newBuilder().setColor(Integer.parseInt(eventColor)).setDialogId(1).show(SingleEvent.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = (ArrayList<Image>) ImagePicker.getImages(data);
            eventPhotoURI = images.get(0).getPath();
            Picasso.with(this).load(eventPhotoURI).into(imagePicker);
        }
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        switch (dialogId) {
            case 1:
                Toast.makeText(SingleEvent.this, "Selected Color: #" + Integer.toHexString(color), Toast.LENGTH_SHORT).show();
                colorPicker.setBackgroundColor(color);
                break;
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

    private String getDate(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM YYYY HH:mm",
                java.util.Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC+3"));
        return sdf.format(date);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

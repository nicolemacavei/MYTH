package com.example.myth.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.myth.Event;
import com.example.myth.R;
import com.example.myth.ReminderReceiver;
import com.example.myth.utilities.CalendarUtils;
import com.google.android.material.slider.Slider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.UUID;

public class NewEventActivity extends AppCompatActivity {

    private EditText eventName, eventDetails;
    private TextView eventDate;
    private Button addEventBtn;
    private FirebaseFirestore firebaseFirestore;
    private NumberPicker eventHour, eventMinute;
    private Slider eventDuration, remindBefore;

    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        initWidgets();
        eventDate.setText("Date: " + CalendarUtils.formattedDate(CalendarUtils.selectedDate));
        eventHour.setMinValue(0);
        eventHour.setMaxValue(23);
        eventMinute.setMinValue(0);
        eventMinute.setMaxValue(59);

        addEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(eventName.getText().toString().isEmpty()){
                    eventName.setError("Name is mandatory");
                }
                else {
                    addEvent();
                    //Fragment calendarFragment = new CalendarFragment();
                    startActivity(new Intent(NewEventActivity.this, MainActivity.class));
                    finish();
                }
            }
        });
    }

    private void initWidgets() {
        eventName = findViewById(R.id.nameEventEditText);
        eventDate = findViewById(R.id.eventDateText);
        eventHour = findViewById(R.id.eventTimeHour);
        eventMinute = findViewById(R.id.eventTimeMin);
        addEventBtn = findViewById(R.id.addEventBtn);
        eventDetails = findViewById(R.id.detailsEventEditText);
        eventDuration = findViewById(R.id.durationSlideBar);
        remindBefore = findViewById(R.id.remindBeforeSlideBar);
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void addEvent(){

        int eventDurationNo = (int) eventDuration.getValue();
        int remindBeforeNo = (int) remindBefore.getValue();
        int hour = eventHour.getValue();
        int minute = eventMinute.getValue();
        String eventNameString = eventName.getText().toString();
        String eventDetailsString = eventDetails.getText().toString();
        String uniqueID = UUID.randomUUID().toString();
        String eventFormattedDate = CalendarUtils.formattedDate(CalendarUtils.selectedDate);

        Event newEvent = new Event(
                uniqueID, eventNameString, eventDetailsString, eventFormattedDate, eventDurationNo, remindBeforeNo, hour, minute);
        firebaseFirestore.collection("User").document(FirebaseAuth.getInstance().getUid())
                .collection("Date").document(CalendarUtils.selectedDate.toString()).collection("Event").document(uniqueID).set(newEvent);

        createNotificationChannel(uniqueID);
        setAlarm(hour, minute, CalendarUtils.selectedDate, remindBeforeNo);
    }

    private void setAlarm(int hour, int minute, LocalDate selectedDate, int remindBeforeNo) {

        setDateAndTimeTrigger(hour, minute, selectedDate, remindBeforeNo);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(NewEventActivity.this, ReminderReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(NewEventActivity.this, 0 , intent, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void setDateAndTimeTrigger(int hour, int minute, LocalDate selectedDate, int remindBeforeNo) {

        if(minute < remindBeforeNo){
            hour--;
            remindBeforeNo -= minute;
            minute = 60 - remindBeforeNo;
        }
        else{
            minute -= remindBeforeNo;
        }
//        int year = 2023;
//        int month = 4;
//        int day = 13;
        calendar = Calendar.getInstance(); //-> this works with current day
        //calendar.clear();
//        calendar.set(Calendar.YEAR, year);
//        calendar.set(Calendar.MONTH, month);
//        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private void createNotificationChannel(String uniqueID){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "eventReminderChannel";
            String desc = "Channel for Event Reminders";
            int imp = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("eventAlarm", name, imp);
            channel.setDescription(desc);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
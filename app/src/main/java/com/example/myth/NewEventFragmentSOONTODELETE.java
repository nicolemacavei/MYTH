package com.example.myth;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.material.slider.Slider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.UUID;

public class NewEventFragmentSOONTODELETE extends Fragment {

    private EditText eventName, eventDetails;
    private TextView eventDate;
    private Button addEventBtn;
    private FirebaseFirestore firebaseFirestore;
    private NumberPicker eventHour, eventMinute;
    private Slider eventDuration, remindBefore;

    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    public NewEventFragmentSOONTODELETE() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_new_event, container, false);
        initWidgets(rootView);
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
                    Fragment calendarFragment = new CalendarFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.body_container, calendarFragment).commit();
                }
            }
        });
        return rootView;
    }

    private void initWidgets(View rootView) {
        eventName = rootView.findViewById(R.id.nameEventEditText);
        eventDate = rootView.findViewById(R.id.eventDateText);
        eventHour = rootView.findViewById(R.id.eventTimeHour);
        eventMinute = rootView.findViewById(R.id.eventTimeMin);
        addEventBtn = rootView.findViewById(R.id.addEventBtn);
        eventDetails = rootView.findViewById(R.id.detailsEventEditText);
        eventDuration = rootView.findViewById(R.id.durationSlideBar);
        remindBefore = rootView.findViewById(R.id.remindBeforeSlideBar);
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

    }

//    private void setAlarm() {
//        calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, 8);
//        calendar.set(Calendar.MINUTE, 36);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MILLISECOND, 0);
//
//        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(MainActivity.this, ReminderReceiver.class);
//        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0 , intent, 0);
//
//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
//    }
//
//    private void createNotificationChannel(){
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            CharSequence name = "eventReminderChannel";
//            String desc = "Channel for Event Reminders";
//            int imp = NotificationManager.IMPORTANCE_HIGH;
//            NotificationChannel channel = new NotificationChannel("eventReminderId", name, imp);
//            channel.setDescription(desc);
//
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }
}
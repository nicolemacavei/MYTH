package com.example.myth.fragments;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myth.Event;
import com.example.myth.R;
import com.example.myth.ReminderReceiver;
import com.example.myth.activities.MainActivity;
import com.example.myth.findTimeSlots.TimeSlot;
import com.example.myth.utilities.CalendarUtils;
import com.example.myth.utilities.Constants;
import com.google.android.material.slider.Slider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.UUID;

public class SingleEventFragment extends Fragment {

    private EditText eventName, eventDetails;
    private TextView eventDate;
    private Button addEventBtn;
    private FirebaseFirestore firebaseFirestore;
    private NumberPicker eventStartHour, eventStartMinute, eventEndHour, eventEndMinute;
    private Slider remindBefore;

    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    public SingleEventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_single_event, container, false);
        initWidgets(rootView);

        addEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(eventName.getText().toString().isEmpty()){
                    eventName.setError("Name is mandatory");
                }
                else {
                    int startTime = eventStartHour.getValue() * 100 + eventStartMinute.getValue();
                    int endTime = eventEndHour.getValue() * 100 + eventEndMinute.getValue();
                    TimeSlot eventTimeSlot = new TimeSlot(startTime, endTime);

                    if(eventTimeSlot.startLessThanEndTime()) {
                        addEvent(eventTimeSlot);
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        getActivity().startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "start time needs to be sooner than end time", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return rootView;
    }

    private void initWidgets(View rootView) {
        eventName = rootView.findViewById(R.id.nameEventEditText);
        eventDate = rootView.findViewById(R.id.eventDateText);
        eventStartHour = rootView.findViewById(R.id.eventStartTimeHour);
        eventStartMinute = rootView.findViewById(R.id.eventStartTimeMin);
        eventEndHour = rootView.findViewById(R.id.eventEndTimeHour);
        eventEndMinute = rootView.findViewById(R.id.eventEndTimeMin);
        addEventBtn = rootView.findViewById(R.id.addEventBtn);
        eventDetails = rootView.findViewById(R.id.detailsEventEditText);
        remindBefore = rootView.findViewById(R.id.remindBeforeSlideBar);
        firebaseFirestore = FirebaseFirestore.getInstance();
        eventDate.setText("Date: " + CalendarUtils.formattedDate(CalendarUtils.selectedDate));
        eventStartHour.setMinValue(0);
        eventStartHour.setMaxValue(23);
        eventStartMinute.setMinValue(0);
        eventStartMinute.setMaxValue(59);
        eventEndHour.setMinValue(0);
        eventEndHour.setMaxValue(23);
        eventEndMinute.setMinValue(0);
        eventEndMinute.setMaxValue(59);
        eventStartHour.setValue(9);
        eventEndHour.setValue(10);
    }

    private void addEvent(TimeSlot eventTimeSlot){

        int remindBeforeNo = (int) remindBefore.getValue();
        String eventNameString = eventName.getText().toString();
        String eventDetailsString = eventDetails.getText().toString();
        String uniqueID = UUID.randomUUID().toString();
        String eventFormattedDate = CalendarUtils.formattedDate(CalendarUtils.selectedDate);

        Event newEvent = new Event(uniqueID, eventNameString, eventDetailsString, eventFormattedDate, eventTimeSlot.getEndTime(), remindBeforeNo, eventTimeSlot.getStartTime());
        firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS).document(FirebaseAuth.getInstance().getUid())
                .collection(Constants.KEY_COLLECTION_DATE).document(CalendarUtils.selectedDate.toString())
                .collection(Constants.KEY_COLLECTION_EVENT).document(uniqueID).set(newEvent);

        createNotificationChannel(uniqueID);
        setAlarm(eventTimeSlot.getStartHour(), eventTimeSlot.getStartMinute(), CalendarUtils.selectedDate, remindBeforeNo);
    }

    private void setAlarm(int hour, int minute, LocalDate selectedDate, int remindBeforeNo) {

        setDateAndTimeTrigger(hour, minute, selectedDate, remindBeforeNo);

        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity().getApplicationContext(), ReminderReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(), 0 , intent, 0);

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

            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
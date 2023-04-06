package com.example.myth;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.LocalTime;

public class EventEditCalendarFragment extends Fragment {

    private EditText eventName, eventDetails;
    private TextView eventDate, eventTime, eventDuration;
    private LocalTime time;
    private Button addEventBtn;
    private FirebaseFirestore firebaseFirestore;

    public EventEditCalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event_edit_calendar, container, false);
        initWidgets(rootView);
        time = LocalTime.now();
        eventDate.setText("Date: " + CalendarUtils.formattedDate(CalendarUtils.selectedDate));
        eventTime.setText("Time: " + CalendarUtils.formattedTime(time));

        addEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventNameString = eventName.getText().toString();
                String eventDetailsString = eventDetails.getText().toString();
                String  eventDurationNo = eventDuration.getText().toString();

                if(eventNameString.isEmpty()){
                    eventName.setError("Name is mandatory");
                }
                else {
                    Fragment calendarFragment = null;
                    addEvent(eventNameString, eventDetailsString, eventDurationNo);
                    calendarFragment = new CalendarFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.body_container, calendarFragment).commit();
                }
            }
        });
        return rootView;
    }

    private void initWidgets(View rootView) {
        eventName = rootView.findViewById(R.id.nameEventEditText);
        eventDate = rootView.findViewById(R.id.eventDateText);
        eventTime = rootView.findViewById(R.id.eventTimeText);
        addEventBtn = rootView.findViewById(R.id.addEventBtn);
        eventDetails = rootView.findViewById(R.id.detailsEventEditText);
        eventDuration = rootView.findViewById(R.id.durationEventText);
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void addEvent(String eventNameString, String eventDetailsString, String eventDurationNo){

        String eventFormattedDate = CalendarUtils.formattedDate(CalendarUtils.selectedDate);
        Event newEvent = new Event(
                eventNameString, eventDetailsString, eventFormattedDate, time, eventDurationNo);
        firebaseFirestore.collection("User").document(FirebaseAuth.getInstance().getUid())
                .collection("Date").document(CalendarUtils.selectedDate.toString()).set(newEvent);

    }
}
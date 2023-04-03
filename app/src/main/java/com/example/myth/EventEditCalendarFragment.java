package com.example.myth;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.LocalTime;

public class EventEditCalendarFragment extends Fragment {

    private EditText eventName;
    private TextView eventDate, eventTime;
    private LocalTime time;
    private Button addEventBtn;

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
        eventDate.setText("Time: " + CalendarUtils.formattedTime(time));

        addEventBtn.setOnClickListener(new View.OnClickListener() {
            Fragment calendarFragment = null;
            @Override
            public void onClick(View v) {
                String eventNameString = eventName.getText().toString();
                Event newEvent = new Event(eventNameString, CalendarUtils.selectedDate, time);
                Event.eventsList.add(newEvent);
                calendarFragment = new CalendarFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.body_container, calendarFragment).commit();
            }
        });
        return rootView;
    }

    private void initWidgets(View rootView) {
        eventName = rootView.findViewById(R.id.nameEventEditText);
        eventDate = rootView.findViewById(R.id.eventDateText);
        eventTime = rootView.findViewById(R.id.eventTimeText);
        addEventBtn = rootView.findViewById(R.id.addEventBtn);
    }
}
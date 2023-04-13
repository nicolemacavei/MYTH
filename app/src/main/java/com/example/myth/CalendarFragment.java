package com.example.myth;

import static com.example.myth.CalendarUtils.daysInMonthArray;
import static com.example.myth.CalendarUtils.monthYearFromDate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarFragment extends Fragment implements CalendarAdapter.OnItemListener{

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private Button previousMonthBtn, nextMonthBtn, addEventBtn;
    private static ListView eventListView;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String userId = auth.getCurrentUser().getUid();
    private static ArrayList<Event> events = new ArrayList<>();

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);
        initWidgets(rootView);
        CalendarUtils.selectedDate = LocalDate.now();
        setMonthView();

        addEventBtn.setOnClickListener(new View.OnClickListener() {
            Fragment newEventFragment = null;
            @Override
            public void onClick(View v) {
                newEventFragment = new NewEventFragmentSOONTODELETE();
                //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.body_container, newEventFragment).commit();

                Intent intent = new Intent(getActivity(), NewEventActivity.class);
                getActivity().startActivity(intent);
            }
        });
        nextMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
                setMonthView();
            }
        });
        previousMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
                setMonthView();
            }
        });
        eventListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                new AlertDialog.Builder(getActivity())
                        .setTitle("Delete event")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeEvent(position);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

                return true;
            }
        });

        return rootView;
    }

    private void initWidgets(View rootView) {
        calendarRecyclerView = rootView.findViewById(R.id.calendarRecyclerView);
        monthYearText = rootView.findViewById(R.id.monthYear);
        previousMonthBtn = (Button) rootView.findViewById(R.id.previousMonthBtn);
        nextMonthBtn = (Button) rootView.findViewById(R.id.nextMonthBtn);
        addEventBtn = (Button) rootView.findViewById(R.id.addItemCalendarBtn);
        eventListView = rootView.findViewById(R.id.eventListView);
    }

    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> daysInMonth = daysInMonthArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
        setEventAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        setEventAdapter();
    }

    private void setEventAdapter() {
        String dateFormatted = CalendarUtils.formattedDate(CalendarUtils.selectedDate);
        //LocalTime time = LocalTime.now();

        Task<QuerySnapshot> eventsList = firebaseFirestore.collection("User").document(userId)
                .collection("Date").document(CalendarUtils.selectedDate.toString()).collection("Event").get();

        eventsList.addOnSuccessListener(task -> {

            events.removeAll(events);
            for(QueryDocumentSnapshot queryDocSn : task){

                String eventId = queryDocSn.getId();
                String name = queryDocSn.getString("name");
                String details = queryDocSn.getString("details");
                int duration = queryDocSn.getLong("duration").intValue();
                int remindBefore = queryDocSn.getLong("remind").intValue();
                int hour = queryDocSn.getLong("hour").intValue();
                int minute = queryDocSn.getLong("minute").intValue();

                Event event = new Event(eventId, name, details, dateFormatted, duration, remindBefore, hour, minute);
                events.add(event);
            }
            EventAdapter eventAdapter = new EventAdapter(getActivity(), events);
            eventListView.setAdapter(eventAdapter);
        });
    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        if(date != null) {
            CalendarUtils.selectedDate = date;
            setMonthView();
        }
    }

    private void removeEvent(int position) {
        System.out.println("POSITION IS: " + position);
        firebaseFirestore.collection("User").document(userId)
                .collection("Date").document(CalendarUtils.selectedDate.toString())
                .collection("Event").document(events.get(position).getEventId())
                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            events.remove(events.get(position));
                            EventAdapter eventAdapter = new EventAdapter(getActivity(), events);
                            eventListView.setAdapter(eventAdapter);
                        }
                        else {
                            Toast.makeText(getActivity(), "Error " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
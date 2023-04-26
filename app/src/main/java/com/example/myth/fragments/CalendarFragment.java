package com.example.myth.fragments;

import static com.example.myth.utilities.CalendarUtils.daysInMonthArray;
import static com.example.myth.utilities.CalendarUtils.monthYearFromDate;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.myth.Event;
import com.example.myth.R;
import com.example.myth.activities.NewEventActivity;
import com.example.myth.adapters.CalendarAdapter;
import com.example.myth.adapters.EventAdapter;
import com.example.myth.utilities.CalendarUtils;
import com.example.myth.utilities.Constants;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarFragment extends Fragment implements CalendarAdapter.OnItemListener{

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView, eventRecyclerView;
    private Button previousMonthBtn;
    private Button nextMonthBtn;
    private FloatingActionButton addEventBtn;
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
            @Override
            public void onClick(View v) {
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
//        eventRecyclerView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//
//                new AlertDialog.Builder(getActivity())
//                        .setTitle("Delete event")
//                        .setMessage("Are you sure?")
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                removeEvent(position);
//                            }
//                        })
//                        .setNegativeButton("No", null)
//                        .show();
//
//                return true;
//            }
//        });

        return rootView;
    }

    private void initWidgets(View rootView) {
        calendarRecyclerView = rootView.findViewById(R.id.calendarRecyclerView);
        monthYearText = rootView.findViewById(R.id.monthYear);
        previousMonthBtn = (Button) rootView.findViewById(R.id.previousMonthBtn);
        nextMonthBtn = (Button) rootView.findViewById(R.id.nextMonthBtn);
        addEventBtn = (FloatingActionButton) rootView.findViewById(R.id.addItemCalendarBtn);
        eventRecyclerView = rootView.findViewById(R.id.eventRecyclerView);
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

        Task<QuerySnapshot> eventsList = firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS).document(userId)
                .collection(Constants.KEY_COLLECTION_DATE).document(CalendarUtils.selectedDate.toString())
                .collection(Constants.KEY_COLLECTION_EVENT)
                .orderBy(Constants.KEY_EVENT_HOUR).get();

        eventsList.addOnSuccessListener(task -> {

            events.removeAll(events);
            for(QueryDocumentSnapshot queryDocSn : task){

                String eventId = queryDocSn.getId();
                String name = queryDocSn.getString(Constants.KEY_EVENT_NAME);
                String details = queryDocSn.getString(Constants.KEY_EVENT_DETAILS);
                int duration = queryDocSn.getLong(Constants.KEY_EVENT_DURATION).intValue();
                int remindBefore = queryDocSn.getLong(Constants.KEY_EVENT_REMIND).intValue();
                int hour = queryDocSn.getLong(Constants.KEY_EVENT_HOUR).intValue();
                int minute = queryDocSn.getLong(Constants.KEY_EVENT_MINUTE).intValue();

                Event event = new Event(eventId, name, details, dateFormatted, duration, remindBefore, hour, minute);
                events.add(event);
            }
            if(events.size() > 0) {
                EventAdapter eventAdapter = new EventAdapter(getActivity(), events);
                eventRecyclerView.setAdapter(eventAdapter);
                eventRecyclerView.setVisibility(View.VISIBLE);
            } else
                eventRecyclerView.setVisibility(View.GONE);
        });
    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        if(date != null) {
            CalendarUtils.selectedDate = date;
            setMonthView();
        }
    }

//    private void removeEvent(int position) {
//        System.out.println("POSITION IS: " + position);
//        firebaseFirestore.collection("User").document(userId)
//                .collection("Date").document(CalendarUtils.selectedDate.toString())
//                .collection("Event").document(events.get(position).getEventId())
//                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isSuccessful()){
//                            events.remove(events.get(position));
//                            EventAdapter eventAdapter = new EventAdapter(getActivity(), events);
//                            eventRecyclerView.setAdapter(eventAdapter);
//                        }
//                        else {
//                            Toast.makeText(getActivity(), "Error " + task.getException(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//
//    }
}
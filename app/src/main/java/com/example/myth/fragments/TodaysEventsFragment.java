package com.example.myth.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myth.Event;
import com.example.myth.R;
import com.example.myth.adapters.EventAdapter;
import com.example.myth.utilities.CalendarUtils;
import com.example.myth.utilities.Constants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.util.ArrayList;

public class TodaysEventsFragment extends Fragment {

    private RecyclerView eventsRecyclerView;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;
    private String userId;
    private ArrayList<Event> events = new ArrayList<>();

    public TodaysEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_events_list, container, false);
        initWidgets(rootView);
        setEventAdapter();

        return rootView;
    }

    private void initWidgets(View rootView) {
        eventsRecyclerView = rootView.findViewById(R.id.eventsRecyclerView);
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();
        CalendarUtils.selectedDate = LocalDate.now();
    }

    private void setEventAdapter(){

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

                Event event = new Event(eventId, name, details, null, duration, remindBefore, hour, minute);
                events.add(event);
            }
            System.out.println("TEST: " + events.size());
            if(events.size() > 0) {
                EventAdapter eventAdapter = new EventAdapter(getActivity(), events);
                eventsRecyclerView.setAdapter(eventAdapter);
                eventsRecyclerView.setVisibility(View.VISIBLE);
            } else
                eventsRecyclerView.setVisibility(View.GONE);
        });
    }
}
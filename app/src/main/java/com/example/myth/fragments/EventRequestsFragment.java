package com.example.myth.fragments;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myth.Event;
import com.example.myth.Notification;
import com.example.myth.R;
import com.example.myth.User;
import com.example.myth.adapters.EventRequestAdapter;
import com.example.myth.adapters.NotificationAdapter;
import com.example.myth.utilities.Constants;
import com.example.myth.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventRequestsFragment extends Fragment {

    private PreferenceManager preferenceManager;
    private RecyclerView eventNotificationRecyclerView;

    public EventRequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event_requests, container, false);
        initWidgets(rootView);
        getEventNotifications();

        return rootView;
    }

    private void initWidgets(View rootView) {
        preferenceManager = new PreferenceManager(getActivity().getApplicationContext());
        eventNotificationRecyclerView = rootView.findViewById(R.id.eventNotificationRecyclerView);
    }

    private void getEventNotifications(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);

        database.collection(Constants.KEY_COLLECTION_NOTIFICATION).document(currentUserId)
                .collection(Constants.KEY_COLLECTION_EVENT).orderBy(Constants.KEY_EVENT_DATE).get()
                .addOnCompleteListener(task -> {

                    List<Event> events = new ArrayList<>();
                    String userName = "UserName";
                    if(task.isSuccessful() && task.getResult() != null){

                        for(QueryDocumentSnapshot document : task.getResult()){
//
                            userName = document.getString(Constants.KEY_NAME);
                            String eventDate = document.getString(Constants.KEY_EVENT_DATE);
                            int eventTime = document.getLong(Constants.KEY_TIME).intValue();
                            int eventDuration = document.getLong(Constants.KEY_EVENT_DURATION).intValue();
                            String eventName = document.getString(Constants.KEY_EVENT_NAME);

                            Event event = new Event(
                                    eventName,
                                    null,
                                    eventDate,
                                    eventDuration,
                                    0,
                                    eventTime
                            );
                            events.add(event);
                        }
                    }
                    if(events.size() > 0 ){
                        EventRequestAdapter eventRequestAdapter = new EventRequestAdapter(events, userName);
                        eventNotificationRecyclerView.setAdapter(eventRequestAdapter);
                        eventNotificationRecyclerView.setVisibility(View.VISIBLE);
                    }
                });
    }
}
package com.example.myth.fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myth.R;
import com.example.myth.User;
import com.example.myth.adapters.TimeSlotAdapter;
import com.example.myth.findTimeSlots.TimeSlot;
import com.example.myth.interfaces.RecyclerViewInterface;
import com.example.myth.utilities.Constants;
import com.example.myth.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.slider.Slider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

public class MutualEventFragment extends Fragment implements ShowConnectionsFragment.OnInputSelected, RecyclerViewInterface {

    private RecyclerView timeSlotsRecyclerView;
    private EditText eventName, details;
    private Button chooseConnectionBtn, suggestTimingBtn;
    private Slider eventDuration;
    FirebaseFirestore firebaseFirestore;
    TextView userSelectedTextView, minimalDurationText, suggestText, suggestedDate, selectMeetingText;
    private User selectedUser;
    private ArrayList<TimeSlot> availableHours = new ArrayList<>();
    private PreferenceManager preferenceManager;
    private LocalDate dateFound;

    public MutualEventFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_mutual_event, container, false);
        initWidgets(rootView);

        chooseConnectionBtn.setOnClickListener(new View.OnClickListener() {
            final FragmentManager fragmentManager = getFragmentManager();
            final ShowConnectionsFragment connectionsFragment = new ShowConnectionsFragment();
            @Override
            public void onClick(View v) {
                connectionsFragment.setTargetFragment(MutualEventFragment.this, 1);
                connectionsFragment.show(fragmentManager, "TV_tag");
            }
        });
        suggestTimingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int duration = (int) eventDuration.getValue();
                if(selectedUser != null) {
                    dateFound = LocalDate.now();
                    getHours(preferenceManager.getString(Constants.KEY_USER_ID), selectedUser.getUserId(), duration);

                } else {
                    Toast.makeText(getActivity(), "Please select a connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    private void initWidgets(View rootView) {
        eventName = rootView.findViewById(R.id.nameEventEditText);
        details = rootView.findViewById(R.id.detailsEventEditText);
        eventDuration = rootView.findViewById(R.id.durationSlideBar);
        minimalDurationText = rootView.findViewById(R.id.minimalDurationText);
        chooseConnectionBtn = rootView.findViewById(R.id.chooseConnectionBtn);
        suggestTimingBtn = rootView.findViewById(R.id.suggestTimingBtn);
        firebaseFirestore = FirebaseFirestore.getInstance();
        userSelectedTextView = rootView.findViewById(R.id.userSelectedText);
        preferenceManager = new PreferenceManager(getActivity().getApplicationContext());
        suggestText = rootView.findViewById(R.id.suggestionMeetingText);
        suggestedDate = rootView.findViewById(R.id.suggestedDateText);
        timeSlotsRecyclerView = rootView.findViewById(R.id.timeSlotsRecyclerView);
        selectMeetingText = rootView.findViewById(R.id.selectHoursText);
    }

    @Override
    public void sendInput(User userSelected) {
        Log.e(TAG, "sendInput: " + userSelected.getFullName());
        chooseConnectionBtn.setVisibility(View.GONE);
        selectedUser = userSelected;
        userSelectedTextView.setText(userSelected.getFullName());
        userSelectedTextView.setVisibility(View.VISIBLE);
    }

    public void getHours(String userIdOne, String userIdTwo, int minDuration){

        ArrayList<TimeSlot> busyHours = new ArrayList<>();

        firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS).document(userIdOne)
                .collection(Constants.KEY_COLLECTION_DATE).document(dateFound.toString())
                .collection(Constants.KEY_COLLECTION_EVENT)
                .orderBy(Constants.KEY_TIME).get()
                .addOnSuccessListener(task -> {
                    for(QueryDocumentSnapshot queryDocSn : task){

                        int duration = queryDocSn.getLong(Constants.KEY_EVENT_DURATION).intValue();
                        int startTime = queryDocSn.getLong(Constants.KEY_TIME).intValue();
                        int endTime = startTime +  100 * (duration / 60) + duration % 60;

                        TimeSlot timeSlot = new TimeSlot(startTime, endTime);
                        busyHours.add(timeSlot);
                    }
                    firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS).document(userIdTwo)
                            .collection(Constants.KEY_COLLECTION_DATE).document(dateFound.toString())
                            .collection(Constants.KEY_COLLECTION_EVENT)
                            .orderBy(Constants.KEY_TIME).get()
                            .addOnSuccessListener(task2 -> {

                                for(QueryDocumentSnapshot queryDocSn : task2){

                                    int duration = queryDocSn.getLong(Constants.KEY_EVENT_DURATION).intValue();
                                    int startTime = queryDocSn.getLong(Constants.KEY_TIME).intValue();
                                    int endTime = startTime + 100 * (duration / 60) + duration % 60;

                                    TimeSlot timeSlot = new TimeSlot(startTime, endTime);
                                    busyHours.add(timeSlot);
                                }

                                Collections.sort(busyHours, new TimeSlot.CompareByStartTime());
                                ArrayList<TimeSlot> availableHours = new ArrayList<>();
                                availableHours = findAvailableHours(minDuration, busyHours);

                                if(availableHours.size() > 0) {
                                    showAvailableHours(availableHours, dateFound);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            });
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public ArrayList<TimeSlot> findAvailableHours(int minDuration, ArrayList<TimeSlot> busyHours){
        int startHour, endHour;

        //the minimum time of the free Time Slot of the users.
        int timeMeeting = 100 * (minDuration / 60) + minDuration % 60;
        int minStart = 900;   // 9 a.m.
        int maxEnd = 2100;   // 9 p.m.

        for(TimeSlot timeSlot : busyHours){
            startHour = timeSlot.getStartTime();
            endHour = timeSlot.getEndTime();

            if(startHour >= minStart + timeMeeting){
                TimeSlot newTimeSlot = new TimeSlot(minStart, startHour);
                availableHours.add(newTimeSlot);
                minStart = endHour;
            } else if (minStart < endHour) {
                minStart = endHour;
            }

        }
        if(minStart <= maxEnd - timeMeeting){
            TimeSlot newTimeSlot = new TimeSlot(minStart, maxEnd);
            availableHours.add(newTimeSlot);
        }

        return availableHours;
    }

    private void showAvailableHours(ArrayList<TimeSlot> availableHours, LocalDate date) {
        minimalDurationText.setVisibility(View.GONE);
        eventDuration.setVisibility(View.GONE);
        suggestTimingBtn.setVisibility(View.GONE);

        suggestedDate.setText(date.toString());
        suggestedDate.setVisibility(View.VISIBLE);
        suggestText.setVisibility(View.VISIBLE);
        selectMeetingText.setVisibility(View.VISIBLE);

        TimeSlotAdapter timeSlotAdapter = new TimeSlotAdapter(availableHours, this);
        timeSlotsRecyclerView.setAdapter(timeSlotAdapter);
        timeSlotsRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(int position) {
        TimeSlot timeSlotSelected = availableHours.get(position);
        String timeSlotString = timeSlotSelected.toString();
        Bundle result = new Bundle();
        result.putString("timeSlot", timeSlotString);
        result.putString("userName", selectedUser.getFullName());
        result.putParcelable("user", selectedUser);
        result.putString("date", dateFound.toString());
        result.putString("userId", selectedUser.getUserId());
        result.putInt("startTime", timeSlotSelected.getStartTime());
        result.putInt("duration", timeSlotSelected.getStartTime() - timeSlotSelected.getEndTime());
        getParentFragmentManager().setFragmentResult("dataFromMutualEvent", result);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.top_event_container, new MutualEventRequestFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
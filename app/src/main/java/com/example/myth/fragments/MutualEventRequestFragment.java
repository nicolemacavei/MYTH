package com.example.myth.fragments;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myth.Event;
import com.example.myth.Notification;
import com.example.myth.R;
import com.example.myth.User;
import com.example.myth.activities.MainActivity;
import com.example.myth.findTimeSlots.TimeSlot;
import com.example.myth.firebase.FCMSend;
import com.example.myth.utilities.Constants;
import com.example.myth.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import java.time.LocalDate;
import java.util.UUID;

public class MutualEventRequestFragment extends Fragment {

    private TextView userSelectedText, suggestedDateText, suggestedTimeText;
    private Button sendRequestBtn;
    private EditText eventName, eventDetails;
    private FirebaseFirestore firebaseFirestore;
    private PreferenceManager preferenceManager;
    private NumberPicker eventStartHour, eventStartMinute, eventEndHour, eventEndMinute;
    private User user;
    private TimeSlot timeSlot;
    private String date;

    public MutualEventRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_mutual_event_request, container, false);
        initWidgets(rootView);

        sendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!eventName.getText().toString().trim().isEmpty()){
                    int startTime = eventStartHour.getValue() * 100 + eventStartMinute.getValue();
                    int endTime = eventEndHour.getValue() * 100 + eventEndMinute.getValue();
                    TimeSlot eventTimeSlot = new TimeSlot(startTime, endTime);

                    if(eventTimeSlot.startLessThanEndTime()) {
                        addNotification(eventTimeSlot);

                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        getActivity().startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "start time needs to be sooner than end time", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    eventName.setError("Name is mandatory");
                }
            }
        });

        return rootView;
    }

    private void addNotification(TimeSlot eventTimeSlot) {

        String uniqueID = UUID.randomUUID().toString();
        Event newEvent = new Event(uniqueID, eventName.getText().toString(), eventDetails.getText().toString(),
                date, eventTimeSlot.getEndTime(), 15, eventTimeSlot.getStartTime());

            firebaseFirestore.collection(Constants.KEY_COLLECTION_NOTIFICATION).document(user.getUserId())
                    .collection(Constants.KEY_COLLECTION_EVENT).document(uniqueID).set(newEvent);
            firebaseFirestore.collection(Constants.KEY_COLLECTION_NOTIFICATION).document(user.getUserId())
                    .collection(Constants.KEY_COLLECTION_EVENT).document(uniqueID)
                    .update(Constants.KEY_NAME, preferenceManager.getString(Constants.KEY_NAME),
                            Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID));

            FCMSend.pushNotification(
                    getContext(),
                    user.token,
                    "Event Request from " + preferenceManager.getString(Constants.KEY_NAME),
                    "on " + date
            );
            Toast.makeText(getActivity(), "request sent", Toast.LENGTH_SHORT).show();
    }

    private void initWidgets(View rootView) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getActivity().getApplicationContext());
        suggestedDateText = rootView.findViewById(R.id.suggestedDateText);
        suggestedTimeText = rootView.findViewById(R.id.suggestedTimeText);
        eventName = rootView.findViewById(R.id.nameEventEditText);
        eventDetails = rootView.findViewById(R.id.detailsEventEditText);
        userSelectedText = rootView.findViewById(R.id.userSelectedText);
        sendRequestBtn = rootView.findViewById(R.id.sendRequestBtn);
        eventStartHour = rootView.findViewById(R.id.eventStartTimeHour);
        eventStartMinute = rootView.findViewById(R.id.eventStartTimeMin);
        eventEndHour = rootView.findViewById(R.id.eventEndTimeHour);
        eventEndMinute = rootView.findViewById(R.id.eventEndTimeMin);

        getFragmentManager().setFragmentResultListener("dataFromMutualEvent", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                date = result.getString("date");
                user = result.getParcelable("user");
                timeSlot = result.getParcelable("timeSlot");
                suggestedTimeText.setText(timeSlot.toString());
                suggestedDateText.setText(date);
                userSelectedText.setText(user.getFullName());

                eventStartHour.setMinValue(timeSlot.getStartHour());
                eventStartHour.setMaxValue(timeSlot.getEndHour());
                eventStartMinute.setMinValue(0);
                eventStartMinute.setMaxValue(59);

                eventEndHour.setMinValue(timeSlot.getStartHour());
                eventEndHour.setMaxValue(timeSlot.getEndHour());
                eventEndHour.setValue(timeSlot.getEndHour());
                eventEndMinute.setMinValue(0);
                eventEndMinute.setMaxValue(59);
            }
        });
    }
}
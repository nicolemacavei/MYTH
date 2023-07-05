package com.example.myth.fragments;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.nfc.Tag;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.myth.Event;
import com.example.myth.R;
import com.example.myth.interfaces.RecyclerViewInterface;
import com.example.myth.utilities.CalendarUtils;
import com.example.myth.utilities.Constants;
import com.example.myth.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.slider.Slider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ShowEventFragment extends DialogFragment{

    private PreferenceManager preferenceManager;
    private TextView eventName, eventDetails, eventType, eventDate;
    private NumberPicker eventStartHour;
    private NumberPicker eventStartMinute;
    private NumberPicker eventEndHour;
    private NumberPicker eventEndMinute;
    private Slider eventAlertSlider;
    private Button addChangesBtn;
    private Bundle eventClicked;
    private String eventIdStr, eventNameStr, eventDateStr, eventDetailsStr;
    private FirebaseAuth auth;
    private int eventEndDate, eventStartDate;

    public ShowEventFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pop_up_event, container, false);
        initWidgets(rootView);
        setEventDetails();
        this.getDialog().setTitle("Dialog ");

        addChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addChanges();
                dismiss();
            }
        });

        return rootView;
    }

    private void addChanges() {

        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection(Constants.KEY_COLLECTION_USERS).document(auth.getCurrentUser().getUid())
                .collection(Constants.KEY_COLLECTION_DATE).document(CalendarUtils.selectedDate.toString())
                .collection(Constants.KEY_COLLECTION_EVENT).document(eventIdStr);
//
//        Map<String, String> updates = new HashMap<>();
//        updates.put(Constants.KEY_EVENT_NAME, String.valueOf(eventName.getText()));

        docRef.update(Constants.KEY_EVENT_NAME, String.valueOf(eventName.getText()))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "Document successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error updating document", e);
                    }
                });

    }

    private void setEventDetails() {
        eventIdStr = eventClicked.getString("eventId");
        eventNameStr = eventClicked.getString("eventName");
        eventDateStr = eventClicked.getString("eventDate");
        eventDetailsStr = eventClicked.getString("eventDetails");
        eventEndDate = eventClicked.getInt("eventEndTime");
        eventStartDate = eventClicked.getInt("eventStartTime");

        Event event = new Event(eventIdStr, eventNameStr, eventDetailsStr, eventDateStr, eventEndDate, eventEndDate, eventStartDate);

        eventName.setText(eventNameStr);
        eventDate.setText(eventDateStr);
        if(!eventDetailsStr.isEmpty()) {
            eventDetails.setText(eventDetailsStr);
            eventDetails.setVisibility(View.VISIBLE);
        }
        eventStartHour.setValue(event.getStartHour());
        eventStartMinute.setValue(event.getStartMinute());
        eventEndHour.setValue(event.getEndHour());
        eventEndMinute.setValue(event.getEndMinute());
        Log.e(TAG, String.valueOf(event.getStartHour()));
    }

    private void initWidgets(View rootView) {
        preferenceManager = new PreferenceManager(getActivity().getApplicationContext());
        eventName = rootView.findViewById(R.id.eventName);
        eventType = rootView.findViewById(R.id.eventTypeText);
        eventDate = rootView.findViewById(R.id.eventDateText);
        eventStartHour = rootView.findViewById(R.id.eventStartTimeHour);
        eventStartMinute = rootView.findViewById(R.id.eventStartTimeMin);
        eventEndHour = rootView.findViewById(R.id.eventEndTimeHour);
        eventEndMinute = rootView.findViewById(R.id.eventEndTimeMin);
        eventAlertSlider = rootView.findViewById(R.id.remindBeforeSlideBar);
        eventDetails = rootView.findViewById(R.id.eventDetails);
        addChangesBtn = rootView.findViewById(R.id.addChangesBtn);
        eventClicked = getArguments();
        auth = FirebaseAuth.getInstance();

        eventStartHour.setMinValue(0);
        eventStartHour.setMaxValue(23);
        eventStartMinute.setMinValue(0);
        eventStartMinute.setMaxValue(59);
        eventEndHour.setMinValue(0);
        eventEndHour.setMaxValue(23);
        eventEndMinute.setMinValue(0);
        eventEndMinute.setMaxValue(59);
    }

}
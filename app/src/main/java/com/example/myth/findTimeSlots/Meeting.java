package com.example.myth.findTimeSlots;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.example.myth.utilities.Constants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.util.ArrayList;

public class Meeting {

    private String userIdOne, userIdTwo;
    private int duration;
    private ArrayList<TimeSlot> busyHours = new ArrayList<>();
    private LocalDate date = LocalDate.now();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public Meeting(String  userIdOne, String userIdTwo, int duration) {
        this.userIdOne = userIdOne;
        this.userIdTwo = userIdTwo;
        this.duration = duration;
    }

    public void getBusyHours(){
        firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS).document(userIdOne)
                .collection(Constants.KEY_COLLECTION_DATE).document(date.toString())
                .collection(Constants.KEY_COLLECTION_EVENT)
                .orderBy(Constants.KEY_TIME).get()
                .addOnSuccessListener(task -> {
                        for(QueryDocumentSnapshot queryDocSn : task){

                            int duration = queryDocSn.getLong(Constants.KEY_EVENT_DURATION).intValue();
                            int startTime = queryDocSn.getLong(Constants.KEY_TIME).intValue();
                            int endTime = startTime + ( 100 * duration / 60 + duration % 60);
                            Log.e(TAG, "TEST STRAT AND END TIME: \n" + startTime + " " + endTime);

                            TimeSlot timeSlot = new TimeSlot(startTime, endTime);
                            busyHours.add(timeSlot);
                        }

                        Log.e(TAG, "TEST AVAILABLE HOURS: \n" + findAvailableHours());
                });
//        firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS).document(userIdTwo)
//                .collection(Constants.KEY_COLLECTION_DATE).document(date.toString())
//                .collection(Constants.KEY_COLLECTION_EVENT)
//                .orderBy(Constants.KEY_TIME).get()
//                .addOnSuccessListener(task -> {
//
//                    for(QueryDocumentSnapshot queryDocSn : task){
//
//                        int duration = queryDocSn.getLong(Constants.KEY_EVENT_DURATION).intValue();
//                        int startTime = queryDocSn.getLong(Constants.KEY_TIME).intValue();
//                        int endTime = startTime + ( 100 * duration / 60 + duration % 40);
//
//                        TimeSlot timeSlot = new TimeSlot(startTime, endTime);
//                        busyHours.add(timeSlot);
//                    }
//                });
    }

    public ArrayList<TimeSlot> findAvailableHours(){
        ArrayList<TimeSlot> availableHours = new ArrayList<>();
        int startHour, endHour;

        //the minimum time of the free Time Slot of the users.
        int timeMeeting = 100 * duration/60 + duration%60;
        int minStart = 900;   // 9 a.m.
        int maxEnd = 2100;   // 9 p.m.

        for(TimeSlot timeSlot : busyHours){
            startHour = timeSlot.getStartTime();
            endHour = timeSlot.getEndTime();

            if(startHour >= minStart + timeMeeting){
                TimeSlot newTimeSlot = new TimeSlot(minStart, startHour);
                availableHours.add(newTimeSlot);
                minStart = endHour;
                Log.e(TAG, "TEST FIRST SLOT: " + newTimeSlot.getStartTime() + " " + newTimeSlot.getEndTime());
            } else if (minStart < endHour) {
                minStart = endHour;
            }

        }
        if(minStart <= maxEnd - timeMeeting){
            TimeSlot newTimeSlot = new TimeSlot(minStart, maxEnd);
            availableHours.add(newTimeSlot);
            Log.e(TAG, "TEST FIRST SLOT: " + newTimeSlot.getStartTime() + " " + newTimeSlot.getEndTime());
        }

        return availableHours;
    }
}

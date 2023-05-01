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
import java.util.Collections;
import java.util.Comparator;

public class Meeting {

    private String userIdOne, userIdTwo;
    private int durationOfMeeting;
    private ArrayList<TimeSlot> busyHours = new ArrayList<>();
    private ArrayList<TimeSlot> availableHours = new ArrayList<>();
    private LocalDate date;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public Meeting(String  userIdOne, String userIdTwo, int durationOfMeeting, LocalDate date) {
        this.userIdOne = userIdOne;
        this.userIdTwo = userIdTwo;
        this.durationOfMeeting = durationOfMeeting;
        this.date = date;
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
                            int endTime = startTime +  100 * (duration / 60) + duration % 60;

                            //Log.e(TAG, "TEST User1: \n" + startTime + " " + endTime);
                            TimeSlot timeSlot = new TimeSlot(startTime, endTime);
                            busyHours.add(timeSlot);
                        }
                    firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS).document(userIdTwo)
                            .collection(Constants.KEY_COLLECTION_DATE).document(date.toString())
                            .collection(Constants.KEY_COLLECTION_EVENT)
                            .orderBy(Constants.KEY_TIME).get()
                            .addOnSuccessListener(task2 -> {

                                for(QueryDocumentSnapshot queryDocSn : task2){

                                    int duration = queryDocSn.getLong(Constants.KEY_EVENT_DURATION).intValue();
                                    int startTime = queryDocSn.getLong(Constants.KEY_TIME).intValue();
                                    int endTime = startTime + 100 * (duration / 60) + duration % 60;
                                    //Log.e(TAG, "TEST User2: \n" + startTime + " " + endTime);

                                    TimeSlot timeSlot = new TimeSlot(startTime, endTime);
                                    busyHours.add(timeSlot);
                                }

                                Collections.sort(busyHours, new TimeSlot.CompareByStartTime());
                                findAvailableHours();
                            });
                });
    }

    public ArrayList<TimeSlot> findAvailableHours(){
        availableHours.clear();
        int startHour, endHour;

        //the minimum time of the free Time Slot of the users.
        int timeMeeting = 100 * (durationOfMeeting / 60) + durationOfMeeting%60;
        int minStart = 900;   // 9 a.m.
        int maxEnd = 2100;   // 9 p.m.

        for(TimeSlot timeSlot : busyHours){
            startHour = timeSlot.getStartTime();
            endHour = timeSlot.getEndTime();

            if(startHour >= minStart + timeMeeting){
                TimeSlot newTimeSlot = new TimeSlot(minStart, startHour);
                availableHours.add(newTimeSlot);
                minStart = endHour;
                Log.e(TAG, "TEST Time SLOT: " + newTimeSlot.getStartTime() + " " + newTimeSlot.getEndTime());
            } else if (minStart < endHour) {
                minStart = endHour;
            }

        }
        if(minStart <= maxEnd - timeMeeting){
            TimeSlot newTimeSlot = new TimeSlot(minStart, maxEnd);
            availableHours.add(newTimeSlot);
            Log.e(TAG, "TEST TIME SLOT: " + newTimeSlot.getStartTime() + " " + newTimeSlot.getEndTime());
        }

        return availableHours;
    }
}

package com.example.myth;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalTime;

public class Event {
    private String name, details, date, eventId;
    private int startTime, endTime, remind;

    public Event(String eventId, String name, String details, String date, int endTime, int remind, int startTime) {
        this.eventId = eventId;
        this.name = name;
        this.details = details;
        this.date = date;
        this.endTime = endTime;
        this.remind = remind;
        this.startTime = startTime;
    }

    public int getRemind() {
        return remind;
    }

    public String getEventId() {
        return eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStartTime() {
        return startTime;
    }

    public String getDetails() {
        return details;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getEndTime() {
        return endTime;
    }

    public String durationString() {
        int duration = endTime - startTime;
        int hours = duration / 100;
        int minutes = 60 - duration % 100;
        String hourString, minuteString;

        if (hours == 0) {
            hourString = "";
        } else if(hours == 1){
            hourString = hours + " hour";
        } else {
            hourString = hours + " hours";
        }

        if (minutes == 0 && !hourString.isEmpty()) {
            minuteString = "";
        } else if (minutes == 1) {
            minuteString = minutes + " minute";
        }else {
            minuteString = minutes + " minutes";
        }

        return hourString + " " + minuteString;
    }

    public String startTimeString(){

        String eventStartMinute = "";
        if(startTime % 100 < 10){
            eventStartMinute = "0";
        }
        eventStartMinute += startTime % 100;

        return startTime / 100 + ":" + eventStartMinute;
    }

    @Override
    public String toString() {

        String startTimeString="", endTimeString="";
        if(startTime % 100 < 10){
            startTimeString = "0";
        }
        if(endTime % 100 < 10){
            endTimeString = "0";
        }
        startTimeString += String.valueOf(startTime % 100);
        endTimeString += String.valueOf(endTime % 100);

        return startTime / 100 + ":" + startTimeString
                + " - "
                + endTime / 100 + ":" + endTimeString;
    }
}

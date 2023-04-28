package com.example.myth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalTime;

public class Event {
    private String name, details, date, eventId;
    private int hour,minute, duration, remind;

    public Event(String eventId, String name, String details, String date, int duration, int remind, int hour, int minute) {
        this.eventId = eventId;
        this.name = name;
        this.details = details;
        this.date = date;
        this.hour = hour;
        this.minute = minute;
        this.remind = remind;
        this.duration = duration;
    }

    public int getRemind() {
        return remind;
    }

    public void setRemind(int remind) {
        this.remind = remind;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public String getEventId() {
        return eventId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}

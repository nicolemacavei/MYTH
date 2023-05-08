package com.example.myth;

import java.time.LocalDate;

public class Notification {

    private String userId, date;
    private Event event;

    public Notification(String date, String userId, Event event) {
        this.date = date;
        this.userId = userId;
        this.event = event;
    }

    public String getUserId() {
        return userId;
    }

    public Event getEvent() {
        return event;
    }
}

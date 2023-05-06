package com.example.myth;

public class Notification {

    private String type;
    private User user;
    private Event event;

    public Notification(String type, User user, Event event) {
        this.type = type;
        this.user = user;
        this.event = event;
    }

    public String getType() {
        return type;
    }

    public User getUser() {
        return user;
    }

    public Event getEvent() {
        return event;
    }
}

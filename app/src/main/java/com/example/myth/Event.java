package com.example.myth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalTime;

public class Event {

//    private static FirebaseAuth auth;
//    private static FirebaseFirestore firebaseFirestore;
//    private static String userId;
    private String name, details;
    private String date;
    //private static LocalTime time;
    private String duration;

    private int hour,minute;

//    public static ArrayList<Event> eventsForDate(LocalDate date){
//
//        String dateFormatted = CalendarUtils.formattedDate(date);
//        firebaseFirestore = FirebaseFirestore.getInstance();
//        auth = FirebaseAuth.getInstance();
//        userId = auth.getCurrentUser().getUid();
//        ArrayList<Event> events = new ArrayList<>();
//
//        firebaseFirestore.collection("User").document(userId)
//                .collection("Date").document(date.toString()).collection("Event").get().addOnCompleteListener(task -> {
//                    if(task.isSuccessful() && task.getResult() != null){
//
//                        for(QueryDocumentSnapshot queryDocSn : task.getResult()){
//
//                            String name = queryDocSn.getString("name");
//                            String details = queryDocSn.getString("details");
//
//                            Event event = new Event(name, details, dateFormatted, name);
//
//                            System.out.println("COLLECTION EVENT REACHED: " + event.getName());
//                            events.add(event);
//                        }
//                    }
//                });
//
//        System.out.println("LIST OF EVENTS: " + events);
//        return events;
//    }

//    private static void retrieveEventsFromFirestore(String dateFormatted, ArrayList<Event> events) {
//
//        firebaseFirestore.collection("User").document(userId)
//                .collection("Date").document(dateFormatted).collection("Event").get().addOnCompleteListener(task -> {
//                    if(task.isSuccessful() && task.getResult() != null){
//
//                        for(QueryDocumentSnapshot queryDocSn : task.getResult()){
//
//                            String name = queryDocSn.getString("name");
//
//                            System.out.println();
//                            System.out.println("COLLECTION EVENT REACHED: " + name);
//                            System.out.println();
//
//                            Event event = new Event(name, name, dateFormatted, time, name);
//                            events.add(event);
//                        }
//                    }
//                });
//
//    }

    public Event(String name, String details, String date, String duration, int hour, int minute) {
        this.name = name;
        this.details = details;
        this.date = date;
        this.hour = hour;
        this.minute = minute;
        this.duration = duration;
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
//
//    public LocalTime getTime() {
//        return time;
//    }
//
//    public void setTime(LocalTime time) {
//        this.time = time;
//    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}

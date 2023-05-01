package com.example.myth.findTimeSlots;

import java.util.Comparator;

public class TimeSlot {
    private int startTime, endTime;

    public TimeSlot(int startTime, int endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public static class CompareByStartTime implements Comparator<TimeSlot>{

        @Override
        public int compare(TimeSlot o1, TimeSlot o2) {
            return o1.getStartTime() - o2.getStartTime();
        }
    }
}

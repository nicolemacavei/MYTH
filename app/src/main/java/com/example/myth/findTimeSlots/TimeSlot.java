package com.example.myth.findTimeSlots;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Comparator;

public class TimeSlot implements Parcelable {
    private int startTime, endTime;

    public TimeSlot(int startTime, int endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    protected TimeSlot(Parcel in) {
        startTime = in.readInt();
        endTime = in.readInt();
    }

    public static final Creator<TimeSlot> CREATOR = new Creator<TimeSlot>() {
        @Override
        public TimeSlot createFromParcel(Parcel in) {
            return new TimeSlot(in);
        }

        @Override
        public TimeSlot[] newArray(int size) {
            return new TimeSlot[size];
        }
    };

    public int getStartTime() {
        return startTime;
    }

    public int getStartHour(){
        if(startTime >= 100)
            return startTime/100;
        return 0;
    }

    public int getStartMinute(){
        if(startTime >= 10)
            return startTime%100;
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public int getEndHour(){
        if(endTime >= 100)
            return endTime/100;
        return 0;
    }

    public int getEndMinute(){
        if(endTime >= 10)
            return endTime%100;
        return endTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(startTime);
        dest.writeInt(endTime);
    }

    public static class CompareByStartTime implements Comparator<TimeSlot>{

        @Override
        public int compare(TimeSlot o1, TimeSlot o2) {
            return o1.getStartTime() - o2.getStartTime();
        }
    }

    @Override
    public String toString() {
        //transform int to hour view
        return startTime / 100 + ":" + startTime % 100
                + " - "
                + endTime / 100 + ":" + endTime % 100;
    }
}

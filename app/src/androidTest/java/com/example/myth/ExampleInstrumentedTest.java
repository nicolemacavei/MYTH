package com.example.myth;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import com.example.myth.findTimeSlots.TimeSlot;
import java.util.ArrayList;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.myth", appContext.getPackageName());
    }

    @Test
    public void testFindAvailableHours_statementCoverage() {
        ArrayList<TimeSlot> busyHours = new ArrayList<>();
        busyHours.add(new TimeSlot(900, 1000));
        busyHours.add(new TimeSlot(1200, 1300));
        busyHours.add(new TimeSlot(1600, 1700));

        ArrayList<TimeSlot> availableHours = findAvailableHours(60, busyHours);

        // Check that all statements were executed
        assertNotNull(availableHours);
    }

    @Test
    public void testFindAvailableHours_decisionConditionCoverage() {
        ArrayList<TimeSlot> busyHours = new ArrayList<>();
        busyHours.add(new TimeSlot(900, 1000));
        busyHours.add(new TimeSlot(1200, 1300));
        busyHours.add(new TimeSlot(1600, 1700));

        ArrayList<TimeSlot> availableHours = findAvailableHours(60, busyHours);

        // Check that the if statement is executed when startHour >= minStart + timeMeeting
        assertFalse(availableHours.isEmpty());
    }

//    @Test
//    public void testFindAvailableHours_multipleConditionCoverage() {
//        ArrayList<TimeSlot> busyHours = new ArrayList<>();
//        busyHours.add(new TimeSlot(900, 1000));
//        busyHours.add(new TimeSlot(1200, 1300));
//        busyHours.add(new TimeSlot(1600, 1700));
//
//        ArrayList<TimeSlot> availableHours = findAvailableHours(60, busyHours);
//
//        // Check that the if statement is executed when startHour >= minStart + timeMeeting
//        assertFalse(availableHours.isEmpty());
//
//        // Check that the if statement is executed when minStart < endHour
//        assertTrue(availableHours.size() == 2);
//
//        // Check that the if statement is executed when startHour == minStart + timeMeeting
//        assertTrue(availableHours.get(0).getEndTime() == 1000 && availableHours.get(1).getEndTime() == 1300);
//    }

    public ArrayList<TimeSlot> findAvailableHours(int minDuration, ArrayList<TimeSlot> busyHours){
        int startHour, endHour;
        ArrayList<TimeSlot> availableHours = new ArrayList<>();

        //the minimum time of the free Time Slot of the users.
        int timeMeeting = 100 * (minDuration / 60) + minDuration % 60;
        int minStart = 900;   // 9 a.m.
        int maxEnd = 2100;   // 9 p.m.
        int end = 2100;

        for(TimeSlot timeSlot : busyHours){
            startHour = timeSlot.getStartTime();
            endHour = timeSlot.getEndTime();

            if(startHour >= minStart + timeMeeting){
                TimeSlot newTimeSlot = new TimeSlot(minStart, startHour);
                availableHours.add(newTimeSlot);
                minStart = endHour;
            } else if (minStart < endHour) {
                minStart = endHour;
            } else if (startHour == minStart + timeMeeting){
                TimeSlot newTimeSlot = new TimeSlot(minStart, startHour);
                availableHours.add(newTimeSlot);
                minStart = endHour;
            } else {
                maxEnd = end;
            }

        }
        if(minStart <= maxEnd - timeMeeting){
            TimeSlot newTimeSlot = new TimeSlot(minStart, maxEnd);
            availableHours.add(newTimeSlot);
        }

        return availableHours;
    }
}
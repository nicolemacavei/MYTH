package com.example.myth;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;

public class EventReminder{
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "eventReminderChannel";
            String desc = "Channel for Event Reminders";
            int imp = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("eventReminderId", name, imp);
            channel.setDescription(desc);
//            NotificationManager notificationManager
//            notificationManager.createNotificationChannel(channel);
        }
    }
}

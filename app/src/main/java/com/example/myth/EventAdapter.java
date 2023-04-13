package com.example.myth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalTime;
import java.util.List;

public class EventAdapter extends ArrayAdapter<Event>{

    public EventAdapter(@NonNull Context context, List<Event> events) {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Event event = getItem(position);

        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_cell, parent, false);

        TextView eventCellTV = convertView.findViewById(R.id.eventCellText);
        int hour = event.getHour();
        int minute = event.getMinute();
//        CalendarUtils.formattedTime(LocalTime.now())
        String eventTitle =  hour + ":" + minute + " - " + event.getName();
        eventCellTV.setText(eventTitle);
        return convertView;
    }

}

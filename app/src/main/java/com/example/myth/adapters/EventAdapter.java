package com.example.myth.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myth.Event;
import com.example.myth.databinding.ItemContainerEventBinding;
import com.example.myth.utilities.PreferenceManager;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder>{

    private final List<Event> events;

    public EventAdapter(@NonNull Context context, List<Event> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public EventAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerEventBinding itemContainerEventBinding = ItemContainerEventBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new EventViewHolder(itemContainerEventBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.EventViewHolder holder, int position) {
        holder.setEventData(events.get(position));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder{

        ItemContainerEventBinding binding;

        EventViewHolder(ItemContainerEventBinding itemContainerEventBinding){
            super(itemContainerEventBinding.getRoot());
            binding = itemContainerEventBinding;
        }

        void setEventData(Event event){
            String time = String.valueOf(event.getTime());
            String eventTime;
            if(time.length() > 2) {
                eventTime = time.substring(0, time.length() - 2) + ":" + time.substring(time.length() - 2);
            } else if(time.length() > 1){
                eventTime = "00:" + time.substring(time.length() - 2);
            } else {
                eventTime = "00:0" + time.substring(time.length() - 1);
            }
            String durationText = event.getDuration() + " minutes";
            binding.eventNameText.setText(event.getName());
            binding.eventDurationText.setText(durationText);
            binding.eventHourText.setText(eventTime);
            binding.availabilityLine.setVisibility(View.GONE);
        }
    }
}

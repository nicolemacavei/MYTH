package com.example.myth.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myth.Event;
import com.example.myth.databinding.ItemContainerEventRequestBinding;
import com.example.myth.findTimeSlots.TimeSlot;

import java.util.List;

public class EventRequestAdapter extends RecyclerView.Adapter<EventRequestAdapter.EventRequestViewHolder> {

    private final List<Event> events;
    private final String userName;

    public EventRequestAdapter(List<Event> events, String userName) {
        this.events = events;
        this.userName = userName;
    }

    @NonNull
    @Override
    public EventRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerEventRequestBinding itemContainerEventRequestBinding = ItemContainerEventRequestBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new EventRequestViewHolder(itemContainerEventRequestBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventRequestViewHolder holder, int position) {
        holder.setEventRequestData(events.get(position));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    class EventRequestViewHolder extends RecyclerView.ViewHolder{

        ItemContainerEventRequestBinding binding;
        public EventRequestViewHolder(@NonNull ItemContainerEventRequestBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        void setEventRequestData(Event event){
            TimeSlot timeSlot = new TimeSlot(event.getTime(), event.getTime()+event.getDuration());
            binding.textName.setText(userName);
            binding.eventTime.setText(timeSlot.toString());
            binding.eventName.setText(event.getName());
            binding.eventDate.setText(event.getDate());

        }
    }
}

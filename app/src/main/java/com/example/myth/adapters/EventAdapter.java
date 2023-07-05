package com.example.myth.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myth.Event;
import com.example.myth.databinding.ItemContainerEventBinding;
import com.example.myth.interfaces.RecyclerViewInterface;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder>{

    private final List<Event> events;
    private final RecyclerViewInterface recyclerViewInterface;

    public EventAdapter(@NonNull Context context, List<Event> events, RecyclerViewInterface recyclerViewInterface) {
        this.events = events;
        this.recyclerViewInterface = recyclerViewInterface;
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

            itemContainerEventBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(recyclerViewInterface != null){
                        int eventPosition = getAdapterPosition();

                        if(eventPosition != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(eventPosition);
                        }
                    }
                }
            });
        }

        void setEventData(Event event){
            binding.eventNameText.setText(event.getName());
            binding.eventDurationText.setText(event.durationString());
            binding.eventHourText.setText(event.startTimeString());
            binding.availabilityLine.setVisibility(View.GONE);
        }
    }
}

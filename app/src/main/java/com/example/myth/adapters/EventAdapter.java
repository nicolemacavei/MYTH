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

//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        Event event = getItem(position);
//
//        if(convertView == null)
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_container_event, parent, false);
//
//        TextView eventName = convertView.findViewById(R.id.eventNameText);
//        TextView eventHour = convertView.findViewById(R.id.eventHourText);
//        TextView eventDuration = convertView.findViewById(R.id.eventDurationText);
//        //TextView eventAvailabilityLine = convertView.findViewById(R.id.availabilityLine);
//
////        int hour = event.getHour();
////        int minute = event.getMinute();
////        String eventTitle =  hour + ":" + minute + " - " + event.getName();
//
//        String hour = event.getHour() + ":" + event.getMinute();
//        eventName.setText(event.getName());
//        eventDuration.setText(event.getDuration());
//        eventHour.setText(hour);
//
//        return convertView;
//    }

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
            String eventTime = event.getHour() + ":" + event.getMinute();
            String durationText = event.getDuration() + " minutes";
            binding.eventNameText.setText(event.getName());
            binding.eventDurationText.setText(durationText);
            binding.eventHourText.setText(eventTime);
            binding.availabilityLine.setVisibility(View.GONE);
        }
    }
}

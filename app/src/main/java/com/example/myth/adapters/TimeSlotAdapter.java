package com.example.myth.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myth.databinding.ItemContainerTimeSlotBinding;
import com.example.myth.findTimeSlots.TimeSlot;
import com.example.myth.interfaces.RecyclerViewInterface;

import java.util.ArrayList;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {

    private final ArrayList<TimeSlot> availableHours;
    private final RecyclerViewInterface recyclerViewInterface;

    public TimeSlotAdapter(ArrayList<TimeSlot> availableHours, RecyclerViewInterface recyclerViewInterface) {
        this.availableHours = availableHours;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerTimeSlotBinding itemContainerTimeSlotBinding = ItemContainerTimeSlotBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new TimeSlotViewHolder(itemContainerTimeSlotBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        holder.setTimeSlotData(availableHours.get(position));
    }

    @Override
    public int getItemCount() {
        return availableHours.size();
    }

    class TimeSlotViewHolder extends RecyclerView.ViewHolder{

        ItemContainerTimeSlotBinding binding;

        TimeSlotViewHolder(ItemContainerTimeSlotBinding itemContainerTimeSlotBinding) {
            super(itemContainerTimeSlotBinding.getRoot());
            binding = itemContainerTimeSlotBinding;
        }

        void setTimeSlotData(TimeSlot timeSlotData){
            String minutes = (timeSlotData.getEndTime() - timeSlotData.getStartTime()) + " minutes";

            binding.timeSlotText.setText(timeSlotData.toString());
            binding.minutesIntervalText.setText(minutes);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(recyclerViewInterface != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}

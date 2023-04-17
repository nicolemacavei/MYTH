package com.example.myth.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myth.CalendarViewHolder;
import com.example.myth.R;
import com.example.myth.utilities.CalendarUtils;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {

    private final ArrayList<LocalDate> dayOfMonth;
    private final OnItemListener onItemListener;

    public CalendarAdapter(ArrayList<LocalDate> dayOfMonth, OnItemListener onItemListener) {
        this.dayOfMonth = dayOfMonth;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.container_calendar_day, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.1666666);
        return new CalendarViewHolder(view, onItemListener, dayOfMonth);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {

        LocalDate date = dayOfMonth.get(position);
        if(date == null)
            holder.dayOfMonth.setText("");
        else {
            holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));
            if(date.equals(CalendarUtils.selectedDate))
                holder.parentView.setBackgroundColor(Color.LTGRAY);
        }
    }

    @Override
    public int getItemCount() {
        return dayOfMonth.size();
    }

    public interface OnItemListener{
        void onItemClick(int position, LocalDate date);
    }
}
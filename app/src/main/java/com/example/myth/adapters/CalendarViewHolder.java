package com.example.myth.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myth.R;
import com.example.myth.adapters.CalendarAdapter;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public final View parentView;
    private final ArrayList<LocalDate> daysInMonth;
    public final TextView dayOfMonth;
    public final CalendarAdapter.OnItemListener onItemListener;

    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener, ArrayList<LocalDate> daysInMonth) {
        super(itemView);
        this.parentView = itemView.findViewById(R.id.parentView);
        dayOfMonth = itemView.findViewById(R.id.cellDayText);
        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);
        this.daysInMonth = daysInMonth;
    }

    @Override
    public void onClick(View v) {
        onItemListener.onItemClick(getAdapterPosition(), daysInMonth.get(getAdapterPosition()));
    }
}

package com.example.calendartest;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarViewHolder extends RecyclerView.ViewHolder
{
    TextView dayOfMonth;
    View parentView;
    ArrayList<String> daysOfMonth;

    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener, ArrayList<String> daysOfMonth)
    {
        super(itemView);
        dayOfMonth = itemView.findViewById(R.id.dayOfMonthTV);
        parentView = itemView;
        this.daysOfMonth = daysOfMonth;

        itemView.setOnClickListener(v -> {
            final LocalDate date = LocalDate.parse(daysOfMonth.get(getAdapterPosition()));
            onItemListener.onItemClick(getAdapterPosition(), date);
        });
    }
}




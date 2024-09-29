package com.example.calendartest00;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;

class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder>
{
    private final ArrayList<LocalDate> days; // 存儲日期的列表
    private final OnItemListener onItemListener; // 點擊事件監聽器

    // CalendarAdapter 構造函數，接受日期列表和點擊事件監聽器
    public CalendarAdapter(ArrayList<LocalDate> days, OnItemListener onItemListener)
    {
        this.days = days;
        this.onItemListener = onItemListener;
    }

    // 創建新的 ViewHolder 對象
    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

        // 設定每個日曆單元格的高度
        if(days.size() > 15) // 如果是月視圖
            layoutParams.height = (int) (parent.getHeight() * 0.166666666); // 高度為父容器高度的六分之一
        else // 如果是周視圖
            layoutParams.height = (int) parent.getHeight(); // 高度為父容器的高度

        return new CalendarViewHolder(view, onItemListener, days); // 返回新的 ViewHolder 對象
    }

    // 將數據綁定到 ViewHolder
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position)
    {
        final LocalDate date = days.get(position); // 獲取當前位置的日期

        holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth())); // 設定日期

        // 如果當前日期是選中的日期，改變背景顏色
        if(date.equals(CalendarUtils.selectedDate))
            holder.parentView.setBackgroundColor(Color.LTGRAY);

        // 設定日期文字顏色，當前月的日期顯示為黑色，其他月份的日期顯示為灰色
        if(date.getMonth().equals(CalendarUtils.selectedDate.getMonth()))
            holder.dayOfMonth.setTextColor(Color.BLACK);
        else
            holder.dayOfMonth.setTextColor(Color.LTGRAY);
    }

    // 返回項目數量
    @Override
    public int getItemCount()
    {
        return days.size();
    }

    // 定義點擊事件監聽器接口
    public interface  OnItemListener
    {
        void onItemClick(int position, LocalDate date); // 當點擊日曆單元格時調用
    }
}

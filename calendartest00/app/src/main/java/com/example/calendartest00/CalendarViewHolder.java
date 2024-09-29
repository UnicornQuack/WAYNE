package com.example.calendartest00;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    private final ArrayList<LocalDate> days; // 存儲所有日曆日期的列表
    public final View parentView; // 父視圖
    public final TextView dayOfMonth; // 顯示日期的 TextView
    private final CalendarAdapter.OnItemListener onItemListener; // 點擊事件監聽器

    // 構造方法，初始化視圖和點擊事件
    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener, ArrayList<LocalDate> days)
    {
        super(itemView);
        parentView = itemView.findViewById(R.id.parentView); // 獲取父視圖
        dayOfMonth = itemView.findViewById(R.id.cellDayText); // 獲取顯示日期的 TextView
        this.onItemListener = onItemListener; // 初始化點擊事件監聽器
        itemView.setOnClickListener(this); // 為當前項設置點擊事件
        this.days = days; // 初始化日期列表
    }

    // 點擊事件處理方法
    @Override
    public void onClick(View view)
    {
        // 當某一天被點擊時，調用 onItemListener 的 onItemClick 方法，並傳遞被點擊的日期和其位置
        onItemListener.onItemClick(getAdapterPosition(), days.get(getAdapterPosition()));
    }
}

package com.example.calendartest00;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

// HourAdapter 是自定義的 ArrayAdapter，用於將 HourEvent 對象與 ListView 或 GridView 綁定
public class HourAdapter extends ArrayAdapter<HourEvent>
{
    // 構造函數，用於初始化適配器
    public HourAdapter(@NonNull Context context, List<HourEvent> hourEvents)
    {
        super(context, 0, hourEvents);
    }

    // 覆寫 getView 方法，用於將每個 HourEvent 對象與其對應的視圖綁定
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        // 獲取當前位置的 HourEvent 對象
        HourEvent event = getItem(position);

        // 如果 convertView 為空，則從布局文件創建一個新的視圖
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.hour_cell, parent, false);

        // 設置顯示的時間
        setHour(convertView, event.time);
        // 設置顯示的事件列表
        setEvents(convertView, event.events);

        // 返回已綁定數據的視圖
        return convertView;
    }

    // 設置顯示的時間
    private void setHour(View convertView, LocalTime time)
    {
        // 獲取時間的 TextView 並設置時間
        TextView timeTV = convertView.findViewById(R.id.timeTV);
        timeTV.setText(CalendarUtils.formattedShortTime(time));
    }

    // 設置顯示的事件列表
    private void setEvents(View convertView, ArrayList<Event> events)
    {
        // 獲取每個事件的 TextView
        TextView event1 = convertView.findViewById(R.id.event1);
        TextView event2 = convertView.findViewById(R.id.event2);
        TextView event3 = convertView.findViewById(R.id.event3);

        // 根據事件的數量設置對應的顯示
        if(events.size() == 0)
        {
            // 如果沒有事件，隱藏所有的事件 TextView
            hideEvent(event1);
            hideEvent(event2);
            hideEvent(event3);
        }
        else if(events.size() == 1)
        {
            // 如果只有一個事件，顯示第一個事件，隱藏其他兩個
            setEvent(event1, events.get(0));
            hideEvent(event2);
            hideEvent(event3);
        }
        else if(events.size() == 2)
        {
            // 如果有兩個事件，顯示前兩個事件，隱藏第三個
            setEvent(event1, events.get(0));
            setEvent(event2, events.get(1));
            hideEvent(event3);
        }
        else if(events.size() == 3)
        {
            // 如果有三個事件，顯示所有事件
            setEvent(event1, events.get(0));
            setEvent(event2, events.get(1));
            setEvent(event3, events.get(2));
        }
        else
        {
            // 如果有超過三個事件，顯示前兩個事件，並在第三個 TextView 中顯示剩餘事件的數量
            setEvent(event1, events.get(0));
            setEvent(event2, events.get(1));
            event3.setVisibility(View.VISIBLE);
            String eventsNotShown = String.valueOf(events.size() - 2);
            eventsNotShown += " More Events";
            event3.setText(eventsNotShown);
        }
    }

    // 將事件名稱設置到 TextView 並顯示它
    private void setEvent(TextView textView, Event event)
    {
        textView.setText(event.getName());
        textView.setVisibility(View.VISIBLE);
    }

    // 隱藏指定的 TextView
    private void hideEvent(TextView tv)
    {
        tv.setVisibility(View.INVISIBLE);
    }
}

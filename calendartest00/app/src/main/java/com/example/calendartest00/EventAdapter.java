package com.example.calendartest00;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class EventAdapter extends ArrayAdapter<Event>
{
    // 建構函數，初始化適配器
    public EventAdapter(@NonNull Context context, List<Event> events)
    {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        // 獲取當前位置的事件對象
        Event event = getItem(position);

        // 如果視圖為空，則從佈局文件中加載視圖
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_cell, parent, false);

        // 獲取事件視圖中的 TextView 控件
        TextView eventCellTV = convertView.findViewById(R.id.eventCellTV);

        // 設置事件標題，包含事件名稱和格式化的時間
        String eventTitle = event.getName() + " " + CalendarUtils.formattedTime(event.getTime());
        eventCellTV.setText(eventTitle);

        // 返回修改後的視圖
        return convertView;
    }
}

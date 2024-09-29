package com.example.calendartest00;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.time.LocalTime;

public class EventEditActivity extends AppCompatActivity
{
    // 用於輸入事件名稱的 EditText 元件
    private EditText eventNameET;

    // 顯示事件日期和時間的 TextView 元件
    private TextView eventDateTV, eventTimeTV;

    // 保存事件的時間
    private LocalTime time;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // 將 activity_event_edit.xml 布局設置為此 Activity 的內容視圖
        setContentView(R.layout.activity_event_edit);

        // 初始化界面中的元件
        initWidgets();

        // 獲取當前時間
        time = LocalTime.now();

        // 設置事件日期和時間的 TextView
        eventDateTV.setText("日期: " + CalendarUtils.formattedDate(CalendarUtils.selectedDate));
        eventTimeTV.setText("時間: " + CalendarUtils.formattedTime(time));
    }

    // 初始化界面中的元件
    private void initWidgets()
    {
        // 獲取事件名稱的 EditText
        eventNameET = findViewById(R.id.eventNameET);

        // 獲取顯示事件日期的 TextView
        eventDateTV = findViewById(R.id.eventDateTV);

        // 獲取顯示事件時間的 TextView
        eventTimeTV = findViewById(R.id.eventTimeTV);
    }

    // 保存事件的動作
    public void saveEventAction(View view)
    {
        // 獲取用戶輸入的事件名稱
        String eventName = eventNameET.getText().toString();

        // 創建一個新的事件對象
        Event newEvent = new Event(eventName, CalendarUtils.selectedDate, time);

        // 將新事件添加到事件列表中
        Event.eventsList.add(newEvent);

        // 結束當前 Activity 並返回到上一個界面
        finish();
    }
}

package com.example.bored.ui.traffic;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TrafficViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public TrafficViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is traffic fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
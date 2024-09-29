package com.example.bored.ui.love;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoveViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public LoveViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is love fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
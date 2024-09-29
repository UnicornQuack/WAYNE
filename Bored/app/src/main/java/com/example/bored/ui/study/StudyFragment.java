package com.example.bored.ui.study;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bored.databinding.FragmentStudyBinding;

public class StudyFragment extends Fragment {

    private FragmentStudyBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        StudyViewModel studyViewModel =
                new ViewModelProvider(this).get(StudyViewModel.class);

        binding = FragmentStudyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textStudy;
        studyViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
package com.example.bored.ui.travel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bored.databinding.FragmentTravelBinding;

public class TravelFragment extends Fragment {

    private FragmentTravelBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TravelViewModel travelViewModel =
                new ViewModelProvider(this).get(TravelViewModel.class);

        binding = FragmentTravelBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textTravel;
        travelViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

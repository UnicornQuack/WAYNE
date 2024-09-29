package com.example.bored.ui.traffic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bored.databinding.FragmentTrafficBinding;

public class TrafficFragment extends Fragment {

    private FragmentTrafficBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TrafficViewModel trafficViewModel =
                new ViewModelProvider(this).get(TrafficViewModel.class);

        binding = FragmentTrafficBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textTraffic;
        trafficViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
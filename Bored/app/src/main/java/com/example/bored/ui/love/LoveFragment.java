package com.example.bored.ui.love;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bored.databinding.FragmentLoveBinding;



public class LoveFragment extends Fragment {

    private FragmentLoveBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LoveViewModel loveViewModel =
                new ViewModelProvider(this).get(LoveViewModel.class);

        binding = FragmentLoveBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textLove;
        loveViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
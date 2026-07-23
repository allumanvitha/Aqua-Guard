package com.aquaguard.presentation.ui.history;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.aquaguard.databinding.FragmentHistoryBinding;
import com.aquaguard.presentation.history.HistoryViewModel;
import com.aquaguard.presentation.ui.adapters.DeviceChipAdapter;
import com.aquaguard.presentation.ui.adapters.ValveLogAdapter;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HistoryFragment extends Fragment {
    private FragmentHistoryBinding binding;
    private HistoryViewModel viewModel;
    private DeviceChipAdapter chipAdapter;
    private ValveLogAdapter logAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(HistoryViewModel.class);

        setupChipsRecyclerView();
        setupLogsRecyclerView();
        setupSearchInput();
        setupFilterButtons();
        observeViewModel();
    }

    private void setupChipsRecyclerView() {
        chipAdapter = new DeviceChipAdapter(device -> viewModel.selectDevice(device.getDeviceId()));
        binding.rvDevices.setAdapter(chipAdapter);
    }

    private void setupLogsRecyclerView() {
        logAdapter = new ValveLogAdapter();
        binding.rvHistoryLogs.setAdapter(logAdapter);
    }

    private void setupSearchInput() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setSearchQuery(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFilterButtons() {
        binding.btnFilterAll.setOnClickListener(v -> {
            viewModel.setTriggerFilter("ALL");
            updateFilterButtonStates("ALL");
        });

        binding.btnFilterUser.setOnClickListener(v -> {
            viewModel.setTriggerFilter("USER");
            updateFilterButtonStates("USER");
        });

        binding.btnFilterSystem.setOnClickListener(v -> {
            viewModel.setTriggerFilter("SYSTEM"); // Handles filtering of SYSTEM or SYSTEM_AUTO
            updateFilterButtonStates("SYSTEM");
        });
    }

    private void updateFilterButtonStates(String activeFilter) {
        int activeBg = Color.parseColor("#2196f3");
        int inactiveBg = Color.parseColor("#1fffffff");
        int activeText = Color.WHITE;
        int inactiveText = Color.parseColor("#80ffffff");

        binding.btnFilterAll.setBackgroundTintList(android.content.res.ColorStateList.valueOf(activeFilter.equals("ALL") ? activeBg : inactiveBg));
        binding.btnFilterAll.setTextColor(activeFilter.equals("ALL") ? activeText : inactiveText);

        binding.btnFilterUser.setBackgroundTintList(android.content.res.ColorStateList.valueOf(activeFilter.equals("USER") ? activeBg : inactiveBg));
        binding.btnFilterUser.setTextColor(activeFilter.equals("USER") ? activeText : inactiveText);

        binding.btnFilterSystem.setBackgroundTintList(android.content.res.ColorStateList.valueOf(activeFilter.equals("SYSTEM") ? activeBg : inactiveBg));
        binding.btnFilterSystem.setTextColor(activeFilter.equals("SYSTEM") ? activeText : inactiveText);
    }

    private void observeViewModel() {
        // Devices chips
        viewModel.getDevices().observe(getViewLifecycleOwner(), list -> {
            if (list != null && !list.isEmpty()) {
                chipAdapter.setDevices(list);
            }
        });

        viewModel.getSelectedDeviceId().observe(getViewLifecycleOwner(), id -> {
            chipAdapter.setSelectedDeviceId(id);
        });

        // Logs
        viewModel.getValveLogs().observe(getViewLifecycleOwner(), list -> {
            if (list == null || list.isEmpty()) {
                binding.tvEmptyHistory.setVisibility(View.VISIBLE);
                binding.rvHistoryLogs.setVisibility(View.GONE);
                logAdapter.setLogs(null);
            } else {
                binding.tvEmptyHistory.setVisibility(View.GONE);
                binding.rvHistoryLogs.setVisibility(View.VISIBLE);
                logAdapter.setLogs(list);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

package com.aquaguard.presentation.ui.alerts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.aquaguard.databinding.FragmentAlertsBinding;
import com.aquaguard.domain.model.Alert;
import com.aquaguard.presentation.alerts.AlertsViewModel;
import com.aquaguard.presentation.ui.adapters.AlertAdapter;
import com.aquaguard.presentation.ui.adapters.DeviceChipAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AlertsFragment extends Fragment {
    private FragmentAlertsBinding binding;
    private AlertsViewModel viewModel;
    private DeviceChipAdapter chipAdapter;
    private AlertAdapter alertAdapter;

    private List<Alert> activeAlertsList = new ArrayList<>();
    private List<Alert> alertHistoryList = new ArrayList<>();
    private int selectedTab = 0; // 0: Active, 1: History

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAlertsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AlertsViewModel.class);

        setupChipsRecyclerView();
        setupAlertsRecyclerView();
        setupTabLayout();
        observeViewModel();
    }

    private void setupChipsRecyclerView() {
        chipAdapter = new DeviceChipAdapter(device -> viewModel.selectDevice(device.getDeviceId()));
        binding.rvDevices.setAdapter(chipAdapter);
    }

    private void setupAlertsRecyclerView() {
        alertAdapter = new AlertAdapter(alert -> viewModel.resolveAlert(alert.getAlertId()));
        binding.rvAlerts.setAdapter(alertAdapter);
    }

    private void setupTabLayout() {
        binding.tabsAlerts.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedTab = tab.getPosition();
                updateAlertsList();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
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

        // Active Alerts data
        viewModel.getActiveAlerts().observe(getViewLifecycleOwner(), list -> {
            activeAlertsList = list != null ? list : new ArrayList<>();
            if (selectedTab == 0) {
                updateAlertsList();
            }
        });

        // Historical Alerts data
        viewModel.getAlertHistory().observe(getViewLifecycleOwner(), list -> {
            alertHistoryList = list != null ? list : new ArrayList<>();
            if (selectedTab == 1) {
                updateAlertsList();
            }
        });
    }

    private void updateAlertsList() {
        List<Alert> targetList = (selectedTab == 0) ? activeAlertsList : alertHistoryList;
        alertAdapter.setAlerts(targetList);

        if (targetList.isEmpty()) {
            binding.tvEmptyAlerts.setVisibility(View.VISIBLE);
            binding.rvAlerts.setVisibility(View.GONE);
        } else {
            binding.tvEmptyAlerts.setVisibility(View.GONE);
            binding.rvAlerts.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

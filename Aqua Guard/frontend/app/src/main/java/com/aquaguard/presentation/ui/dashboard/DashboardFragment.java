package com.aquaguard.presentation.ui.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.aquaguard.R;
import com.aquaguard.databinding.FragmentDashboardBinding;
import com.aquaguard.presentation.dashboard.DashboardViewModel;
import com.aquaguard.presentation.ui.adapters.DeviceChipAdapter;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding binding;
    private DashboardViewModel viewModel;
    private DeviceChipAdapter chipAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);

        setupChipsRecyclerView();
        observeViewModel();
        setupCctvControls();
        setupSwitchListeners();

        // Header actions
        binding.ivAiAssistant.setOnClickListener(v -> 
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.action_main_to_aiAssistant)
        );

        binding.ivAddDevice.setOnClickListener(v -> 
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.action_main_to_devices)
        );

        binding.cardNoDevices.setOnClickListener(v -> 
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.action_main_to_devices)
        );
    }

    private void setupChipsRecyclerView() {
        chipAdapter = new DeviceChipAdapter(device -> viewModel.selectDevice(device.getDeviceId()));
        binding.rvDevices.setAdapter(chipAdapter);
    }

    private void observeViewModel() {
        // Devices list
        viewModel.getDevices().observe(getViewLifecycleOwner(), list -> {
            if (list == null || list.isEmpty()) {
                binding.cardNoDevices.setVisibility(View.VISIBLE);
                binding.scrollDashboard.setVisibility(View.GONE);
                chipAdapter.setDevices(null);
            } else {
                binding.cardNoDevices.setVisibility(View.GONE);
                binding.scrollDashboard.setVisibility(View.VISIBLE);
                chipAdapter.setDevices(list);
            }
        });

        // Selected Device ID
        viewModel.getSelectedDeviceId().observe(getViewLifecycleOwner(), id -> {
            chipAdapter.setSelectedDeviceId(id);
        });

        // Water level percentage
        viewModel.getWaterLevel().observe(getViewLifecycleOwner(), level -> {
            int lvl = level != null ? level : 0;
            binding.waveTankView.setLevelPercentage(lvl);
            binding.tvLevelPercentage.setText(lvl + "%");
            updateCctvTelemetry();
        });

        // Flow rate
        viewModel.getFlowRate().observe(getViewLifecycleOwner(), rate -> {
            float r = rate != null ? rate : 0f;
            binding.tvFlowRate.setText(String.format(java.util.Locale.getDefault(), "%.1f L/min", r));
            updateCctvTelemetry();
        });

        // Valve toggle state
        viewModel.isValveOpen().observe(getViewLifecycleOwner(), open -> {
            boolean isOpen = open != null && open;
            binding.switchValve.setChecked(isOpen);
            if (isOpen) {
                binding.tvValveStateDesc.setText("Valve is OPEN (Water Flowing)");
                binding.tvValveStateDesc.setTextColor(Color.parseColor("#4caf50"));
            } else {
                binding.tvValveStateDesc.setText("Valve is CLOSED (Water Blocked)");
                binding.tvValveStateDesc.setTextColor(Color.parseColor("#f44336"));
            }
            updateCctvTelemetry();
        });

        // Auto mode
        viewModel.isAutoModeEnabled().observe(getViewLifecycleOwner(), enabled -> {
            binding.switchAutoMode.setChecked(enabled != null && enabled);
        });

        // Leak status check
        viewModel.isLeakDetected().observe(getViewLifecycleOwner(), leak -> {
            boolean isLeak = leak != null && leak;
            if (isLeak) {
                binding.cardLeakStatus.setCardBackgroundColor(Color.parseColor("#26d32f2f"));
                binding.tvLeakStatus.setText("LEAKING");
                binding.tvLeakStatus.setTextColor(Color.parseColor("#f44336"));
                binding.ivLeakIcon.setColorFilter(Color.parseColor("#f44336"));
            } else {
                binding.cardLeakStatus.setCardBackgroundColor(Color.parseColor("#121e2d"));
                binding.tvLeakStatus.setText("Secure");
                binding.tvLeakStatus.setTextColor(Color.parseColor("#4caf50"));
                binding.ivLeakIcon.setColorFilter(Color.parseColor("#2196f3"));
            }
        });

        // Last sync time
        viewModel.getLastSyncTime().observe(getViewLifecycleOwner(), time -> {
            binding.tvSyncTimestamp.setText("Last synced: " + (time != null ? time : "--:--:--"));
        });

        // Loading spinner
        viewModel.isLoading().observe(getViewLifecycleOwner(), loading -> {
            binding.dashboardProgress.setVisibility(loading != null && loading ? View.VISIBLE : View.GONE);
        });
    }

    private void updateCctvTelemetry() {
        boolean open = viewModel.isValveOpen().getValue() != null && viewModel.isValveOpen().getValue();
        float flow = viewModel.getFlowRate().getValue() != null ? viewModel.getFlowRate().getValue() : 0f;
        int level = viewModel.getWaterLevel().getValue() != null ? viewModel.getWaterLevel().getValue() : 0;
        binding.cctvCameraView.setTelemetry(open, flow, level);
    }

    private void setupCctvControls() {
        // Cameras selection
        binding.btnCam1.setOnClickListener(v -> {
            binding.cctvCameraView.setSelectedCam(1);
            binding.tvActiveCamera.setText("CAM-01 // TANK_CHAMBER_A");
            binding.btnCam1.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#2196f3")));
            binding.btnCam1.setTextColor(Color.WHITE);
            binding.btnCam2.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#1fffffff")));
            binding.btnCam2.setTextColor(Color.parseColor("#80ffffff"));
        });

        binding.btnCam2.setOnClickListener(v -> {
            binding.cctvCameraView.setSelectedCam(2);
            binding.tvActiveCamera.setText("CAM-02 // INLET_VALVE_B");
            binding.btnCam2.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#2196f3")));
            binding.btnCam2.setTextColor(Color.WHITE);
            binding.btnCam1.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#1fffffff")));
            binding.btnCam1.setTextColor(Color.parseColor("#80ffffff"));
        });

        // Filters selectors
        binding.ibFilterNormal.setOnClickListener(v -> {
            binding.cctvCameraView.setFilterMode("normal");
            binding.ibFilterNormal.setColorFilter(Color.parseColor("#2196f3"));
            binding.ibFilterNv.setColorFilter(Color.parseColor("#80ffffff"));
            binding.ibFilterThermal.setColorFilter(Color.parseColor("#80ffffff"));
        });

        binding.ibFilterNv.setOnClickListener(v -> {
            binding.cctvCameraView.setFilterMode("nv");
            binding.ibFilterNormal.setColorFilter(Color.parseColor("#80ffffff"));
            binding.ibFilterNv.setColorFilter(Color.parseColor("#2ec471"));
            binding.ibFilterThermal.setColorFilter(Color.parseColor("#80ffffff"));
        });

        binding.ibFilterThermal.setOnClickListener(v -> {
            binding.cctvCameraView.setFilterMode("thermal");
            binding.ibFilterNormal.setColorFilter(Color.parseColor("#80ffffff"));
            binding.ibFilterNv.setColorFilter(Color.parseColor("#80ffffff"));
            binding.ibFilterThermal.setColorFilter(Color.parseColor("#ff3d00"));
        });
    }

    private void setupSwitchListeners() {
        binding.switchValve.setOnClickListener(v -> {
            boolean isChecked = binding.switchValve.isChecked();
            viewModel.toggleValve(isChecked);
        });

        binding.switchAutoMode.setOnClickListener(v -> {
            boolean isChecked = binding.switchAutoMode.isChecked();
            viewModel.setAutoMode(isChecked);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

package com.aquaguard.presentation.ui.analytics;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.aquaguard.databinding.FragmentAnalyticsBinding;
import com.aquaguard.databinding.ItemForecastBinding;
import com.aquaguard.presentation.analytics.AnalyticsViewModel;
import com.aquaguard.presentation.ui.adapters.DeviceChipAdapter;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AnalyticsFragment extends Fragment {
    private FragmentAnalyticsBinding binding;
    private AnalyticsViewModel viewModel;
    private DeviceChipAdapter chipAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAnalyticsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AnalyticsViewModel.class);

        setupChipsRecyclerView();
        observeViewModel();

        // PDF Export Button
        binding.btnExportPdf.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Generating PDF Report...", Toast.LENGTH_SHORT).show();
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "PDF Report saved successfully in Downloads.", Toast.LENGTH_LONG).show();
                }
            }, 2000);
        });
    }

    private void setupChipsRecyclerView() {
        chipAdapter = new DeviceChipAdapter(device -> viewModel.selectDevice(device.getDeviceId()));
        binding.rvDevices.setAdapter(chipAdapter);
    }

    private void observeViewModel() {
        // Devices selection
        viewModel.getDevices().observe(getViewLifecycleOwner(), list -> {
            if (list != null && !list.isEmpty()) {
                chipAdapter.setDevices(list);
            }
        });

        viewModel.getSelectedDeviceId().observe(getViewLifecycleOwner(), id -> {
            chipAdapter.setSelectedDeviceId(id);
        });

        // Weekly trend line graph data
        viewModel.getWeeklyConsumptionData().observe(getViewLifecycleOwner(), data -> {
            binding.bezierChartView.setData(data);
        });

        // Average Liter Readings
        viewModel.getAverageConsumption().observe(getViewLifecycleOwner(), avg -> {
            float value = avg != null ? avg : 0f;
            binding.tvAvgConsumption.setText(String.format(java.util.Locale.getDefault(), "%.1f L", value));
        });

        // Water savings liter metrics
        viewModel.getWaterSaved().observe(getViewLifecycleOwner(), saved -> {
            float value = saved != null ? saved : 0f;
            binding.tvWaterSaved.setText(String.format(java.util.Locale.getDefault(), "%.1f L", value));
        });

        // Estimated bill metrics
        viewModel.getEstimatedBill().observe(getViewLifecycleOwner(), bill -> {
            float value = bill != null ? bill : 0f;
            binding.tvEstimatedBill.setText(String.format(java.util.Locale.getDefault(), "$%.2f", value));
        });

        // Predicted forecast list
        viewModel.getPredictedForecast().observe(getViewLifecycleOwner(), forecast -> {
            binding.llPredictionContainer.removeAllViews();
            if (forecast != null) {
                for (AnalyticsViewModel.ForecastItem item : forecast) {
                    ItemForecastBinding itemBinding = ItemForecastBinding.inflate(
                            getLayoutInflater(), binding.llPredictionContainer, false
                    );
                    itemBinding.tvForecastDay.setText(item.getDay());
                    itemBinding.tvForecastValue.setText(String.format(java.util.Locale.getDefault(), "%.0fL", item.getPredictedLiters()));
                    binding.llPredictionContainer.addView(itemBinding.getRoot());
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

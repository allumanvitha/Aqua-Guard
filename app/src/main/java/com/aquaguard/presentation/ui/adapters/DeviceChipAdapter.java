package com.aquaguard.presentation.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aquaguard.databinding.ItemDeviceChipBinding;
import com.aquaguard.domain.model.Device;

import java.util.ArrayList;
import java.util.List;

public class DeviceChipAdapter extends RecyclerView.Adapter<DeviceChipAdapter.ViewHolder> {
    private List<Device> devices = new ArrayList<>();
    private String selectedDeviceId = null;
    private final OnDeviceSelectedListener listener;

    public interface OnDeviceSelectedListener {
        void onDeviceSelected(Device device);
    }

    public DeviceChipAdapter(OnDeviceSelectedListener listener) {
        this.listener = listener;
    }

    public void setDevices(List<Device> newDevices) {
        this.devices = newDevices != null ? newDevices : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setSelectedDeviceId(String deviceId) {
        this.selectedDeviceId = deviceId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDeviceChipBinding binding = ItemDeviceChipBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Device device = devices.get(position);
        holder.binding.chipDevice.setText(device.getName());

        // Manage selection state programmatically
        boolean isSelected = device.getDeviceId().equals(selectedDeviceId);
        holder.binding.chipDevice.setChecked(isSelected);

        holder.binding.chipDevice.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeviceSelected(device);
            }
        });
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemDeviceChipBinding binding;

        ViewHolder(ItemDeviceChipBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

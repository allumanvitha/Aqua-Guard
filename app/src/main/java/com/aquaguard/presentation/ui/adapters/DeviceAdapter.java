package com.aquaguard.presentation.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aquaguard.databinding.ItemDeviceBinding;
import com.aquaguard.domain.model.Device;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {
    private List<Device> devices = new ArrayList<>();
    private final Set<String> expandedDeviceIds = new HashSet<>();
    private java.util.Map<String, com.aquaguard.presentation.devices.DevicesViewModel.OtaState> otaStatesMap = new java.util.HashMap<>();
    private final OnDeviceClickListener listener;

    public interface OnDeviceClickListener {
        void onRenameClick(Device device);
        void onDeleteClick(Device device);
        void onWifiConfigClick(Device device);
        void onOtaUpdateClick(Device device, ItemDeviceBinding itemBinding);
    }

    public DeviceAdapter(OnDeviceClickListener listener) {
        this.listener = listener;
    }

    public void setDevices(List<Device> newDevices) {
        this.devices = newDevices != null ? newDevices : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOtaStates(java.util.Map<String, com.aquaguard.presentation.devices.DevicesViewModel.OtaState> map) {
        this.otaStatesMap = map != null ? map : new java.util.HashMap<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDeviceBinding binding = ItemDeviceBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Device device = devices.get(position);

        holder.binding.tvDeviceName.setText(device.getName());
        holder.binding.tvDeviceId.setText("ID: " + device.getDeviceId());
        holder.binding.tvDeviceBattery.setText("🔌 Power: " + device.getBatteryLevel() + "%");
        holder.binding.tvDeviceFirmware.setText("⚙️ Firmware: v" + device.getFirmwareVersion());

        // Status indicator dot
        int statusColor = device.isOnline() ? Color.parseColor("#4caf50") : Color.parseColor("#9e9e9e");
        holder.binding.vStatusDot.getBackground().setTint(statusColor);

        // Expandable panel toggle
        boolean isExpanded = expandedDeviceIds.contains(device.getDeviceId());
        holder.binding.llExpandedActions.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.binding.ivExpand.setRotation(isExpanded ? 180f : 0f);

        holder.binding.ivExpand.setOnClickListener(v -> {
            if (isExpanded) {
                expandedDeviceIds.remove(device.getDeviceId());
            } else {
                expandedDeviceIds.add(device.getDeviceId());
            }
            notifyItemChanged(position);
        });

        // Click Callbacks
        holder.binding.ivRename.setOnClickListener(v -> {
            if (listener != null) listener.onRenameClick(device);
        });

        holder.binding.ivDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(device);
        });

        holder.binding.btnWifiConfig.setOnClickListener(v -> {
            if (listener != null) listener.onWifiConfigClick(device);
        });

        holder.binding.btnOtaUpdate.setOnClickListener(v -> {
            if (listener != null) listener.onOtaUpdateClick(device, holder.binding);
        });

        // Bind OTA progress card
        com.aquaguard.presentation.devices.DevicesViewModel.OtaState ota = otaStatesMap.get(device.getDeviceId());
        if (ota != null) {
            holder.binding.cardOtaProgress.setVisibility(View.VISIBLE);
            if (ota.getStatus() == com.aquaguard.presentation.devices.DevicesViewModel.OtaState.Status.CHECKING) {
                holder.binding.tvOtaStatusText.setText("Checking for updates...");
                holder.binding.pbOtaProgress.setIndeterminate(true);
            } else if (ota.getStatus() == com.aquaguard.presentation.devices.DevicesViewModel.OtaState.Status.DOWNLOADING) {
                holder.binding.tvOtaStatusText.setText("Downloading: " + (int)(ota.getProgress() * 100) + "%");
                holder.binding.pbOtaProgress.setIndeterminate(false);
                holder.binding.pbOtaProgress.setProgress((int)(ota.getProgress() * 100));
            } else if (ota.getStatus() == com.aquaguard.presentation.devices.DevicesViewModel.OtaState.Status.INSTALLING) {
                holder.binding.tvOtaStatusText.setText("Installing updates...");
                holder.binding.pbOtaProgress.setIndeterminate(true);
            } else if (ota.getStatus() == com.aquaguard.presentation.devices.DevicesViewModel.OtaState.Status.SUCCESS) {
                holder.binding.tvOtaStatusText.setText("Update installed successfully!");
                holder.binding.pbOtaProgress.setIndeterminate(false);
                holder.binding.pbOtaProgress.setProgress(100);
            }
        } else {
            holder.binding.cardOtaProgress.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemDeviceBinding binding;

        ViewHolder(ItemDeviceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

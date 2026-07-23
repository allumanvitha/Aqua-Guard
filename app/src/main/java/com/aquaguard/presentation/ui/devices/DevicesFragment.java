package com.aquaguard.presentation.ui.devices;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.aquaguard.databinding.FragmentDevicesBinding;
import com.aquaguard.databinding.ItemDeviceBinding;
import com.aquaguard.domain.model.Device;
import com.aquaguard.presentation.devices.DevicesViewModel;
import com.aquaguard.presentation.ui.adapters.DeviceAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.aquaguard.R;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DevicesFragment extends Fragment {
    private FragmentDevicesBinding binding;
    private DevicesViewModel viewModel;
    private DeviceAdapter adapter;
    private AlertDialog loadingDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDevicesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(DevicesViewModel.class);

        binding.ivBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        setupDevicesRecyclerView();
        observeViewModel();

        binding.ivAddNewDevice.setOnClickListener(v -> showRegisterDeviceDialog());
        binding.btnAddDeviceEmpty.setOnClickListener(v -> showRegisterDeviceDialog());
    }

    private void setupDevicesRecyclerView() {
        adapter = new DeviceAdapter(new DeviceAdapter.OnDeviceClickListener() {
            @Override
            public void onRenameClick(Device device) {
                showRenameDeviceDialog(device);
            }

            @Override
            public void onDeleteClick(Device device) {
                showDeleteConfirmDialog(device);
            }

            @Override
            public void onWifiConfigClick(Device device) {
                showWifiProvisionDialog(device);
            }

            @Override
            public void onOtaUpdateClick(Device device, ItemDeviceBinding itemBinding) {
                viewModel.checkAndTriggerOtaUpdate(device.getDeviceId());
            }
        });
        binding.rvDevicesList.setAdapter(adapter);
    }

    private void observeViewModel() {
        // Devices
        viewModel.getDevices().observe(getViewLifecycleOwner(), list -> {
            if (list == null || list.isEmpty()) {
                binding.llEmptyDevices.setVisibility(View.VISIBLE);
                binding.rvDevicesList.setVisibility(View.GONE);
                adapter.setDevices(null);
            } else {
                binding.llEmptyDevices.setVisibility(View.GONE);
                binding.rvDevicesList.setVisibility(View.VISIBLE);
                adapter.setDevices(list);
            }
        });

        // Action states (Registering/Unregistering alerts)
        viewModel.getDeviceActionState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            if (state.getStatus() == DevicesViewModel.DeviceActionState.Status.LOADING) {
                showLoadingDialog("Processing device configuration...");
            } else {
                hideLoadingDialog();
                if (state.getStatus() == DevicesViewModel.DeviceActionState.Status.SUCCESS) {
                    Toast.makeText(getContext(), state.getMessage(), Toast.LENGTH_SHORT).show();
                    viewModel.clearActionState();
                } else if (state.getStatus() == DevicesViewModel.DeviceActionState.Status.ERROR) {
                    Toast.makeText(getContext(), state.getMessage(), Toast.LENGTH_LONG).show();
                    viewModel.clearActionState();
                }
            }
        });

        // Wi-Fi provisioning states
        viewModel.getProvisioningState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;

            if (state.getStatus() == DevicesViewModel.ProvisioningState.Status.SEARCHING) {
                showLoadingDialog("Searching for device Access Point (SoftAP)...");
            } else if (state.getStatus() == DevicesViewModel.ProvisioningState.Status.CONNECTING) {
                showLoadingDialog("Connected. Transmitting credentials...");
            } else if (state.getStatus() == DevicesViewModel.ProvisioningState.Status.SUCCESS) {
                hideLoadingDialog();
                Toast.makeText(getContext(), "Wi-Fi credentials saved. Device is rebooting.", Toast.LENGTH_LONG).show();
            } else if (state.getStatus() == DevicesViewModel.ProvisioningState.Status.ERROR) {
                hideLoadingDialog();
                Toast.makeText(getContext(), "Provisioning failed: " + state.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // OTA Map
        viewModel.getOtaState().observe(getViewLifecycleOwner(), map -> {
            adapter.setOtaStates(map);
        });
    }

    private void showRegisterDeviceDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle("Register Device");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final EditText etId = new EditText(getContext());
        etId.setHint("Hardware Device ID (e.g. ESP32_01)");
        etId.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(etId);

        final EditText etName = new EditText(getContext());
        etName.setHint("Device Display Name (e.g. Main Kitchen Tank)");
        etName.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(etName);

        builder.setView(layout);
        builder.setPositiveButton("Register", (dialog, which) -> {
            String id = etId.getText().toString().trim();
            String name = etName.getText().toString().trim();
            viewModel.registerDevice(id, name);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showRenameDeviceDialog(Device device) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle("Rename Device");

        final EditText etName = new EditText(getContext());
        etName.setText(device.getName());
        etName.setSelection(device.getName().length());
        builder.setView(etName);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            if (!name.isEmpty()) {
                viewModel.updateDeviceName(device.getDeviceId(), name);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showDeleteConfirmDialog(Device device) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Unregister Device")
                .setMessage("Are you sure you want to remove " + device.getName() + "? This will erase its real-time telemetry links.")
                .setPositiveButton("Unregister", (dialog, which) -> viewModel.unregisterDevice(device.getDeviceId()))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showWifiProvisionDialog(Device device) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle("Configure Wi-Fi");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final EditText etSsid = new EditText(getContext());
        etSsid.setHint("Wi-Fi Router SSID");
        layout.addView(etSsid);

        final EditText etPass = new EditText(getContext());
        etPass.setHint("Password");
        etPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etPass);

        builder.setView(layout);
        builder.setPositiveButton("Provision", (dialog, which) -> {
            String ssid = etSsid.getText().toString().trim();
            String pass = etPass.getText().toString().trim();
            viewModel.configureDeviceWifi(device.getDeviceId(), ssid, pass);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showLoadingDialog(String msg) {
        if (loadingDialog == null) {
            loadingDialog = new MaterialAlertDialogBuilder(requireContext())
                    .setMessage(msg)
                    .setCancelable(false)
                    .create();
        } else {
            loadingDialog.setMessage(msg);
        }
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    private void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideLoadingDialog();
        binding = null;
    }
}

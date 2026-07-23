package com.aquaguard.presentation.devices;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aquaguard.domain.model.Device;
import com.aquaguard.domain.repository.DeviceRepository;
import com.aquaguard.domain.repository.RepositoryCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class DevicesViewModel extends ViewModel {
    private final DeviceRepository deviceRepository;

    private final LiveData<List<Device>> devices;
    private final MutableLiveData<DeviceActionState> deviceActionState = new MutableLiveData<>(DeviceActionState.idle());
    private final MutableLiveData<ProvisioningState> provisioningState = new MutableLiveData<>(ProvisioningState.idle());
    private final MutableLiveData<Map<String, OtaState>> otaState = new MutableLiveData<>(new HashMap<>());

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Inject
    public DevicesViewModel(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
        this.devices = deviceRepository.getDevices();
    }

    public LiveData<List<Device>> getDevices() { return devices; }
    public LiveData<DeviceActionState> getDeviceActionState() { return deviceActionState; }
    public LiveData<ProvisioningState> getProvisioningState() { return provisioningState; }
    public LiveData<Map<String, OtaState>> getOtaState() { return otaState; }

    public void registerDevice(String deviceId, String deviceName) {
        if (deviceId == null || deviceId.trim().isEmpty() || deviceName == null || deviceName.trim().isEmpty()) {
            deviceActionState.setValue(DeviceActionState.error("Device ID and Name cannot be empty"));
            return;
        }

        deviceActionState.setValue(DeviceActionState.loading());
        deviceRepository.registerDevice(deviceId, deviceName, new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                deviceActionState.postValue(DeviceActionState.success("Device registered successfully"));
            }

            @Override
            public void onFailure(Exception e) {
                deviceActionState.postValue(DeviceActionState.error(e.getMessage() != null ? e.getMessage() : "Failed to register device"));
            }
        });
    }

    public void unregisterDevice(String deviceId) {
        deviceActionState.setValue(DeviceActionState.loading());
        deviceRepository.unregisterDevice(deviceId, new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                deviceActionState.postValue(DeviceActionState.success("Device unregistered successfully"));
            }

            @Override
            public void onFailure(Exception e) {
                deviceActionState.postValue(DeviceActionState.error(e.getMessage() != null ? e.getMessage() : "Failed to unregister device"));
            }
        });
    }

    public void updateDeviceName(String deviceId, String name) {
        deviceRepository.updateDeviceName(deviceId, name, new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {}
            @Override
            public void onFailure(Exception e) {}
        });
    }

    public void configureDeviceWifi(String deviceId, String ssid, String pass) {
        provisioningState.setValue(ProvisioningState.searching());
        handler.postDelayed(() -> {
            provisioningState.setValue(ProvisioningState.connecting());
            handler.postDelayed(() -> {
                provisioningState.setValue(ProvisioningState.success());
                handler.postDelayed(() -> provisioningState.setValue(ProvisioningState.idle()), 1500);
            }, 2500);
        }, 2000);
    }

    public void checkAndTriggerOtaUpdate(String deviceId) {
        setOtaStateForDevice(deviceId, OtaState.checking());

        handler.postDelayed(() -> {
            setOtaStateForDevice(deviceId, OtaState.downloading(0.1f));
            simulateOtaDownload(deviceId, 1);
        }, 2000);
    }

    private void simulateOtaDownload(String deviceId, final int step) {
        if (step <= 10) {
            handler.postDelayed(() -> {
                setOtaStateForDevice(deviceId, OtaState.downloading(step / 10f));
                simulateOtaDownload(deviceId, step + 1);
            }, 400);
        } else {
            setOtaStateForDevice(deviceId, OtaState.installing());
            handler.postDelayed(() -> {
                setOtaStateForDevice(deviceId, OtaState.success());
                handler.postDelayed(() -> {
                    Map<String, OtaState> currentMap = new HashMap<>(otaState.getValue() != null ? otaState.getValue() : new HashMap<>());
                    currentMap.remove(deviceId);
                    otaState.setValue(currentMap);
                }, 1500);
            }, 1500);
        }
    }

    private void setOtaStateForDevice(String deviceId, OtaState state) {
        Map<String, OtaState> currentMap = new HashMap<>(otaState.getValue() != null ? otaState.getValue() : new HashMap<>());
        currentMap.put(deviceId, state);
        otaState.setValue(currentMap);
    }

    public void clearActionState() {
        deviceActionState.setValue(DeviceActionState.idle());
    }

    // Custom State Classes
    public static class DeviceActionState {
        public enum Status { IDLE, LOADING, SUCCESS, ERROR }
        private final Status status;
        private final String message;

        private DeviceActionState(Status status, String message) {
            this.status = status;
            this.message = message;
        }

        public static DeviceActionState idle() { return new DeviceActionState(Status.IDLE, ""); }
        public static DeviceActionState loading() { return new DeviceActionState(Status.LOADING, ""); }
        public static DeviceActionState success(String msg) { return new DeviceActionState(Status.SUCCESS, msg); }
        public static DeviceActionState error(String err) { return new DeviceActionState(Status.ERROR, err); }

        public Status getStatus() { return status; }
        public String getMessage() { return message; }
    }

    public static class ProvisioningState {
        public enum Status { IDLE, SEARCHING, CONNECTING, SUCCESS, ERROR }
        private final Status status;
        private final String message;

        private ProvisioningState(Status status, String message) {
            this.status = status;
            this.message = message;
        }

        public static ProvisioningState idle() { return new ProvisioningState(Status.IDLE, ""); }
        public static ProvisioningState searching() { return new ProvisioningState(Status.SEARCHING, ""); }
        public static ProvisioningState connecting() { return new ProvisioningState(Status.CONNECTING, ""); }
        public static ProvisioningState success() { return new ProvisioningState(Status.SUCCESS, ""); }
        public static ProvisioningState error(String err) { return new ProvisioningState(Status.ERROR, err); }

        public Status getStatus() { return status; }
        public String getMessage() { return message; }
    }

    public static class OtaState {
        public enum Status { CHECKING, DOWNLOADING, INSTALLING, SUCCESS, ERROR }
        private final Status status;
        private final float progress;
        private final String error;

        private OtaState(Status status, float progress, String error) {
            this.status = status;
            this.progress = progress;
            this.error = error;
        }

        public static OtaState checking() { return new OtaState(Status.CHECKING, 0f, ""); }
        public static OtaState downloading(float progress) { return new OtaState(Status.DOWNLOADING, progress, ""); }
        public static OtaState installing() { return new OtaState(Status.INSTALLING, 0f, ""); }
        public static OtaState success() { return new OtaState(Status.SUCCESS, 0f, ""); }
        public static OtaState error(String err) { return new OtaState(Status.ERROR, 0f, err); }

        public Status getStatus() { return status; }
        public float getProgress() { return progress; }
        public String getError() { return error; }
    }
}

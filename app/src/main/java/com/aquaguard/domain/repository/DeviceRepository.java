package com.aquaguard.domain.repository;

import androidx.lifecycle.LiveData;
import com.aquaguard.domain.model.Device;
import java.util.List;

public interface DeviceRepository {
    LiveData<List<Device>> getDevices();
    void registerDevice(String deviceId, String deviceName, RepositoryCallback<Void> callback);
    void unregisterDevice(String deviceId, RepositoryCallback<Void> callback);
    void updateDeviceName(String deviceId, String name, RepositoryCallback<Void> callback);
}

package com.aquaguard.presentation.alerts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aquaguard.domain.model.Alert;
import com.aquaguard.domain.model.Device;
import com.aquaguard.domain.repository.AlertRepository;
import com.aquaguard.domain.repository.DeviceRepository;
import com.aquaguard.domain.repository.RepositoryCallback;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AlertsViewModel extends ViewModel {
    private final DeviceRepository deviceRepository;
    private final AlertRepository alertRepository;

    private final LiveData<List<Device>> devices;
    private final MutableLiveData<String> selectedDeviceId = new MutableLiveData<>(null);
    private final MediatorLiveData<List<Alert>> activeAlerts = new MediatorLiveData<>();
    private final MediatorLiveData<List<Alert>> alertHistory = new MediatorLiveData<>();

    private LiveData<List<Alert>> currentActiveAlertsSource = null;
    private LiveData<List<Alert>> currentAlertHistorySource = null;

    @Inject
    public AlertsViewModel(DeviceRepository deviceRepository, AlertRepository alertRepository) {
        this.deviceRepository = deviceRepository;
        this.alertRepository = alertRepository;

        this.devices = deviceRepository.getDevices();

        // Select first device by default
        this.activeAlerts.addSource(this.devices, list -> {
            if (selectedDeviceId.getValue() == null && list != null && !list.isEmpty()) {
                selectDevice(list.get(0).getDeviceId());
            }
        });

        // Switch source on device change
        this.activeAlerts.addSource(selectedDeviceId, id -> {
            if (currentActiveAlertsSource != null) {
                activeAlerts.removeSource(currentActiveAlertsSource);
            }
            if (id != null) {
                currentActiveAlertsSource = alertRepository.getActiveAlerts(id);
                activeAlerts.addSource(currentActiveAlertsSource, activeAlerts::setValue);
            } else {
                currentActiveAlertsSource = null;
                activeAlerts.setValue(new ArrayList<>());
            }
        });

        this.alertHistory.addSource(selectedDeviceId, id -> {
            if (currentAlertHistorySource != null) {
                alertHistory.removeSource(currentAlertHistorySource);
            }
            if (id != null) {
                currentAlertHistorySource = alertRepository.getAlertHistory(id);
                alertHistory.addSource(currentAlertHistorySource, alertHistory::setValue);
            } else {
                currentAlertHistorySource = null;
                alertHistory.setValue(new ArrayList<>());
            }
        });
    }

    public LiveData<List<Device>> getDevices() {
        return devices;
    }

    public LiveData<String> getSelectedDeviceId() {
        return selectedDeviceId;
    }

    public LiveData<List<Alert>> getActiveAlerts() {
        return activeAlerts;
    }

    public LiveData<List<Alert>> getAlertHistory() {
        return alertHistory;
    }

    public void selectDevice(String deviceId) {
        selectedDeviceId.setValue(deviceId);
    }

    public void resolveAlert(String alertId) {
        alertRepository.resolveAlert(alertId, new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                // Done
            }

            @Override
            public void onFailure(Exception e) {
                // Handle failure
            }
        });
    }
}

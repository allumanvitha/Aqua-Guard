package com.aquaguard.presentation.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.Transformations;

import com.aquaguard.domain.model.Device;
import com.aquaguard.domain.model.WaterReading;
import com.aquaguard.domain.repository.DeviceRepository;
import com.aquaguard.domain.repository.WaterRepository;
import com.aquaguard.domain.repository.RepositoryCallback;
import com.aquaguard.domain.usecase.GetLiveDeviceStatusUseCase;
import com.aquaguard.domain.usecase.ToggleValveUseCase;

import java.util.List;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class DashboardViewModel extends ViewModel {
    private final DeviceRepository deviceRepository;
    private final GetLiveDeviceStatusUseCase getLiveDeviceStatusUseCase;
    private final ToggleValveUseCase toggleValveUseCase;
    private final WaterRepository waterRepository;

    private final LiveData<List<Device>> devices;
    private final MutableLiveData<String> selectedDeviceId = new MutableLiveData<>(null);
    private final MediatorLiveData<WaterReading> liveReading = new MediatorLiveData<>();
    private LiveData<WaterReading> currentLiveReadingSource = null;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    @Inject
    public DashboardViewModel(
            DeviceRepository deviceRepository,
            GetLiveDeviceStatusUseCase getLiveDeviceStatusUseCase,
            ToggleValveUseCase toggleValveUseCase,
            WaterRepository waterRepository
    ) {
        this.deviceRepository = deviceRepository;
        this.getLiveDeviceStatusUseCase = getLiveDeviceStatusUseCase;
        this.toggleValveUseCase = toggleValveUseCase;
        this.waterRepository = waterRepository;

        this.devices = deviceRepository.getDevices();

        // Automatically select the first device when the list is populated and nothing is selected
        this.liveReading.addSource(this.devices, deviceList -> {
            if (selectedDeviceId.getValue() == null && deviceList != null && !deviceList.isEmpty()) {
                selectDevice(deviceList.get(0).getDeviceId());
            }
        });

        // Listen for selection changes and switch the live status stream source
        this.liveReading.addSource(this.selectedDeviceId, id -> {
            if (currentLiveReadingSource != null) {
                this.liveReading.removeSource(currentLiveReadingSource);
            }
            if (id != null) {
                currentLiveReadingSource = getLiveDeviceStatusUseCase.execute(id);
                this.liveReading.addSource(currentLiveReadingSource, this.liveReading::setValue);
            } else {
                currentLiveReadingSource = null;
                this.liveReading.setValue(null);
            }
        });
    }

    public LiveData<List<Device>> getDevices() {
        return devices;
    }

    public LiveData<String> getSelectedDeviceId() {
        return selectedDeviceId;
    }

    public LiveData<WaterReading> getLiveReading() {
        return liveReading;
    }

    public void selectDevice(String deviceId) {
        selectedDeviceId.setValue(deviceId);
    }

    public void toggleValve(boolean open) {
        String id = selectedDeviceId.getValue();
        if (id == null) return;
        toggleValveUseCase.execute(id, open, "Manual toggle via App", new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                // Done
            }

            @Override
            public void onFailure(Exception e) {
                // Log or handle error
            }
        });
    }

    public void toggleAutoMode(boolean enabled) {
        String id = selectedDeviceId.getValue();
        if (id == null) return;
        waterRepository.toggleAutoMode(id, enabled, new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                // Done
            }

            @Override
            public void onFailure(Exception e) {
                // Handle error
            }
        });
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }

    public void setAutoMode(boolean enabled) {
        toggleAutoMode(enabled);
    }

    public LiveData<Integer> getWaterLevel() {
        return Transformations.map(liveReading, reading -> reading != null ? reading.getWaterLevelPct() : 0);
    }

    public LiveData<Float> getFlowRate() {
        return Transformations.map(liveReading, reading -> reading != null ? reading.getFlowRate() : 0f);
    }

    public LiveData<Boolean> isValveOpen() {
        return Transformations.map(liveReading, reading -> reading != null ? reading.isValveOpen() : false);
    }

    public LiveData<Boolean> isAutoModeEnabled() {
        return Transformations.map(liveReading, reading -> reading != null ? reading.isAutoMode() : false);
    }

    public LiveData<Boolean> isLeakDetected() {
        return Transformations.map(liveReading, reading -> reading != null ? reading.isLeakDetected() : false);
    }

    public LiveData<Long> getLastSyncTime() {
        return Transformations.map(liveReading, reading -> reading != null ? reading.getLastSeen() : 0L);
    }
}

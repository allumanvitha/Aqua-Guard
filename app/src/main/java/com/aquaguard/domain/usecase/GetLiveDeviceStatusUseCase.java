package com.aquaguard.domain.usecase;

import androidx.lifecycle.LiveData;

import com.aquaguard.domain.model.WaterReading;
import com.aquaguard.domain.repository.WaterRepository;

import javax.inject.Inject;

public class GetLiveDeviceStatusUseCase {
    private final WaterRepository waterRepository;

    @Inject
    public GetLiveDeviceStatusUseCase(WaterRepository waterRepository) {
        this.waterRepository = waterRepository;
    }

    public LiveData<WaterReading> execute(String deviceId) {
        return waterRepository.getLiveReading(deviceId);
    }
}

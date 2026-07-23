package com.aquaguard.domain.usecase;

import com.aquaguard.domain.repository.WaterRepository;
import com.aquaguard.domain.repository.RepositoryCallback;

import javax.inject.Inject;

public class ToggleValveUseCase {
    private final WaterRepository waterRepository;

    @Inject
    public ToggleValveUseCase(WaterRepository waterRepository) {
        this.waterRepository = waterRepository;
    }

    public void execute(String deviceId, boolean open, String reason, RepositoryCallback<Void> callback) {
        waterRepository.toggleValve(deviceId, open, "USER", reason, callback);
    }
}

package com.aquaguard.domain.repository;

import androidx.lifecycle.LiveData;
import com.aquaguard.domain.model.WaterReading;
import java.util.Map;

public interface WaterRepository {
    LiveData<WaterReading> getLiveReading(String deviceId);
    void toggleValve(String deviceId, boolean open, String triggeredBy, String reason, RepositoryCallback<Void> callback);
    void toggleAutoMode(String deviceId, boolean enabled, RepositoryCallback<Void> callback);
    LiveData<Map<String, Float>> getDailyUsage(String deviceId);
    LiveData<Map<String, Float>> getMonthlyUsage(String deviceId);
    void recordUsage(String deviceId, float liters, float waterSaved, RepositoryCallback<Void> callback);
}

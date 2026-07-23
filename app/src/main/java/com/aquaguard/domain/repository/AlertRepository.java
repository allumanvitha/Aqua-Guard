package com.aquaguard.domain.repository;

import androidx.lifecycle.LiveData;
import com.aquaguard.domain.model.Alert;
import java.util.List;

public interface AlertRepository {
    LiveData<List<Alert>> getActiveAlerts(String deviceId);
    LiveData<List<Alert>> getAlertHistory(String deviceId);
    void createAlert(Alert alert, RepositoryCallback<Void> callback);
    void resolveAlert(String alertId, RepositoryCallback<Void> callback);
}

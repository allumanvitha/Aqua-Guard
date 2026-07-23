package com.aquaguard.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.aquaguard.data.local.dao.AlertDao;
import com.aquaguard.data.local.entity.AlertEntity;
import com.aquaguard.domain.model.Alert;
import com.aquaguard.domain.repository.AlertRepository;
import com.aquaguard.domain.repository.RepositoryCallback;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AlertRepositoryImpl implements AlertRepository {
    private final FirebaseFirestore firestore;
    private final AlertDao alertDao;
    private final Executor dbExecutor = Executors.newSingleThreadExecutor();

    @Inject
    public AlertRepositoryImpl(FirebaseFirestore firestore, AlertDao alertDao) {
        this.firestore = firestore;
        this.alertDao = alertDao;
    }

    @Override
    public LiveData<List<Alert>> getActiveAlerts(String deviceId) {
        MediatorLiveData<List<Alert>> mediator = new MediatorLiveData<>();
        LiveData<List<AlertEntity>> cacheSource = alertDao.getActiveAlerts(deviceId);

        mediator.addSource(cacheSource, cachedEntities -> {
            mediator.setValue(toDomainList(cachedEntities));
        });

        // Trigger remote sync
        firestore.collection("alerts")
                .whereEqualTo("deviceId", deviceId)
                .whereEqualTo("resolved", false)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<AlertEntity> entities = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        entities.add(toEntity(doc, deviceId));
                    }
                    if (!entities.isEmpty()) {
                        dbExecutor.execute(() -> alertDao.insertAll(entities));
                    }
                })
                .addOnFailureListener(e -> {
                    // Fail silently
                });

        return mediator;
    }

    @Override
    public LiveData<List<Alert>> getAlertHistory(String deviceId) {
        MediatorLiveData<List<Alert>> mediator = new MediatorLiveData<>();
        LiveData<List<AlertEntity>> cacheSource = alertDao.getAlertHistory(deviceId);

        mediator.addSource(cacheSource, cachedEntities -> {
            mediator.setValue(toDomainList(cachedEntities));
        });

        // Trigger remote sync
        firestore.collection("alerts")
                .whereEqualTo("deviceId", deviceId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<AlertEntity> entities = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        entities.add(toEntity(doc, deviceId));
                    }
                    if (!entities.isEmpty()) {
                        dbExecutor.execute(() -> alertDao.insertAll(entities));
                    }
                })
                .addOnFailureListener(e -> {
                    // Fail silently
                });

        return mediator;
    }

    @Override
    public void createAlert(Alert alert, RepositoryCallback<Void> callback) {
        Map<String, Object> alertMap = new HashMap<>();
        alertMap.put("deviceId", alert.getDeviceId());
        alertMap.put("type", alert.getType().name());
        alertMap.put("severity", alert.getSeverity().name());
        alertMap.put("message", alert.getMessage());
        alertMap.put("timestamp", alert.getTimestamp());
        alertMap.put("resolved", alert.isResolved());

        firestore.collection("alerts").document().set(alertMap)
                .addOnSuccessListener(aVoid -> {
                    dbExecutor.execute(() -> alertDao.insertAlert(new AlertEntity(
                            alert.getAlertId(),
                            alert.getDeviceId(),
                            alert.getType().name(),
                            alert.getSeverity().name(),
                            alert.getMessage(),
                            alert.getTimestamp(),
                            alert.isResolved()
                    )));
                    callback.onSuccess(null);
                })
                .addOnFailureListener(callback::onFailure);
    }

    @Override
    public void resolveAlert(String alertId, RepositoryCallback<Void> callback) {
        firestore.collection("alerts").document(alertId)
                .update("resolved", true)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }

    private List<Alert> toDomainList(List<AlertEntity> entities) {
        List<Alert> domainList = new ArrayList<>();
        if (entities != null) {
            for (AlertEntity entity : entities) {
                Alert.AlertType type;
                try {
                    type = Alert.AlertType.valueOf(entity.getType());
                } catch (Exception e) {
                    type = Alert.AlertType.SENSOR_FAILURE;
                }

                Alert.AlertSeverity severity;
                try {
                    severity = Alert.AlertSeverity.valueOf(entity.getSeverity());
                } catch (Exception e) {
                    severity = Alert.AlertSeverity.INFO;
                }

                domainList.add(new Alert(
                        entity.getAlertId(),
                        entity.getDeviceId(),
                        type,
                        severity,
                        entity.getMessage(),
                        entity.getTimestamp(),
                        entity.isResolved()
                ));
            }
        }
        return domainList;
    }

    private AlertEntity toEntity(DocumentSnapshot doc, String deviceId) {
        String type = doc.getString("type");
        String severity = doc.getString("severity");
        String message = doc.getString("message");
        Long timestampVal = doc.getLong("timestamp");
        long timestamp = timestampVal != null ? timestampVal : 0L;
        Boolean resolvedVal = doc.getBoolean("resolved");
        boolean resolved = resolvedVal != null ? resolvedVal : false;

        return new AlertEntity(
                doc.getId(),
                deviceId,
                type != null ? type : Alert.AlertType.SENSOR_FAILURE.name(),
                severity != null ? severity : Alert.AlertSeverity.INFO.name(),
                message != null ? message : "",
                timestamp,
                resolved
        );
    }
}

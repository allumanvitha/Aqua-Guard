package com.aquaguard.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.aquaguard.data.local.dao.UsageDao;
import com.aquaguard.data.local.entity.UsageEntity;
import com.aquaguard.domain.model.WaterReading;
import com.aquaguard.domain.repository.WaterRepository;
import com.aquaguard.domain.repository.RepositoryCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WaterRepositoryImpl implements WaterRepository {
    private final FirebaseDatabase firebaseDatabase;
    private final FirebaseFirestore firestore;
    private final UsageDao usageDao;
    private final Executor dbExecutor = Executors.newSingleThreadExecutor();

    @Inject
    public WaterRepositoryImpl(FirebaseDatabase firebaseDatabase, FirebaseFirestore firestore, UsageDao usageDao) {
        this.firebaseDatabase = firebaseDatabase;
        this.firestore = firestore;
        this.usageDao = usageDao;
    }

    @Override
    public LiveData<WaterReading> getLiveReading(String deviceId) {
        return new LiveReadingLiveData(firebaseDatabase.getReference("devices/" + deviceId + "/live_status"));
    }

    @Override
    public void toggleValve(String deviceId, boolean open, String triggeredBy, String reason, RepositoryCallback<Void> callback) {
        firebaseDatabase.getReference("devices/" + deviceId + "/live_status/valve_open")
                .setValue(open)
                .addOnSuccessListener(aVoid -> {
                    Map<String, Object> logMap = new HashMap<>();
                    logMap.put("deviceId", deviceId);
                    logMap.put("triggeredBy", triggeredBy);
                    logMap.put("action", open ? "OPEN" : "CLOSE");
                    logMap.put("reason", reason);
                    logMap.put("timestamp", System.currentTimeMillis());

                    firestore.collection("valve_logs").document().set(logMap)
                            .addOnSuccessListener(aVoid2 -> callback.onSuccess(null))
                            .addOnFailureListener(callback::onFailure);
                })
                .addOnFailureListener(callback::onFailure);
    }

    @Override
    public void toggleAutoMode(String deviceId, boolean enabled, RepositoryCallback<Void> callback) {
        firebaseDatabase.getReference("devices/" + deviceId + "/live_status/auto_mode")
                .setValue(enabled)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }

    @Override
    public LiveData<Map<String, Float>> getDailyUsage(String deviceId) {
        MediatorLiveData<Map<String, Float>> mediator = new MediatorLiveData<>();
        LiveData<List<UsageEntity>> cacheSource = usageDao.getUsageHistory(deviceId);

        mediator.addSource(cacheSource, cachedList -> {
            Map<String, Float> usageMap = new HashMap<>();
            if (cachedList != null) {
                for (UsageEntity entity : cachedList) {
                    usageMap.put(entity.getDate(), entity.getTotalLiters());
                }
            }
            mediator.setValue(usageMap);
        });

        // Trigger remote sync
        firestore.collection("water_usage")
                .whereEqualTo("deviceId", deviceId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<UsageEntity> entities = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        String date = doc.getString("date");
                        Double total = doc.getDouble("totalLiters");
                        Double saved = doc.getDouble("waterSavedLiters");

                        if (date != null && !date.isEmpty()) {
                            float totalVal = total != null ? total.floatValue() : 0f;
                            float savedVal = saved != null ? saved.floatValue() : 0f;
                            entities.add(new UsageEntity(
                                    deviceId + "_" + date,
                                    deviceId,
                                    date,
                                    totalVal,
                                    savedVal
                            ));
                        }
                    }
                    if (!entities.isEmpty()) {
                        dbExecutor.execute(() -> usageDao.insertAll(entities));
                    }
                })
                .addOnFailureListener(e -> {
                    // Fail silently, cache value is already emitted
                });

        return mediator;
    }

    @Override
    public LiveData<Map<String, Float>> getMonthlyUsage(String deviceId) {
        MediatorLiveData<Map<String, Float>> mediator = new MediatorLiveData<>();
        LiveData<Map<String, Float>> dailySource = getDailyUsage(deviceId);

        mediator.addSource(dailySource, dailyMap -> {
            Map<String, Float> monthlyMap = new HashMap<>();
            if (dailyMap != null) {
                for (Map.Entry<String, Float> entry : dailyMap.entrySet()) {
                    String date = entry.getKey();
                    float liters = entry.getValue();
                    String month = date.length() >= 7 ? date.substring(0, 7) : "Unknown";
                    float currentSum = monthlyMap.containsKey(month) ? monthlyMap.get(month) : 0f;
                    monthlyMap.put(month, currentSum + liters);
                }
            }
            mediator.setValue(monthlyMap);
        });

        return mediator;
    }

    @Override
    public void recordUsage(String deviceId, float liters, float waterSaved, RepositoryCallback<Void> callback) {
        String dateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String docId = deviceId + "_" + dateStr;

        Map<String, Object> usageMap = new HashMap<>();
        usageMap.put("deviceId", deviceId);
        usageMap.put("date", dateStr);
        usageMap.put("totalLiters", liters);
        usageMap.put("waterSavedLiters", waterSaved);

        firestore.collection("water_usage").document(docId).set(usageMap)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }

    // Custom LiveData subclass to manage Realtime Database ValueEventListener
    private static class LiveReadingLiveData extends LiveData<WaterReading> {
        private final DatabaseReference ref;
        private final ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    postValue(null);
                    return;
                }
                Float flowRateVal = snapshot.child("flow_rate").getValue(Float.class);
                float flowRate = flowRateVal != null ? flowRateVal : 0f;

                Integer waterLevelVal = snapshot.child("water_level_pct").getValue(Integer.class);
                int waterLevelPct = waterLevelVal != null ? waterLevelVal : 0;

                Boolean leakDetectedVal = snapshot.child("leak_detected").getValue(Boolean.class);
                boolean leakDetected = leakDetectedVal != null ? leakDetectedVal : false;

                Boolean valveOpenVal = snapshot.child("valve_open").getValue(Boolean.class);
                boolean valveOpen = valveOpenVal != null ? valveOpenVal : false;

                Boolean autoModeVal = snapshot.child("auto_mode").getValue(Boolean.class);
                boolean autoMode = autoModeVal != null ? autoModeVal : false;

                Long lastSeenVal = snapshot.child("last_seen").getValue(Long.class);
                long lastSeen = lastSeenVal != null ? lastSeenVal : 0L;

                postValue(new WaterReading(flowRate, waterLevelPct, leakDetected, valveOpen, autoMode, lastSeen));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Ignore cancel errors
            }
        };

        public LiveReadingLiveData(DatabaseReference ref) {
            this.ref = ref;
        }

        @Override
        protected void onActive() {
            super.onActive();
            ref.addValueEventListener(listener);
        }

        @Override
        protected void onInactive() {
            super.onInactive();
            ref.removeEventListener(listener);
        }
    }
}

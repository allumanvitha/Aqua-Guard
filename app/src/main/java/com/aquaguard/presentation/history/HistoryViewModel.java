package com.aquaguard.presentation.history;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aquaguard.domain.model.Device;
import com.aquaguard.domain.model.ValveLog;
import com.aquaguard.domain.repository.DeviceRepository;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HistoryViewModel extends ViewModel {
    private final DeviceRepository deviceRepository;
    private final FirebaseFirestore firestore;

    private final LiveData<List<Device>> devices;
    private final MutableLiveData<String> selectedDeviceId = new MutableLiveData<>(null);
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final MutableLiveData<String> selectedTriggerFilter = new MutableLiveData<>("ALL");

    private final MediatorLiveData<List<ValveLog>> valveLogs = new MediatorLiveData<>();
    private ListenerRegistration logsListenerRegistration = null;
    private List<ValveLog> rawLogsList = new ArrayList<>();

    @Inject
    public HistoryViewModel(DeviceRepository deviceRepository, FirebaseFirestore firestore) {
        this.deviceRepository = deviceRepository;
        this.firestore = firestore;

        this.devices = deviceRepository.getDevices();

        // Select first device by default
        this.valveLogs.addSource(this.devices, list -> {
            if (selectedDeviceId.getValue() == null && list != null && !list.isEmpty()) {
                selectDevice(list.get(0).getDeviceId());
            }
        });

        // Whenever device changes, setup firestore listener
        this.valveLogs.addSource(selectedDeviceId, id -> {
            cleanupLogsListener();
            if (id != null) {
                logsListenerRegistration = firestore.collection("valve_logs")
                        .whereEqualTo("deviceId", id)
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .addSnapshotListener((snapshot, error) -> {
                            if (error != null) {
                                rawLogsList = new ArrayList<>();
                                filterAndPostLogs();
                                return;
                            }
                            List<ValveLog> list = new ArrayList<>();
                            if (snapshot != null) {
                                for (DocumentSnapshot doc : snapshot.getDocuments()) {
                                    list.add(new ValveLog(
                                            doc.getId(),
                                            id,
                                            doc.getString("triggeredBy") != null ? doc.getString("triggeredBy") : "",
                                            doc.getString("action") != null ? doc.getString("action") : "",
                                            doc.getString("reason") != null ? doc.getString("reason") : "",
                                            doc.getLong("timestamp") != null ? doc.getLong("timestamp") : 0L
                                    ));
                                }
                            }
                            rawLogsList = list;
                            filterAndPostLogs();
                        });
            } else {
                rawLogsList = new ArrayList<>();
                filterAndPostLogs();
            }
        });

        // Trigger filters when search query changes
        this.valveLogs.addSource(searchQuery, q -> filterAndPostLogs());

        // Trigger filters when trigger filter changes
        this.valveLogs.addSource(selectedTriggerFilter, f -> filterAndPostLogs());
    }

    private void filterAndPostLogs() {
        List<ValveLog> filtered = new ArrayList<>();
        String query = searchQuery.getValue() != null ? searchQuery.getValue().toLowerCase().trim() : "";
        String filter = selectedTriggerFilter.getValue() != null ? selectedTriggerFilter.getValue() : "ALL";

        for (ValveLog log : rawLogsList) {
            // Apply trigger type filter
            boolean isSystemMatch = filter.equals("SYSTEM") && log.getTriggeredBy().toUpperCase().startsWith("SYSTEM");
            if (!filter.equals("ALL") && !log.getTriggeredBy().equalsIgnoreCase(filter) && !isSystemMatch) {
                continue;
            }
            // Apply query text search
            if (!query.isEmpty()) {
                boolean matchesReason = log.getReason().toLowerCase().contains(query);
                boolean matchesAction = log.getAction().toLowerCase().contains(query);
                boolean matchesTrigger = log.getTriggeredBy().toLowerCase().contains(query);
                if (!matchesReason && !matchesAction && !matchesTrigger) {
                    continue;
                }
            }
            filtered.add(log);
        }
        valveLogs.setValue(filtered);
    }

    private void cleanupLogsListener() {
        if (logsListenerRegistration != null) {
            logsListenerRegistration.remove();
            logsListenerRegistration = null;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        cleanupLogsListener();
    }

    public LiveData<List<Device>> getDevices() { return devices; }
    public LiveData<String> getSelectedDeviceId() { return selectedDeviceId; }
    public LiveData<String> getSearchQuery() { return searchQuery; }
    public LiveData<String> getSelectedTriggerFilter() { return selectedTriggerFilter; }
    public LiveData<List<ValveLog>> getValveLogs() { return valveLogs; }

    public void selectDevice(String deviceId) {
        selectedDeviceId.setValue(deviceId);
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public void setTriggerFilter(String filter) {
        selectedTriggerFilter.setValue(filter);
    }
}

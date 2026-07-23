package com.aquaguard.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.aquaguard.domain.model.Device;
import com.aquaguard.domain.repository.DeviceRepository;
import com.aquaguard.domain.repository.RepositoryCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DeviceRepositoryImpl implements DeviceRepository {
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;
    private final MutableLiveData<List<Device>> devicesLiveData = new MutableLiveData<>();
    private ListenerRegistration devicesListenerRegistration;

    @Inject
    public DeviceRepositoryImpl(FirebaseAuth firebaseAuth, FirebaseFirestore firestore) {
        this.firebaseAuth = firebaseAuth;
        this.firestore = firestore;

        // Automatically setup devices listener when authentication state changes
        this.firebaseAuth.addAuthStateListener(auth -> {
            if (auth.getCurrentUser() != null) {
                setupDevicesListener(auth.getCurrentUser().getUid());
            } else {
                cleanupDevicesListener();
                devicesLiveData.postValue(new ArrayList<>());
            }
        });
    }

    private void setupDevicesListener(String uid) {
        cleanupDevicesListener();
        devicesListenerRegistration = firestore.collection("devices")
                .whereEqualTo("ownerUid", uid)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        return;
                    }
                    List<Device> devices = new ArrayList<>();
                    if (snapshot != null) {
                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            Long registeredAtVal = doc.getLong("registeredAt");
                            long registeredAt = registeredAtVal != null ? registeredAtVal : 0L;

                            Long batteryLevelVal = doc.getLong("batteryLevel");
                            int batteryLevel = batteryLevelVal != null ? batteryLevelVal.intValue() : 100;

                            devices.add(new Device(
                                    doc.getId(),
                                    doc.getString("deviceName") != null ? doc.getString("deviceName") : "Aqua Guard Device",
                                    doc.getString("ownerUid") != null ? doc.getString("ownerUid") : "",
                                    registeredAt,
                                    doc.getString("firmwareVersion") != null ? doc.getString("firmwareVersion") : "v1.0.0",
                                    doc.getString("status") != null ? doc.getString("status") : "offline",
                                    batteryLevel
                            ));
                        }
                    }
                    devicesLiveData.postValue(devices);
                });
    }

    private void cleanupDevicesListener() {
        if (devicesListenerRegistration != null) {
            devicesListenerRegistration.remove();
            devicesListenerRegistration = null;
        }
    }

    @Override
    public LiveData<List<Device>> getDevices() {
        return devicesLiveData;
    }

    @Override
    public void registerDevice(String deviceId, String deviceName, RepositoryCallback<Void> callback) {
        String uid = firebaseAuth.getCurrentUser() != null ? firebaseAuth.getCurrentUser().getUid() : null;
        if (uid == null) {
            callback.onFailure(new Exception("User not authenticated"));
            return;
        }

        Map<String, Object> deviceMap = new HashMap<>();
        deviceMap.put("deviceName", deviceName);
        deviceMap.put("ownerUid", uid);
        deviceMap.put("registeredAt", System.currentTimeMillis());
        deviceMap.put("firmwareVersion", "v1.0.0");
        deviceMap.put("status", "online");
        deviceMap.put("batteryLevel", 100);

        firestore.collection("devices").document(deviceId).set(deviceMap)
                .addOnSuccessListener(aVoid -> {
                    firestore.collection("users").document(uid)
                            .update("connectedDevices", FieldValue.arrayUnion(deviceId))
                            .addOnSuccessListener(aVoid2 -> callback.onSuccess(null))
                            .addOnFailureListener(callback::onFailure);
                })
                .addOnFailureListener(callback::onFailure);
    }

    @Override
    public void unregisterDevice(String deviceId, RepositoryCallback<Void> callback) {
        String uid = firebaseAuth.getCurrentUser() != null ? firebaseAuth.getCurrentUser().getUid() : null;
        if (uid == null) {
            callback.onFailure(new Exception("User not authenticated"));
            return;
        }

        firestore.collection("devices").document(deviceId).delete()
                .addOnSuccessListener(aVoid -> {
                    firestore.collection("users").document(uid)
                            .update("connectedDevices", FieldValue.arrayRemove(deviceId))
                            .addOnSuccessListener(aVoid2 -> callback.onSuccess(null))
                            .addOnFailureListener(callback::onFailure);
                })
                .addOnFailureListener(callback::onFailure);
    }

    @Override
    public void updateDeviceName(String deviceId, String name, RepositoryCallback<Void> callback) {
        firestore.collection("devices").document(deviceId)
                .update("deviceName", name)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }
}

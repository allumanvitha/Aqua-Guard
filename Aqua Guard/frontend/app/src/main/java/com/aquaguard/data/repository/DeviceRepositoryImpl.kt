package com.aquaguard.data.repository

import com.aquaguard.domain.model.Device
import com.aquaguard.domain.repository.DeviceRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : DeviceRepository {

    override fun getDevices(): Flow<List<Device>> = callbackFlow {
        val uid = firebaseAuth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("devices")
            .whereEqualTo("ownerUid", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val devices = snapshot?.documents?.mapNotNull { doc ->
                    Device(
                        deviceId = doc.id,
                        deviceName = doc.getString("deviceName") ?: "Aqua Guard Device",
                        ownerUid = doc.getString("ownerUid").orEmpty(),
                        registeredAt = doc.getLong("registeredAt") ?: 0L,
                        firmwareVersion = doc.getString("firmwareVersion") ?: "v1.0.0",
                        status = doc.getString("status") ?: "offline",
                        batteryLevel = doc.getLong("batteryLevel")?.toInt() ?: 100
                    )
                } ?: emptyList()
                
                trySend(devices)
            }
        
        awaitClose { listener.remove() }
    }

    override suspend fun registerDevice(deviceId: String, deviceName: String): Result<Unit> = runCatching {
        val uid = firebaseAuth.currentUser?.uid ?: throw Exception("Not authenticated")
        
        val deviceMap = mapOf(
            "deviceName" to deviceName,
            "ownerUid" to uid,
            "registeredAt" to System.currentTimeMillis(),
            "firmwareVersion" to "v1.0.0",
            "status" to "online",
            "batteryLevel" to 100
        )
        
        firestore.collection("devices").document(deviceId).set(deviceMap).await()
        
        firestore.collection("users").document(uid)
            .update("connectedDevices", FieldValue.arrayUnion(deviceId))
            .await()
    }

    override suspend fun unregisterDevice(deviceId: String): Result<Unit> = runCatching {
        val uid = firebaseAuth.currentUser?.uid ?: throw Exception("Not authenticated")
        
        firestore.collection("devices").document(deviceId).delete().await()
        
        firestore.collection("users").document(uid)
            .update("connectedDevices", FieldValue.arrayRemove(deviceId))
            .await()
    }

    override suspend fun updateDeviceName(deviceId: String, name: String): Result<Unit> = runCatching {
        firestore.collection("devices").document(deviceId)
            .update("deviceName", name)
            .await()
    }
}

package com.aquaguard.data.repository

import com.aquaguard.data.local.dao.UsageDao
import com.aquaguard.data.local.entity.UsageEntity
import com.aquaguard.domain.model.WaterReading
import com.aquaguard.domain.repository.WaterRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WaterRepositoryImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val firestore: FirebaseFirestore,
    private val usageDao: UsageDao
) : WaterRepository {

    override fun getLiveReading(deviceId: String): Flow<WaterReading?> = callbackFlow {
        val ref = firebaseDatabase.getReference("devices/$deviceId/live_status")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    trySend(null)
                    return
                }
                val flowRate = snapshot.child("flow_rate").getValue(Float::class.java) ?: 0f
                val waterLevelPct = snapshot.child("water_level_pct").getValue(Int::class.java) ?: 0
                val leakDetected = snapshot.child("leak_detected").getValue(Boolean::class.java) ?: false
                val valveOpen = snapshot.child("valve_open").getValue(Boolean::class.java) ?: false
                val autoMode = snapshot.child("auto_mode").getValue(Boolean::class.java) ?: false
                val lastSeen = snapshot.child("last_seen").getValue(Long::class.java) ?: 0L
                
                trySend(
                    WaterReading(
                        flowRate = flowRate,
                        waterLevelPct = waterLevelPct,
                        leakDetected = leakDetected,
                        valveOpen = valveOpen,
                        autoMode = autoMode,
                        lastSeen = lastSeen
                    )
                )
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override suspend fun toggleValve(
        deviceId: String,
        open: Boolean,
        triggeredBy: String,
        reason: String
    ): Result<Unit> = runCatching {
        // 1. Update Realtime Database
        firebaseDatabase.getReference("devices/$deviceId/live_status/valve_open")
            .setValue(open)
            .await()
        
        // 2. Log action to Firestore
        val logMap = mapOf(
            "deviceId" to deviceId,
            "triggeredBy" to triggeredBy,
            "action" to (if (open) "OPEN" else "CLOSE"),
            "reason" to reason,
            "timestamp" to System.currentTimeMillis()
        )
        firestore.collection("valve_logs").document().set(logMap).await()
    }

    override suspend fun toggleAutoMode(deviceId: String, enabled: Boolean): Result<Unit> = runCatching {
        firebaseDatabase.getReference("devices/$deviceId/live_status/auto_mode")
            .setValue(enabled)
            .await()
    }

    override fun getDailyUsage(deviceId: String): Flow<Map<String, Float>> = flow {
        // First emit from local cache
        usageDao.getUsageHistory(deviceId).collect { cachedList ->
            val cachedMap = cachedList.associate { it.date to it.totalLiters }
            emit(cachedMap)
            
            // Fetch from network and update cache
            try {
                val snapshot = firestore.collection("water_usage")
                    .whereEqualTo("deviceId", deviceId)
                    .get()
                    .await()
                
                val usageMap = mutableMapOf<String, Float>()
                val entitiesToCache = snapshot.documents.mapNotNull { doc ->
                    val date = doc.getString("date").orEmpty()
                    val totalLiters = doc.getDouble("totalLiters")?.toFloat() ?: 0f
                    val waterSaved = doc.getDouble("waterSavedLiters")?.toFloat() ?: 0f
                    if (date.isNotEmpty()) {
                        usageMap[date] = totalLiters
                        UsageEntity(
                            id = "${deviceId}_$date",
                            deviceId = deviceId,
                            date = date,
                            totalLiters = totalLiters,
                            waterSavedLiters = waterSaved
                        )
                    } else null
                }
                
                // Save to cache
                if (entitiesToCache.isNotEmpty()) {
                    usageDao.insertAll(entitiesToCache)
                }
                emit(usageMap)
            } catch (e: Exception) {
                // Ignore network errors, local cache is already emitted
            }
        }
    }

    override fun getMonthlyUsage(deviceId: String): Flow<Map<String, Float>> = flow {
        // Aggregate daily usage into months
        getDailyUsage(deviceId).collect { dailyMap ->
            val monthlyMap = mutableMapOf<String, Float>()
            dailyMap.forEach { (date, liters) ->
                // Date format is YYYY-MM-DD
                val month = if (date.length >= 7) date.substring(0, 7) else "Unknown"
                monthlyMap[month] = (monthlyMap[month] ?: 0f) + liters
            }
            emit(monthlyMap)
        }
    }

    override suspend fun recordUsage(deviceId: String, liters: Float, waterSaved: Float): Result<Unit> = runCatching {
        val dateStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        val docId = "${deviceId}_$dateStr"
        
        val usageMap = mapOf(
            "deviceId" to deviceId,
            "date" to dateStr,
            "totalLiters" to liters,
            "waterSavedLiters" to waterSaved
        )
        
        firestore.collection("water_usage").document(docId).set(usageMap).await()
    }
}

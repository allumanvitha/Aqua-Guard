package com.aquaguard.data.repository

import com.aquaguard.data.local.dao.AlertDao
import com.aquaguard.data.local.entity.AlertEntity
import com.aquaguard.domain.model.Alert
import com.aquaguard.domain.model.AlertSeverity
import com.aquaguard.domain.model.AlertType
import com.aquaguard.domain.repository.AlertRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlertRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val alertDao: AlertDao
) : AlertRepository {

    override fun getActiveAlerts(deviceId: String): Flow<List<Alert>> = flow {
        alertDao.getActiveAlerts(deviceId).collect { cachedList ->
            emit(cachedList.map { it.toDomain() })
            
            try {
                val snapshot = firestore.collection("alerts")
                    .whereEqualTo("deviceId", deviceId)
                    .whereEqualTo("resolved", false)
                    .get()
                    .await()
                
                val alerts = snapshot.documents.mapNotNull { doc ->
                    val typeStr = doc.getString("type").orEmpty()
                    val severityStr = doc.getString("severity").orEmpty()
                    
                    Alert(
                        alertId = doc.id,
                        deviceId = deviceId,
                        type = runCatching { AlertType.valueOf(typeStr) }.getOrDefault(AlertType.SENSOR_FAILURE),
                        severity = runCatching { AlertSeverity.valueOf(severityStr) }.getOrDefault(AlertSeverity.INFO),
                        message = doc.getString("message").orEmpty(),
                        timestamp = doc.getLong("timestamp") ?: 0L,
                        resolved = false
                    )
                }
                
                // Cache locally
                alertDao.insertAll(alerts.map { it.toEntity() })
                emit(alerts)
            } catch (e: Exception) {
                // Ignore network errors
            }
        }
    }

    override fun getAlertHistory(deviceId: String): Flow<List<Alert>> = flow {
        alertDao.getAlertHistory(deviceId).collect { cachedList ->
            emit(cachedList.map { it.toDomain() })
            
            try {
                val snapshot = firestore.collection("alerts")
                    .whereEqualTo("deviceId", deviceId)
                    .get()
                    .await()
                
                val alerts = snapshot.documents.mapNotNull { doc ->
                    val typeStr = doc.getString("type").orEmpty()
                    val severityStr = doc.getString("severity").orEmpty()
                    
                    Alert(
                        alertId = doc.id,
                        deviceId = deviceId,
                        type = runCatching { AlertType.valueOf(typeStr) }.getOrDefault(AlertType.SENSOR_FAILURE),
                        severity = runCatching { AlertSeverity.valueOf(severityStr) }.getOrDefault(AlertSeverity.INFO),
                        message = doc.getString("message").orEmpty(),
                        timestamp = doc.getLong("timestamp") ?: 0L,
                        resolved = doc.getBoolean("resolved") ?: false
                    )
                }
                
                // Cache locally
                alertDao.insertAll(alerts.map { it.toEntity() })
                emit(alerts)
            } catch (e: Exception) {
                // Ignore network errors
            }
        }
    }

    override suspend fun createAlert(alert: Alert): Result<Unit> = runCatching {
        val alertMap = mapOf(
            "deviceId" to alert.deviceId,
            "type" to alert.type.name,
            "severity" to alert.severity.name,
            "message" to alert.message,
            "timestamp" to alert.timestamp,
            "resolved" to alert.resolved
        )
        val ref = firestore.collection("alerts").document()
        ref.set(alertMap).await()
        
        // Save to local cache
        alertDao.insertAlert(alert.copy(alertId = ref.id).toEntity())
    }

    override suspend fun resolveAlert(alertId: String): Result<Unit> = runCatching {
        firestore.collection("alerts").document(alertId)
            .update("resolved", true)
            .await()
    }

    // Mapper helper functions
    private fun AlertEntity.toDomain() = Alert(
        alertId = alertId,
        deviceId = deviceId,
        type = runCatching { AlertType.valueOf(type) }.getOrDefault(AlertType.SENSOR_FAILURE),
        severity = runCatching { AlertSeverity.valueOf(severity) }.getOrDefault(AlertSeverity.INFO),
        message = message,
        timestamp = timestamp,
        resolved = resolved
    )

    private fun Alert.toEntity() = AlertEntity(
        alertId = alertId,
        deviceId = deviceId,
        type = type.name,
        severity = severity.name,
        message = message,
        timestamp = timestamp,
        resolved = resolved
    )
}

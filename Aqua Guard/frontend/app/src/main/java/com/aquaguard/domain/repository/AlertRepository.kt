package com.aquaguard.domain.repository

import com.aquaguard.domain.model.Alert
import kotlinx.coroutines.flow.Flow

interface AlertRepository {
    fun getActiveAlerts(deviceId: String): Flow<List<Alert>>
    fun getAlertHistory(deviceId: String): Flow<List<Alert>>
    suspend fun createAlert(alert: Alert): Result<Unit>
    suspend fun resolveAlert(alertId: String): Result<Unit>
}

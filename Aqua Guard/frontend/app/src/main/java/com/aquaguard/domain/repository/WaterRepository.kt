package com.aquaguard.domain.repository

import com.aquaguard.domain.model.WaterReading
import kotlinx.coroutines.flow.Flow

interface WaterRepository {
    fun getLiveReading(deviceId: String): Flow<WaterReading?>
    suspend fun toggleValve(deviceId: String, open: Boolean, triggeredBy: String, reason: String): Result<Unit>
    suspend fun toggleAutoMode(deviceId: String, enabled: Boolean): Result<Unit>
    fun getDailyUsage(deviceId: String): Flow<Map<String, Float>>
    fun getMonthlyUsage(deviceId: String): Flow<Map<String, Float>>
    suspend fun recordUsage(deviceId: String, liters: Float, waterSaved: Float): Result<Unit>
}

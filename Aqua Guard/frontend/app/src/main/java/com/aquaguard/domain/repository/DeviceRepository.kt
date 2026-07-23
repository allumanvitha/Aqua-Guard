package com.aquaguard.domain.repository

import com.aquaguard.domain.model.Device
import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    fun getDevices(): Flow<List<Device>>
    suspend fun registerDevice(deviceId: String, deviceName: String): Result<Unit>
    suspend fun unregisterDevice(deviceId: String): Result<Unit>
    suspend fun updateDeviceName(deviceId: String, name: String): Result<Unit>
}

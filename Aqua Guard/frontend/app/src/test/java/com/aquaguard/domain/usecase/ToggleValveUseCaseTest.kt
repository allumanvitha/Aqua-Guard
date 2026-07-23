package com.aquaguard.domain.usecase

import com.aquaguard.domain.model.WaterReading
import com.aquaguard.domain.repository.WaterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ToggleValveUseCaseTest {

    // A simple Fake WaterRepository for unit testing
    private class FakeWaterRepository : WaterRepository {
        var lastValveState: Boolean = false
        var lastTriggeredBy: String = ""
        var lastReason: String = ""

        override fun getLiveReading(deviceId: String): Flow<WaterReading?> {
            return flowOf(
                WaterReading(
                    flowRate = 0f,
                    waterLevelPct = 50,
                    leakDetected = false,
                    valveOpen = lastValveState,
                    autoMode = true,
                    lastSeen = System.currentTimeMillis()
                )
            )
        }

        override suspend fun toggleValve(
            deviceId: String,
            open: Boolean,
            triggeredBy: String,
            reason: String
        ): Result<Unit> {
            lastValveState = open
            lastTriggeredBy = triggeredBy
            lastReason = reason
            return Result.success(Unit)
        }

        override suspend fun toggleAutoMode(deviceId: String, enabled: Boolean): Result<Unit> {
            return Result.success(Unit)
        }

        override fun getDailyUsage(deviceId: String): Flow<Map<String, Float>> {
            return flowOf(emptyMap())
        }

        override fun getMonthlyUsage(deviceId: String): Flow<Map<String, Float>> {
            return flowOf(emptyMap())
        }

        override suspend fun recordUsage(deviceId: String, liters: Float, waterSaved: Float): Result<Unit> {
            return Result.success(Unit)
        }
    }

    @Test
    fun `when valve is toggled, it updates repository state`() = runBlocking {
        // Given
        val fakeRepository = FakeWaterRepository()
        val toggleValveUseCase = ToggleValveUseCase(fakeRepository)

        // When
        val result = toggleValveUseCase("device_1", open = false, reason = "Manual shutoff")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(false, fakeRepository.lastValveState)
        assertEquals("USER", fakeRepository.lastTriggeredBy)
        assertEquals("Manual shutoff", fakeRepository.lastReason)
    }
}

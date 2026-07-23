package com.aquaguard.domain.usecase

import com.aquaguard.domain.repository.WaterRepository
import javax.inject.Inject

class ToggleValveUseCase @Inject constructor(
    private val waterRepository: WaterRepository
) {
    suspend operator fun invoke(deviceId: String, open: Boolean, reason: String): Result<Unit> {
        return waterRepository.toggleValve(deviceId, open, "USER", reason)
    }
}

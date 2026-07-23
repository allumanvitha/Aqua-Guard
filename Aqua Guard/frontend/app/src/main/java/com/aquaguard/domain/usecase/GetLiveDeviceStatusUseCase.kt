package com.aquaguard.domain.usecase

import com.aquaguard.domain.model.WaterReading
import com.aquaguard.domain.repository.WaterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLiveDeviceStatusUseCase @Inject constructor(
    private val waterRepository: WaterRepository
) {
    operator fun invoke(deviceId: String): Flow<WaterReading?> {
        return waterRepository.getLiveReading(deviceId)
    }
}

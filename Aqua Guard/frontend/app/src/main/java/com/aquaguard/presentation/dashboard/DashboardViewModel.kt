package com.aquaguard.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquaguard.domain.model.Device
import com.aquaguard.domain.model.WaterReading
import com.aquaguard.domain.repository.DeviceRepository
import com.aquaguard.domain.repository.WaterRepository
import com.aquaguard.domain.usecase.GetLiveDeviceStatusUseCase
import com.aquaguard.domain.usecase.ToggleValveUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val deviceRepository: DeviceRepository,
    private val getLiveDeviceStatusUseCase: GetLiveDeviceStatusUseCase,
    private val toggleValveUseCase: ToggleValveUseCase,
    private val waterRepository: WaterRepository
) : ViewModel() {

    val devices: StateFlow<List<Device>> = deviceRepository.getDevices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedDeviceId = MutableStateFlow<String?>(null)
    val selectedDeviceId = _selectedDeviceId.asStateFlow()

    val liveReading: StateFlow<WaterReading?> = _selectedDeviceId
        .flatMapLatest { id ->
            if (id != null) getLiveDeviceStatusUseCase(id) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        viewModelScope.launch {
            devices.collect { list ->
                if (_selectedDeviceId.value == null && list.isNotEmpty()) {
                    _selectedDeviceId.value = list.first().deviceId
                }
            }
        }
    }

    fun selectDevice(deviceId: String) {
        _selectedDeviceId.value = deviceId
    }

    fun toggleValve(open: Boolean) {
        val id = _selectedDeviceId.value ?: return
        viewModelScope.launch {
            toggleValveUseCase(id, open, "Manual toggle via App")
        }
    }

    fun toggleAutoMode(enabled: Boolean) {
        val id = _selectedDeviceId.value ?: return
        viewModelScope.launch {
            waterRepository.toggleAutoMode(id, enabled)
        }
    }
}

package com.aquaguard.presentation.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquaguard.domain.model.Alert
import com.aquaguard.domain.model.Device
import com.aquaguard.domain.repository.AlertRepository
import com.aquaguard.domain.repository.DeviceRepository
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
class AlertsViewModel @Inject constructor(
    private val deviceRepository: DeviceRepository,
    private val alertRepository: AlertRepository
) : ViewModel() {

    val devices: StateFlow<List<Device>> = deviceRepository.getDevices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedDeviceId = MutableStateFlow<String?>(null)
    val selectedDeviceId = _selectedDeviceId.asStateFlow()

    val activeAlerts: StateFlow<List<Alert>> = _selectedDeviceId
        .flatMapLatest { id ->
            if (id != null) alertRepository.getActiveAlerts(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val alertHistory: StateFlow<List<Alert>> = _selectedDeviceId
        .flatMapLatest { id ->
            if (id != null) alertRepository.getAlertHistory(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

    fun resolveAlert(alertId: String) {
        viewModelScope.launch {
            alertRepository.resolveAlert(alertId)
        }
    }
}

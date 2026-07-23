package com.aquaguard.presentation.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquaguard.domain.model.Device
import com.aquaguard.domain.repository.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DevicesViewModel @Inject constructor(
    private val deviceRepository: DeviceRepository
) : ViewModel() {

    val devices: StateFlow<List<Device>> = deviceRepository.getDevices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _deviceActionState = MutableStateFlow<DeviceActionState>(DeviceActionState.Idle)
    val deviceActionState = _deviceActionState.asStateFlow()

    fun registerDevice(deviceId: String, deviceName: String) {
        if (deviceId.isBlank() || deviceName.isBlank()) {
            _deviceActionState.value = DeviceActionState.Error("Device ID and Name cannot be empty")
            return
        }
        viewModelScope.launch {
            _deviceActionState.value = DeviceActionState.Loading
            deviceRepository.registerDevice(deviceId, deviceName)
                .onSuccess {
                    _deviceActionState.value = DeviceActionState.Success("Device registered successfully")
                }
                .onFailure {
                    _deviceActionState.value = DeviceActionState.Error(it.message ?: "Failed to register device")
                }
        }
    }

    fun unregisterDevice(deviceId: String) {
        viewModelScope.launch {
            _deviceActionState.value = DeviceActionState.Loading
            deviceRepository.unregisterDevice(deviceId)
                .onSuccess {
                    _deviceActionState.value = DeviceActionState.Success("Device unregistered successfully")
                }
                .onFailure {
                    _deviceActionState.value = DeviceActionState.Error(it.message ?: "Failed to unregister device")
                }
        }
    }

    fun updateDeviceName(deviceId: String, name: String) {
        viewModelScope.launch {
            deviceRepository.updateDeviceName(deviceId, name)
        }
    }

    // Wi-Fi provisioning state
    private val _provisioningState = MutableStateFlow<ProvisioningState>(ProvisioningState.Idle)
    val provisioningState = _provisioningState.asStateFlow()

    fun configureDeviceWifi(deviceId: String, ssid: String, pass: String) {
        viewModelScope.launch {
            _provisioningState.value = ProvisioningState.Searching
            kotlinx.coroutines.delay(2000)
            _provisioningState.value = ProvisioningState.Connecting
            kotlinx.coroutines.delay(2500)
            _provisioningState.value = ProvisioningState.Success
            kotlinx.coroutines.delay(1500)
            _provisioningState.value = ProvisioningState.Idle
        }
    }

    // OTA status state
    private val _otaState = MutableStateFlow<Map<String, OtaState>>(emptyMap())
    val otaState = _otaState.asStateFlow()

    fun checkAndTriggerOtaUpdate(deviceId: String) {
        viewModelScope.launch {
            val currentMap = _otaState.value.toMutableMap()
            currentMap[deviceId] = OtaState.Checking
            _otaState.value = currentMap

            kotlinx.coroutines.delay(2000)
            currentMap[deviceId] = OtaState.Downloading(0.1f)
            _otaState.value = currentMap

            for (progress in 1..10) {
                kotlinx.coroutines.delay(400)
                currentMap[deviceId] = OtaState.Downloading(progress / 10f)
                _otaState.value = currentMap
            }

            currentMap[deviceId] = OtaState.Installing
            _otaState.value = currentMap
            kotlinx.coroutines.delay(1500)

            currentMap[deviceId] = OtaState.Success
            _otaState.value = currentMap
            kotlinx.coroutines.delay(1500)
            currentMap.remove(deviceId)
            _otaState.value = currentMap
        }
    }

    fun clearActionState() {
        _deviceActionState.value = DeviceActionState.Idle
    }
}

sealed interface DeviceActionState {
    object Idle : DeviceActionState
    object Loading : DeviceActionState
    data class Success(val message: String) : DeviceActionState
    data class Error(val error: String) : DeviceActionState
}

sealed interface ProvisioningState {
    object Idle : ProvisioningState
    object Searching : ProvisioningState
    object Connecting : ProvisioningState
    object Success : ProvisioningState
    data class Error(val message: String) : ProvisioningState
}

sealed interface OtaState {
    object Checking : OtaState
    data class Downloading(val progress: Float) : OtaState
    object Installing : OtaState
    object Success : OtaState
    data class Error(val error: String) : OtaState
}

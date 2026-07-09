package com.aquaguard.presentation.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquaguard.domain.model.Device
import com.aquaguard.domain.repository.DeviceRepository
import com.aquaguard.domain.repository.WaterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val deviceRepository: DeviceRepository,
    private val waterRepository: WaterRepository
) : ViewModel() {

    val devices: StateFlow<List<Device>> = deviceRepository.getDevices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedDeviceId = MutableStateFlow<String?>(null)
    val selectedDeviceId = _selectedDeviceId.asStateFlow()

    // Daily consumption (Date -> Liters)
    val dailyUsage: StateFlow<Map<String, Float>> = _selectedDeviceId
        .flatMapLatest { id ->
            if (id != null) waterRepository.getDailyUsage(id) else flowOf(emptyMap())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Monthly consumption (Month -> Liters)
    val monthlyUsage: StateFlow<Map<String, Float>> = _selectedDeviceId
        .flatMapLatest { id ->
            if (id != null) waterRepository.getMonthlyUsage(id) else flowOf(emptyMap())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Calculated metrics
    val totalWaterSaved: StateFlow<Float> = dailyUsage.map { usage ->
        // Assume savings is roughly 12% of total usage when Auto-Mode prevents overflow/leaks (mock calculation for demo)
        usage.values.sum() * 0.12f
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val averageConsumption: StateFlow<Float> = dailyUsage.map { usage ->
        if (usage.isEmpty()) 0f else usage.values.average().toFloat()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val estimatedBill: StateFlow<Float> = dailyUsage.map { usage ->
        // Bill rate: $0.005 per Liter
        usage.values.sum() * 0.005f
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    // AI Consumption Prediction for next 7 days (Day -> Liters)
    val aiConsumptionPrediction: StateFlow<List<Pair<String, Float>>> = averageConsumption.map { avg ->
        if (avg == 0f) {
            emptyList()
        } else {
            val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            days.mapIndexed { index, day ->
                // Simulate daily variations in future prediction using sine wave + average
                val factor = 1.0f + 0.15f * kotlin.math.sin(index.toFloat())
                day to (avg * factor)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // PDF Export Status
    private val _pdfState = MutableStateFlow<PdfExportState>(PdfExportState.Idle)
    val pdfState = _pdfState.asStateFlow()

    fun exportUsageReportAsPdf() {
        viewModelScope.launch {
            _pdfState.value = PdfExportState.Generating
            kotlinx.coroutines.delay(2000)
            _pdfState.value = PdfExportState.Success("AquaGuard_Usage_Report.pdf")
            kotlinx.coroutines.delay(2000)
            _pdfState.value = PdfExportState.Idle
        }
    }

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
}

sealed interface PdfExportState {
    object Idle : PdfExportState
    object Generating : PdfExportState
    data class Success(val fileName: String) : PdfExportState
    data class Error(val error: String) : PdfExportState
}

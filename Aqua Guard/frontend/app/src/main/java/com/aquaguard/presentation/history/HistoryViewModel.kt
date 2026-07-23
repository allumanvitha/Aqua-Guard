package com.aquaguard.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquaguard.domain.model.Device
import com.aquaguard.domain.model.ValveLog
import com.aquaguard.domain.repository.DeviceRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channels.awaitClose
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val deviceRepository: DeviceRepository,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    val devices: StateFlow<List<Device>> = deviceRepository.getDevices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedDeviceId = MutableStateFlow<String?>(null)
    val selectedDeviceId = _selectedDeviceId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedTriggerFilter = MutableStateFlow("ALL")
    val selectedTriggerFilter = _selectedTriggerFilter.asStateFlow()

    // Fetch valve operation logs from Firestore in real-time with search and filters
    val valveLogs: StateFlow<List<ValveLog>> = kotlinx.coroutines.flow.combine(
        _selectedDeviceId,
        _searchQuery,
        _selectedTriggerFilter
    ) { deviceId, query, filter ->
        Triple(deviceId, query, filter)
    }.flatMapLatest { (id, query, filter) ->
        if (id != null) {
            callbackFlow {
                val listener = firestore.collection("valve_logs")
                    .whereEqualTo("deviceId", id)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            trySend(emptyList())
                            return@addSnapshotListener
                        }
                        var logs = snapshot?.documents?.mapNotNull { doc ->
                            ValveLog(
                                logId = doc.id,
                                deviceId = id,
                                triggeredBy = doc.getString("triggeredBy").orEmpty(),
                                action = doc.getString("action").orEmpty(),
                                reason = doc.getString("reason").orEmpty(),
                                timestamp = doc.getLong("timestamp") ?: 0L
                            )
                        } ?: emptyList()

                        // Local query filter
                        if (query.isNotEmpty()) {
                            logs = logs.filter {
                                it.reason.contains(query, ignoreCase = true) ||
                                it.action.contains(query, ignoreCase = true) ||
                                it.triggeredBy.contains(query, ignoreCase = true)
                            }
                        }

                        // Local trigger filter
                        if (filter != "ALL") {
                            logs = logs.filter { it.triggeredBy.equals(filter, ignoreCase = true) }
                        }

                        trySend(logs)
                    }
                awaitClose { listener.remove() }
            }
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setTriggerFilter(filter: String) {
        _selectedTriggerFilter.value = filter
    }
}

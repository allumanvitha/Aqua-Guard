package com.aquaguard.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquaguard.domain.model.ValveLog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.ui.tooling.preview.Preview
import com.aquaguard.presentation.theme.AquaGuardTheme
import com.aquaguard.domain.model.Device

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel
) {
    val devices by viewModel.devices.collectAsState()
    val selectedDeviceId by viewModel.selectedDeviceId.collectAsState()
    val valveLogs by viewModel.valveLogs.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedTriggerFilter.collectAsState()

    HistoryScreenContent(
        devices = devices,
        selectedDeviceId = selectedDeviceId,
        valveLogs = valveLogs,
        searchQuery = searchQuery,
        selectedFilter = selectedFilter,
        onSelectDevice = { viewModel.selectDevice(it) },
        onSearchQueryChange = { viewModel.setSearchQuery(it) },
        onFilterChange = { viewModel.setTriggerFilter(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreenContent(
    devices: List<Device>,
    selectedDeviceId: String?,
    valveLogs: List<ValveLog>,
    searchQuery: String,
    selectedFilter: String,
    onSelectDevice: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onFilterChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Operation History",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Device Selector
        if (devices.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(devices) { device ->
                    val isSelected = device.deviceId == selectedDeviceId
                    InputChip(
                        selected = isSelected,
                        onClick = { onSelectDevice(device.deviceId) },
                        label = { Text(device.deviceName) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text("Search operations...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Filter chips (All, User, Auto)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("ALL" to "All Triggers", "USER" to "User Actions", "SYSTEM_AUTO" to "System Auto").forEach { (filterVal, label) ->
                val isSelected = selectedFilter == filterVal
                FilterChip(
                    selected = isSelected,
                    onClick = { onFilterChange(filterVal) },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (valveLogs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "No history",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No valve operations recorded.",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(valveLogs) { log ->
                    ValveLogItem(log = log)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    AquaGuardTheme {
        HistoryScreenContent(
            devices = listOf(
                Device("dev1", "Main Inlet", "user1", 1687957200000L, "v1.0.0", "online", 100)
            ),
            selectedDeviceId = "dev1",
            valveLogs = listOf(
                ValveLog("log1", "dev1", "USER", "CLOSE", "Manual shutoff via App", System.currentTimeMillis() - 300000),
                ValveLog("log2", "dev1", "SYSTEM_AUTO", "CLOSE", "Leakage detected!", System.currentTimeMillis() - 1200000),
                ValveLog("log3", "dev1", "USER", "OPEN", "Initial setup", System.currentTimeMillis() - 2400000)
            ),
            onSelectDevice = {}
        )
    }
}


@Composable
fun ValveLogItem(log: ValveLog) {
    val isOpenAction = log.action == "OPEN"
    val actionColor = if (isOpenAction) Color(0xFF4CAF50) else Color(0xFFE53935)
    val actionIcon = if (isOpenAction) Icons.Default.LockOpen else Icons.Default.Lock

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Action Icon Background circle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(actionColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = actionIcon,
                    contentDescription = log.action,
                    tint = actionColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Valve ${log.action.lowercase(Locale.getDefault())}ed",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = "Triggered by: ${log.triggeredBy}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                if (log.reason.isNotEmpty()) {
                    Text(
                        text = "Reason: ${log.reason}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Text(
                text = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()).format(Date(log.timestamp)),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                modifier = Modifier.align(Alignment.Top)
            )
        }
    }
}

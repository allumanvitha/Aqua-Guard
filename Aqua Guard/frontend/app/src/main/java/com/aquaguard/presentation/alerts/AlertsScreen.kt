package com.aquaguard.presentation.alerts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquaguard.domain.model.Alert
import com.aquaguard.domain.model.AlertSeverity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.ui.tooling.preview.Preview
import com.aquaguard.presentation.theme.AquaGuardTheme
import com.aquaguard.domain.model.AlertType

@Composable
fun AlertsScreen(
    viewModel: AlertsViewModel
) {
    val devices by viewModel.devices.collectAsState()
    val selectedDeviceId by viewModel.selectedDeviceId.collectAsState()
    val activeAlerts by viewModel.activeAlerts.collectAsState()
    val alertHistory by viewModel.alertHistory.collectAsState()

    AlertsScreenContent(
        devices = devices,
        selectedDeviceId = selectedDeviceId,
        activeAlerts = activeAlerts,
        alertHistory = alertHistory,
        onSelectDevice = { viewModel.selectDevice(it) },
        onResolveAlert = { viewModel.resolveAlert(it) }
    )
}

@Composable
fun AlertsScreenContent(
    devices: List<Device>,
    selectedDeviceId: String?,
    activeAlerts: List<Alert>,
    alertHistory: List<Alert>,
    onSelectDevice: (String) -> Unit,
    onResolveAlert: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Active", "History")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Smart Alerts",
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

        Spacer(modifier = Modifier.height(8.dp))

        // Tabs
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontWeight = FontWeight.Bold) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Alerts List
        val currentAlerts = if (selectedTab == 0) activeAlerts else alertHistory

        if (currentAlerts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "No alerts",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (selectedTab == 0) "No active alerts! Everything is secure." else "No alert history.",
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
                items(currentAlerts) { alert ->
                    AlertItem(
                        alert = alert,
                        showResolveButton = selectedTab == 0,
                        onResolve = { onResolveAlert(alert.alertId) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AlertsScreenPreview() {
    AquaGuardTheme {
        AlertsScreenContent(
            devices = listOf(
                Device("dev1", "Main Inlet", "user1", 1687957200000L, "v1.0.0", "online", 100)
            ),
            selectedDeviceId = "dev1",
            activeAlerts = listOf(
                Alert("alt1", "dev1", AlertType.LEAK_DETECTED, AlertSeverity.CRITICAL, "Critical water leakage detected in the main pipeline. Valve closed automatically.", System.currentTimeMillis() - 600000, false),
                Alert("alt2", "dev1", AlertType.EXCESSIVE_USAGE, AlertSeverity.WARNING, "Daily water target of 300L exceeded.", System.currentTimeMillis() - 1200000, false)
            ),
            alertHistory = emptyList(),
            onSelectDevice = {},
            onResolveAlert = {}
        )
    }
}


@Composable
fun AlertItem(
    alert: Alert,
    showResolveButton: Boolean,
    onResolve: () -> Unit
) {
    val severityColor = when (alert.severity) {
        AlertSeverity.CRITICAL -> MaterialTheme.colorScheme.error
        AlertSeverity.WARNING -> Color(0xFFF57C00) // Orange
        AlertSeverity.INFO -> MaterialTheme.colorScheme.primary
    }

    val cardBg = when (alert.severity) {
        AlertSeverity.CRITICAL -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(severityColor),
            width = if (alert.severity == AlertSeverity.CRITICAL) 2.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpacerBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (alert.severity == AlertSeverity.CRITICAL) Icons.Default.Warning else Icons.Default.Info,
                        contentDescription = "Alert Icon",
                        tint = severityColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = alert.type.name.replace("_", " "),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = severityColor
                    )
                }

                Text(
                    text = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()).format(Date(alert.timestamp)),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = alert.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            if (showResolveButton) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onResolve,
                    colors = ButtonDefaults.buttonColors(containerColor = severityColor),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Resolve", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Resolve", fontSize = 14.sp)
                }
            }
        }
    }
}

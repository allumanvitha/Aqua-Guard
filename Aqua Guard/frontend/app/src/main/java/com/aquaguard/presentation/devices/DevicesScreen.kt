package com.aquaguard.presentation.devices

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquaguard.domain.model.Device

import androidx.compose.ui.tooling.preview.Preview
import com.aquaguard.presentation.theme.AquaGuardTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicesScreen(
    viewModel: DevicesViewModel,
    onNavigateBack: () -> Unit
) {
    val devices by viewModel.devices.collectAsState()
    val actionState by viewModel.deviceActionState.collectAsState()
    val provisioningState by viewModel.provisioningState.collectAsState()
    val otaStates by viewModel.otaState.collectAsState()

    DevicesScreenContent(
        devices = devices,
        actionState = actionState,
        provisioningState = provisioningState,
        otaStates = otaStates,
        onRegisterDevice = { id, name -> viewModel.registerDevice(id, name) },
        onUnregisterDevice = { viewModel.unregisterDevice(it) },
        onUpdateDeviceName = { id, name -> viewModel.updateDeviceName(id, name) },
        onConfigureWifi = { id, ssid, pass -> viewModel.configureDeviceWifi(id, ssid, pass) },
        onTriggerOta = { id -> viewModel.checkAndTriggerOtaUpdate(id) },
        onClearActionState = { viewModel.clearActionState() },
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicesScreenContent(
    devices: List<Device>,
    actionState: DeviceActionState,
    provisioningState: ProvisioningState,
    otaStates: Map<String, OtaState>,
    onRegisterDevice: (String, String) -> Unit,
    onUnregisterDevice: (String) -> Unit,
    onUpdateDeviceName: (String, String) -> Unit,
    onConfigureWifi: (String, String, String) -> Unit,
    onTriggerOta: (String) -> Unit,
    onClearActionState: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var showRegisterDialog by remember { mutableStateOf(false) }
    var deviceIdInput by remember { mutableStateOf("") }
    var deviceNameInput by remember { mutableStateOf("") }

    var showRenameDialog by remember { mutableStateOf(false) }
    var renameDeviceId by remember { mutableStateOf("") }
    var renameDeviceNameInput by remember { mutableStateOf("") }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteDeviceId by remember { mutableStateOf("") }

    var showWifiDialog by remember { mutableStateOf(false) }
    var wifiDeviceId by remember { mutableStateOf("") }
    var wifiSsid by remember { mutableStateOf("") }
    var wifiPassword by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(actionState) {
        when (actionState) {
            is DeviceActionState.Success -> {
                snackbarHostState.showSnackbar(actionState.message)
                onClearActionState()
                showRegisterDialog = false
                deviceIdInput = ""
                deviceNameInput = ""
            }
            is DeviceActionState.Error -> {
                snackbarHostState.showSnackbar(actionState.error)
                onClearActionState()
            }
            else -> {}
        }
    }

    LaunchedEffect(provisioningState) {
        when (provisioningState) {
            is ProvisioningState.Success -> {
                snackbarHostState.showSnackbar("Wi-Fi successfully configured on device")
                showWifiDialog = false
                wifiSsid = ""
                wifiPassword = ""
            }
            is ProvisioningState.Error -> {
                snackbarHostState.showSnackbar(provisioningState.message)
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Device Management", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showRegisterDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Device")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (devices.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Router,
                        contentDescription = "No Devices",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        modifier = Modifier.size(96.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No Connected Devices",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Register your Aqua Guard hardware device to start monitoring your water usage and preventing leakages.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { showRegisterDialog = true }) {
                        Text("Add Aqua Guard Device")
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(devices) { device ->
                        DeviceCard(
                            device = device,
                            otaState = otaStates[device.deviceId],
                            onRename = { id, currentName ->
                                renameDeviceId = id
                                renameDeviceNameInput = currentName
                                showRenameDialog = true
                            },
                            onDelete = { id ->
                                deleteDeviceId = id
                                showDeleteDialog = true
                            },
                            onConfigureWifiClick = { id ->
                                wifiDeviceId = id
                                showWifiDialog = true
                            },
                            onTriggerOta = onTriggerOta
                        )
                    }
                }
            }

            if (showRegisterDialog) {
                AlertDialog(
                    onDismissRequest = { showRegisterDialog = false },
                    title = { Text("Register Aqua Guard Device") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = deviceIdInput,
                                onValueChange = { deviceIdInput = it },
                                label = { Text("Device ID (e.g. device_id_123)") },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = deviceNameInput,
                                onValueChange = { deviceNameInput = it },
                                label = { Text("Device Name (e.g. Main Tank)") },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { onRegisterDevice(deviceIdInput.trim(), deviceNameInput.trim()) },
                            enabled = deviceIdInput.isNotBlank() && deviceNameInput.isNotBlank() && actionState !is DeviceActionState.Loading
                        ) {
                            if (actionState is DeviceActionState.Loading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                            } else {
                                Text("Register")
                            }
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showRegisterDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            if (showRenameDialog) {
                AlertDialog(
                    onDismissRequest = { showRenameDialog = false },
                    title = { Text("Rename Device") },
                    text = {
                        OutlinedTextField(
                            value = renameDeviceNameInput,
                            onValueChange = { renameDeviceNameInput = it },
                            label = { Text("Device Name") },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                onUpdateDeviceName(renameDeviceId, renameDeviceNameInput.trim())
                                showRenameDialog = false
                            },
                            enabled = renameDeviceNameInput.isNotBlank()
                        ) {
                            Text("Save")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showRenameDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Unregister Device") },
                    text = { Text("Are you sure you want to unregister this device? You will no longer receive alerts or view live data for this unit.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                onUnregisterDevice(deleteDeviceId)
                                showDeleteDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Unregister")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            if (showWifiDialog) {
                AlertDialog(
                    onDismissRequest = { if (provisioningState !is ProvisioningState.Searching && provisioningState !is ProvisioningState.Connecting) showWifiDialog = false },
                    title = { Text("ESP32 Wi-Fi Provisioning") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            if (provisioningState is ProvisioningState.Idle) {
                                Text("Enter target Wi-Fi credentials. The app will pair via Bluetooth / SoftAP and transmit the settings.", fontSize = 14.sp)
                                OutlinedTextField(
                                    value = wifiSsid,
                                    onValueChange = { wifiSsid = it },
                                    label = { Text("SSID (Wi-Fi Name)") },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = wifiPassword,
                                    onValueChange = { wifiPassword = it },
                                    label = { Text("Password") },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                    Text(
                                        text = when (provisioningState) {
                                            is ProvisioningState.Searching -> "Searching for Aqua Guard BLE Device..."
                                            is ProvisioningState.Connecting -> "Transmitting network credentials..."
                                            else -> "Provisioning ESP32..."
                                        },
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {
                        if (provisioningState is ProvisioningState.Idle) {
                            Button(
                                onClick = { onConfigureWifi(wifiDeviceId, wifiSsid.trim(), wifiPassword.trim()) },
                                enabled = wifiSsid.isNotBlank()
                            ) {
                                Text("Provision")
                            }
                        }
                    },
                    dismissButton = {
                        if (provisioningState is ProvisioningState.Idle) {
                            TextButton(onClick = { showWifiDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DevicesScreenPreview() {
    AquaGuardTheme {
        DevicesScreenContent(
            devices = listOf(
                Device("dev1", "Main Inlet Valve", "user1", 1687957200000L, "v1.0.0", "online", 100),
                Device("dev2", "Rooftop Tank", "user1", 1687957200000L, "v1.0.0", "offline", 85)
            ),
            actionState = DeviceActionState.Idle,
            provisioningState = ProvisioningState.Idle,
            otaStates = emptyMap(),
            onRegisterDevice = { _, _ -> },
            onUnregisterDevice = {},
            onUpdateDeviceName = { _, _ -> },
            onConfigureWifi = { _, _, _ -> },
            onTriggerOta = {},
            onClearActionState = {},
            onNavigateBack = {}
        )
    }
}

@Composable
fun DeviceCard(
    device: Device,
    otaState: OtaState?,
    onRename: (String, String) -> Unit,
    onDelete: (String) -> Unit,
    onConfigureWifiClick: (String) -> Unit,
    onTriggerOta: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val isOnline = device.status == "online"
    val statusColor = if (isOnline) Color(0xFF4CAF50) else Color(0xFF757575)

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(statusColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = device.deviceName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row {
                    IconButton(onClick = { onRename(device.deviceId, device.deviceName) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Rename", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = { onDelete(device.deviceId) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Unregister", tint = MaterialTheme.colorScheme.error)
                    }
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Options"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "ID: ${device.deviceId}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.BatteryFull,
                        contentDescription = "Power Status",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Power: ${device.batteryLevel}% (AC)", style = MaterialTheme.typography.labelSmall)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SettingsSuggest,
                        contentDescription = "Firmware",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Firmware: ${device.firmwareVersion}", style = MaterialTheme.typography.labelSmall)
                }
            }

            if (expanded) {
                Divider(modifier = Modifier.padding(vertical = 12.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { onConfigureWifiClick(device.deviceId) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Wifi, contentDescription = "Wi-Fi Config", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Configure Wi-Fi", fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { onTriggerOta(device.deviceId) },
                            enabled = otaState == null && isOnline,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.SystemUpdateAlt, contentDescription = "OTA Update", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("OTA Update", fontSize = 12.sp)
                        }
                    }

                    // OTA Progress status representation
                    otaState?.let { state ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                when (state) {
                                    is OtaState.Checking -> {
                                        CircularProgressIndicator(modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Checking for server firmware...", fontSize = 12.sp)
                                    }
                                    is OtaState.Downloading -> {
                                        LinearProgressIndicator(
                                            progress = state.progress,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Downloading: ${(state.progress * 100).toInt()}%", fontSize = 12.sp)
                                    }
                                    is OtaState.Installing -> {
                                        CircularProgressIndicator(modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Installing firmware... Device will reboot.", fontSize = 12.sp)
                                    }
                                    is OtaState.Success -> {
                                        Icon(Icons.Default.CheckCircle, contentDescription = "Success", tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Firmware installed successfully!", fontSize = 12.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                                    }
                                    is OtaState.Error -> {
                                        Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Update failed: ${state.error}", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

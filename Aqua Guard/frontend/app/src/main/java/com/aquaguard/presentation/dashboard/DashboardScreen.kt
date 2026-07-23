package com.aquaguard.presentation.dashboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquaguard.domain.model.Device
import com.aquaguard.domain.model.WaterReading
import com.aquaguard.presentation.theme.AquaGuardTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import kotlin.math.sin
import kotlinx.coroutines.delay
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.withFrameNanos


@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToDevices: () -> Unit,
    onNavigateToAIAssistant: () -> Unit
) {
    val devices by viewModel.devices.collectAsState()
    val selectedDeviceId by viewModel.selectedDeviceId.collectAsState()
    val liveReading by viewModel.liveReading.collectAsState()

    DashboardScreenContent(
        devices = devices,
        selectedDeviceId = selectedDeviceId,
        liveReading = liveReading,
        onSelectDevice = { viewModel.selectDevice(it) },
        onToggleValve = { viewModel.toggleValve(it) },
        onToggleAutoMode = { viewModel.toggleAutoMode(it) },
        onNavigateToDevices = onNavigateToDevices,
        onNavigateToAIAssistant = onNavigateToAIAssistant
    )
}

@Composable
fun DashboardScreenContent(
    devices: List<Device>,
    selectedDeviceId: String?,
    liveReading: WaterReading?,
    onSelectDevice: (String) -> Unit,
    onToggleValve: (Boolean) -> Unit,
    onToggleAutoMode: (Boolean) -> Unit,
    onNavigateToDevices: () -> Unit,
    onNavigateToAIAssistant: () -> Unit
) {
    var showValveDialog by remember { mutableStateOf(false) }
    var pendingValveState by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Title & Add Device Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Aqua Guard",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateToAIAssistant) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "AI Assistant", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onNavigateToDevices) {
                    Icon(Icons.Default.Add, contentDescription = "Add Device", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Device Selection Row
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
                        label = { Text(device.deviceName) },
                        colors = InputChipDefaults.inputChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        } else {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToDevices() }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, contentDescription = "No Devices", tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("No Aqua Guard devices registered.", fontWeight = FontWeight.Bold)
                        Text("Tap here to set up your first device.", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedDeviceId != null && liveReading != null) {
            val reading = liveReading

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Water Level Indicator (With Wave Animation)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(220.dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                        .border(4.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape)
                ) {
                    WaterTankWave(
                        levelPercentage = reading.waterLevelPct,
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape)
                    )
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${reading.waterLevelPct}%",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (reading.waterLevelPct > 45) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Water Level",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (reading.waterLevelPct > 45) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Text(
                    text = "Last synced: " + SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date(reading.lastSeen)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )

                // 2. Quick Status Grid (Flow rate, Leak status)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Water, contentDescription = "Flow Rate", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Flow Rate", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            Text("${reading.flowRate} L/min", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    val leakColor = if (reading.leakDetected) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    val leakBgColor = if (reading.leakDetected) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = leakBgColor)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = if (reading.leakDetected) Icons.Default.Warning else Icons.Default.VerifiedUser,
                                contentDescription = "Leak Status",
                                tint = leakColor,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Leak Status", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            Text(
                                text = if (reading.leakDetected) "LEAK DETECTED" else "Secure",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = leakColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2c. Environment Diagnostic Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Temperature Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Thermostat, contentDescription = "Temperature", tint = Color(0xFFFF9800), modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Temperature", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            Text("24.5 °C", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Water Quality TDS Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.FilterAlt, contentDescription = "TDS Level", tint = Color(0xFF00BCD4), modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Water TDS", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            Text("185 PPM", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Connectivity status
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Wifi, contentDescription = "Signal Strength", tint = Color(0xFF4CAF50), modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Signal (RSSI)", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            Text("-68 dBm", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Device battery/power status
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.ElectricBolt, contentDescription = "Power Mode", tint = Color(0xFFFFEB3B), modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Power Source", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            Text("AC Power", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2b. Simulated Live Video Feed Card
                LiveCameraFeedCard(reading = reading)

                // 3. Valve Control Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpacerBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Solenoid Valve", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text(
                                    text = if (reading.valveOpen) "Valve is OPEN (Water Flowing)" else "Valve is CLOSED (Water Blocked)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (reading.valveOpen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                )
                            }
                            Switch(
                                checked = reading.valveOpen,
                                onCheckedChange = { targetState ->
                                    pendingValveState = targetState
                                    showValveDialog = true
                                }
                            )
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpacerBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Auto Protection Mode", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(
                                    "Closes valve automatically on leaks/overflows",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            Switch(
                                checked = reading.autoMode,
                                onCheckedChange = { onToggleAutoMode(it) }
                            )
                        }
                    }
                }
            }
        } else if (selectedDeviceId != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        if (showValveDialog) {
            AlertDialog(
                onDismissRequest = { showValveDialog = false },
                title = { Text("Confirm Valve Action") },
                text = {
                    Text(
                        if (pendingValveState)
                            "Are you sure you want to OPEN the solenoid valve? This will resume water flow."
                        else
                            "Are you sure you want to CLOSE the solenoid valve? This will stop all water flow immediately."
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onToggleValve(pendingValveState)
                            showValveDialog = false
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showValveDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun WaterTankWave(
    levelPercentage: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveOffset"
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val waterHeight = height * (1f - (levelPercentage / 100f))
        val wavePath = Path()
        val circlePath = Path().apply {
            addOval(rect = size.toRect())
        }

        clipPath(circlePath) {
            drawRect(
                color = Color(0xFF1976D2).copy(alpha = 0.1f),
                size = size
            )

            wavePath.moveTo(0f, waterHeight)
            val waveAmplitude = 15f
            val waveFrequency = 0.03f

            for (x in 0..width.toInt()) {
                val y = waterHeight + waveAmplitude * kotlin.math.sin((x * waveFrequency) + waveOffset)
                wavePath.lineTo(x.toFloat(), y)
            }

            wavePath.lineTo(width, height)
            wavePath.lineTo(0f, height)
            wavePath.close()

            drawPath(
                path = wavePath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF80DEEA),
                        Color(0xFF0288D1),
                        Color(0xFF01579B)
                    )
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    AquaGuardTheme {
        DashboardScreenContent(
            devices = listOf(
                Device("dev1", "Main Inlet", "user1", 1687957200000L, "v1.0.0", "online", 100),
                Device("dev2", "Backyard Tank", "user1", 1687957200000L, "v1.0.0", "offline", 80)
            ),
            selectedDeviceId = "dev1",
            liveReading = WaterReading(
                flowRate = 12.5f,
                waterLevelPct = 68,
                leakDetected = false,
                valveOpen = true,
                autoMode = true,
                lastSeen = System.currentTimeMillis()
            ),
            onSelectDevice = {},
            onToggleValve = {},
            onToggleAutoMode = {},
            onNavigateToDevices = {}
        )
    }
}

@Composable
fun LiveCameraFeedCard(
    reading: WaterReading,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var selectedCam by remember { mutableStateOf(1) } // 1: Tank, 2: Valve
    var filterMode by remember { mutableStateOf("normal") } // "normal", "nv", "thermal"
    
    // PTZ state
    var panX by remember { mutableStateOf(0f) }
    var panY by remember { mutableStateOf(0f) }
    var zoom by remember { mutableStateOf(1.0f) }

    // Running clock state
    var timeString by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        while(true) {
            timeString = sdf.format(Date())
            delay(1000)
        }
    }

    // Animation frames/tick
    var animTick by remember { mutableStateOf(0) }
    var staticIntensity by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while(true) {
            withFrameNanos { animTick++ }
        }
    }

    // Decay static noise
    if (staticIntensity > 0f) {
        LaunchedEffect(animTick) {
            staticIntensity = (staticIntensity - 0.05f).coerceAtLeast(0f)
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header row with Expand/Collapse toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Camera",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Live Video Monitoring",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = if (selectedCam == 1) "CAM-01 // TANK_CHAMBER_A" else "CAM-02 // INLET_VALVE_B",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand"
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Cam tabs & Filter tabs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Camera Selector Tabs
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    selectedCam = 1
                                    staticIntensity = 1.0f
                                },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedCam == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (selectedCam == 1) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("CAM-01: Tank", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Button(
                                onClick = {
                                    selectedCam = 2
                                    staticIntensity = 1.0f
                                },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedCam == 2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (selectedCam == 2) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("CAM-02: Valve", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        // Filter mode selection
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("normal" to "Normal", "nv" to "Night", "thermal" to "Thermal").forEach { (mode, label) ->
                                Button(
                                    onClick = {
                                        filterMode = mode
                                        staticIntensity = 0.5f
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (filterMode == mode) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        contentColor = if (filterMode == mode) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(26.dp)
                                ) {
                                    Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // CCTV Screen Container
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF05080C))
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    ) {
                        // Drawing Canvas
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val canvasWidth = size.width
                            val canvasHeight = size.height

                            withTransform({
                                translate(left = panX, top = panY)
                                scale(scaleX = zoom, scaleY = zoom, pivot = center)
                            }) {
                                // Draw Background Grids
                                val gridColor = Color.White.copy(alpha = 0.02f)
                                for (i in 0..canvasWidth.toInt() step 50) {
                                    drawLine(gridColor, start = Offset(i.toFloat(), 0f), end = Offset(i.toFloat(), canvasHeight))
                                }
                                for (i in 0..canvasHeight.toInt() step 50) {
                                    drawLine(gridColor, start = Offset(0f, i.toFloat()), end = Offset(canvasWidth, i.toFloat()))
                                }

                                if (selectedCam == 1) {
                                    // CAM-01: Draw Water Tank
                                    val tankWidth = 140f
                                    val tankHeight = 160f
                                    val tankLeft = (canvasWidth - tankWidth) / 2f
                                    val tankTop = (canvasHeight - tankHeight) / 2f + 10f
                                    val tankRight = tankLeft + tankWidth
                                    val tankBottom = tankTop + tankHeight

                                    // Pipe inlet
                                    val pipeColor = if (filterMode == "thermal") Color(0xFF311B92) else Color(0xFF1E2D3B)
                                    drawRect(
                                        color = pipeColor,
                                        topLeft = Offset(tankLeft - 50f, tankTop + 10f),
                                        size = Size(50f, 15f)
                                    )

                                    // Tank structure lines
                                    val structureColor = if (filterMode == "thermal") Color(0xFF1E88E5) else Color(0xFF4FC3F7).copy(alpha = 0.4f)
                                    // Left wall
                                    drawLine(structureColor, Offset(tankLeft, tankTop), Offset(tankLeft, tankBottom), strokeWidth = 3f)
                                    // Right wall
                                    drawLine(structureColor, Offset(tankRight, tankTop), Offset(tankRight, tankBottom), strokeWidth = 3f)
                                    // Bottom wall
                                    drawLine(structureColor, Offset(tankLeft, tankBottom), Offset(tankRight, tankBottom), strokeWidth = 4f)

                                    // Gradation ticks
                                    val labelColor = if (filterMode == "nv") Color(0xFF2EC471) else Color.White.copy(alpha = 0.4f)
                                    for (percent in listOf(25, 50, 75, 100)) {
                                        val gy = tankBottom - (tankHeight * (percent / 100f))
                                        drawLine(labelColor, Offset(tankLeft - 6f, gy), Offset(tankLeft, gy), strokeWidth = 1f)
                                    }

                                    // Draw Water Wave
                                    val waterLevelPct = reading.waterLevelPct
                                    if (waterLevelPct > 0) {
                                        val currentWaterHeight = tankHeight * (waterLevelPct / 100f)
                                        val waterY = tankBottom - currentWaterHeight

                                        val wavePath = Path()
                                        wavePath.moveTo(tankLeft + 2f, waterY)
                                        val waveAmplitude = 4f
                                        val waveFreq = 0.04f
                                        for (x in (tankLeft + 2f).toInt()..(tankRight - 2f).toInt()) {
                                            val y = waterY + waveAmplitude * sin((x * waveFreq) + (animTick * 0.06f))
                                            wavePath.lineTo(x.toFloat(), y)
                                        }
                                        wavePath.lineTo(tankRight - 2f, tankBottom - 2f)
                                        wavePath.lineTo(tankLeft + 2f, tankBottom - 2f)
                                        wavePath.close()

                                        val waterBrush = when (filterMode) {
                                            "thermal" -> Brush.verticalGradient(
                                                colors = listOf(Color(0xFF0D47A1), Color(0xFF000022))
                                            )
                                            "nv" -> Brush.verticalGradient(
                                                colors = listOf(Color(0xFF2EC471).copy(alpha = 0.4f), Color(0xFF2EC471).copy(alpha = 0.1f))
                                            )
                                            else -> Brush.verticalGradient(
                                                colors = listOf(Color(0xFF80DEEA).copy(alpha = 0.7f), Color(0xFF01579B).copy(alpha = 0.8f))
                                            )
                                        }

                                        drawPath(wavePath, brush = waterBrush)
                                    }

                                    // Draw Falling Droplets
                                    if (reading.valveOpen && reading.flowRate > 0) {
                                        val dropColor = if (filterMode == "thermal") Color(0xFFFF9100) else (if (filterMode == "nv") Color(0xFF2EC471) else Color(0xFF80DEEA))
                                        val dropletInletX = tankLeft - 10f
                                        val dropletInletY = tankTop + 17f
                                        val dropSpacing = 50f
                                        val dropSpeed = 4f
                                        val maxDropletY = tankBottom - (tankHeight * (reading.waterLevelPct / 100f))
                                        
                                        for (j in 0..3) {
                                            val dropY = dropletInletY + ((animTick * dropSpeed + j * dropSpacing) % (tankBottom - dropletInletY))
                                            if (dropY < maxDropletY) {
                                                drawCircle(
                                                    color = dropColor,
                                                    radius = 2.5f,
                                                    center = Offset(dropletInletX, dropY)
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    // CAM-02: Draw Flow Valve Piping
                                    val pipeY = canvasHeight / 2f
                                    val pipeHeight = 35f

                                    // Draw Pipes
                                    val pipeColor = if (filterMode == "thermal") Color(0xFF15152A) else Color(0xFF101D28)
                                    val pipeOutlineColor = if (filterMode == "thermal") Color(0xFF3F51B5) else Color(0xFF4FC3F7).copy(alpha = 0.3f)
                                    drawRect(
                                        color = pipeColor,
                                        topLeft = Offset(0f, pipeY - pipeHeight / 2f),
                                        size = Size(canvasWidth, pipeHeight)
                                    )
                                    drawLine(
                                        color = pipeOutlineColor,
                                        start = Offset(0f, pipeY - pipeHeight / 2f),
                                        end = Offset(canvasWidth, pipeY - pipeHeight / 2f),
                                        strokeWidth = 2f
                                    )
                                    drawLine(
                                        color = pipeOutlineColor,
                                        start = Offset(0f, pipeY + pipeHeight / 2f),
                                        end = Offset(canvasWidth, pipeY + pipeHeight / 2f),
                                        strokeWidth = 2f
                                    )

                                    // Valve body (Triangle shapes and square connector)
                                    val valveSize = 50f
                                    val valveLeft = canvasWidth / 2f - valveSize / 2f
                                    val valveRight = canvasWidth / 2f + valveSize / 2f

                                    val valvePath = Path().apply {
                                        moveTo(valveLeft, pipeY - 20f)
                                        lineTo(valveLeft, pipeY + 20f)
                                        lineTo(canvasWidth / 2f, pipeY)
                                        lineTo(valveRight, pipeY - 20f)
                                        lineTo(valveRight, pipeY + 20f)
                                        lineTo(canvasWidth / 2f, pipeY)
                                        close()
                                    }
                                    val valveBodyColor = if (filterMode == "thermal") Color(0xFF263238) else Color(0xFF223446)
                                    drawPath(
                                        path = valvePath,
                                        color = valveBodyColor
                                    )
                                    drawPath(
                                        path = valvePath,
                                        color = pipeOutlineColor,
                                        style = Stroke(width = 1.5f)
                                    )

                                    // Actuator coil box on top
                                    val coilColor = if (reading.valveOpen) {
                                        if (filterMode == "thermal") Color(0xFFFF3D00) else Color(0xFF4CAF50)
                                    } else {
                                        if (filterMode == "thermal") Color(0xFF0D47A1) else Color(0xFFE53935)
                                    }
                                    drawRect(
                                        color = coilColor,
                                        topLeft = Offset(canvasWidth / 2f - 15f, pipeY - 35f),
                                        size = Size(30f, 15f)
                                    )
                                    drawRect(
                                        color = pipeOutlineColor,
                                        topLeft = Offset(canvasWidth / 2f - 15f, pipeY - 35f),
                                        size = Size(30f, 15f),
                                        style = Stroke(width = 1f)
                                    )

                                    // Flow particles
                                    if (reading.valveOpen && reading.flowRate > 0) {
                                        val bubbleColor = if (filterMode == "thermal") Color(0xFFFFD600) else (if (filterMode == "nv") Color(0xFF2EC471) else Color(0xFF80DEEA))
                                        val pSpacing = 40f
                                        val pSpeed = 3f + (reading.flowRate * 0.15f)
                                        for (k in 0..12) {
                                            val px = (k * pSpacing + animTick * pSpeed) % (canvasWidth + pSpacing) - pSpacing
                                            // Draw bubble
                                            drawCircle(
                                                color = bubbleColor,
                                                radius = 2f,
                                                center = Offset(px, pipeY + (k % 3 - 1) * 6f)
                                            )
                                        }
                                    }
                                }
                            }

                            // Filter Tint Overlays
                            if (filterMode == "nv") {
                                // Green filter
                                drawRect(Color(0xFF4CAF50).copy(alpha = 0.12f))
                                // Radial spotlight overlay
                                drawRect(
                                    brush = Brush.radialGradient(
                                        colors = listOf(Color.White.copy(alpha = 0.05f), Color.Black.copy(alpha = 0.45f)),
                                        center = center,
                                        radius = size.maxDimension / 1.8f
                                    ),
                                    blendMode = BlendMode.Multiply
                                )
                            } else if (filterMode == "thermal") {
                                // Thermal false-color overlay
                                drawRect(Color(0xFF311B92).copy(alpha = 0.15f))
                            } else {
                                // Normal cctv tint
                                drawRect(Color(0xFF4FC3F7).copy(alpha = 0.04f))
                            }

                            // Horizontal CRT scanlines
                            for (y in 0..canvasHeight.toInt() step 5) {
                                drawLine(
                                    color = Color.Black.copy(alpha = 0.12f),
                                    start = Offset(0f, y.toFloat()),
                                    end = Offset(canvasWidth, y.toFloat()),
                                    strokeWidth = 1f
                                )
                            }

                            // Draw channel static noise
                            if (staticIntensity > 0f) {
                                val noiseAlpha = (staticIntensity * 0.4f).coerceAtMost(1f)
                                for (i in 0..(staticIntensity * 8).toInt()) {
                                    val noiseY = (0..canvasHeight.toInt()).random().toFloat()
                                    val noiseH = (4..18).random().toFloat()
                                    drawRect(
                                        color = Color.White.copy(alpha = noiseAlpha),
                                        topLeft = Offset(0f, noiseY),
                                        size = Size(canvasWidth, noiseH)
                                    )
                                }
                            }
                        }

                        // HUD Text Elements (Screen Overlay)
                        // Blinking REC Indicator
                        val isRecVisible = (animTick / 30) % 2 == 0
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(
                                        color = if (isRecVisible) Color(0xFFE53935) else Color.Transparent,
                                        shape = CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (filterMode) {
                                    "nv" -> "REC 1080P IR"
                                    "thermal" -> "REC 1080P FLIR"
                                    else -> "REC 1080P"
                                },
                                color = Color.White,
                                fontSize = 9.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }

                        // Signal Quality Info
                        Text(
                            text = "SIG: 92% // CELL_B2",
                            color = if (filterMode == "nv") Color(0xFF2EC471) else MaterialTheme.colorScheme.primary,
                            fontSize = 9.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        )

                        // Camera Name (Bottom Left)
                        Text(
                            text = if (selectedCam == 1) "CAM-01 // TANK_LVL_MON" else "CAM-02 // VALVE_FLOW_MON",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        )

                        // Running Timestamp Clock (Bottom Right)
                        Text(
                            text = timeString,
                            color = Color.White,
                            fontSize = 9.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp)
                                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        )

                        // Alarm overlays
                        if (reading.leakDetected) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .background(Color(0xFFE53935).copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                                    .border(1.dp, Color.White, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "ANOMALY: LEAK DETECTED",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        } else if (reading.waterLevelPct >= 95) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .background(Color(0xFFE53935).copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                                    .border(1.dp, Color.White, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "WARNING: OVERFLOW RISK",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }

                    // PTZ Arrow Controls
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "PTZ CAMERA CONTROLS",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // D-Pad grid
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                IconButton(
                                    onClick = { panY -= 15f },
                                    modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                                ) {
                                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Pan Up", modifier = Modifier.size(18.dp))
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    IconButton(
                                        onClick = { panX -= 15f },
                                        modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                                    ) {
                                        Icon(Icons.Default.ChevronLeft, contentDescription = "Pan Left", modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(
                                        onClick = {
                                            panX = 0f
                                            panY = 0f
                                            zoom = 1.0f
                                        },
                                        modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                                    ) {
                                        Icon(Icons.Default.Refresh, contentDescription = "Reset PTZ", modifier = Modifier.size(16.dp))
                                    }
                                    IconButton(
                                        onClick = { panX += 15f },
                                        modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                                    ) {
                                        Icon(Icons.Default.ChevronRight, contentDescription = "Pan Right", modifier = Modifier.size(18.dp))
                                    }
                                }
                                IconButton(
                                    onClick = { panY += 15f },
                                    modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                                ) {
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Pan Down", modifier = Modifier.size(18.dp))
                                }
                            }

                            // Zoom controls
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { zoom = (zoom + 0.2f).coerceAtMost(2.5f) },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Zoom +", fontSize = 10.sp)
                                }
                                Button(
                                    onClick = { zoom = (zoom - 0.2f).coerceAtLeast(0.8f) },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out", modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Zoom -", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}



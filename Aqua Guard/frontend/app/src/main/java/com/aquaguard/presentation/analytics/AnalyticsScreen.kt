package com.aquaguard.presentation.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquaguard.domain.model.Device
import java.util.Locale

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel
) {
    val devices by viewModel.devices.collectAsState()
    val selectedDeviceId by viewModel.selectedDeviceId.collectAsState()
    val dailyUsage by viewModel.dailyUsage.collectAsState()
    val totalSaved by viewModel.totalWaterSaved.collectAsState()
    val avgConsumption by viewModel.averageConsumption.collectAsState()
    val estBill by viewModel.estimatedBill.collectAsState()
    val aiPrediction by viewModel.aiConsumptionPrediction.collectAsState()
    val pdfState by viewModel.pdfState.collectAsState()

    AnalyticsScreenContent(
        devices = devices,
        selectedDeviceId = selectedDeviceId,
        dailyUsage = dailyUsage,
        totalSaved = totalSaved,
        avgConsumption = avgConsumption,
        estBill = estBill,
        aiPrediction = aiPrediction,
        pdfState = pdfState,
        onSelectDevice = { viewModel.selectDevice(it) },
        onExportPdf = { viewModel.exportUsageReportAsPdf() }
    )
}

@Composable
fun AnalyticsScreenContent(
    devices: List<Device>,
    selectedDeviceId: String?,
    dailyUsage: Map<String, Float>,
    totalSaved: Float,
    avgConsumption: Float,
    estBill: Float,
    aiPrediction: List<Pair<String, Float>>,
    pdfState: PdfExportState,
    onSelectDevice: (String) -> Unit,
    onExportPdf: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(pdfState) {
        if (pdfState is PdfExportState.Success) {
            snackbarHostState.showSnackbar("Exported successfully as: ${pdfState.fileName}")
        } else if (pdfState is PdfExportState.Error) {
            snackbarHostState.showSnackbar("Failed to export PDF: ${pdfState.error}")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        Text(
            text = "Water Analytics",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )

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

        // Summary Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avg Consumption Card
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.WaterDrop, contentDescription = "Avg", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Daily Avg", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Text(String.format(Locale.getDefault(), "%.1f L", avgConsumption), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Water Saved Card
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Eco, contentDescription = "Saved", tint = Color(0xFF4CAF50))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Water Saved", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                    Text(String.format(Locale.getDefault(), "%.1f L", totalSaved), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                }
            }

            // Bill Card
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.AttachMoney, contentDescription = "Bill", tint = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Est. Bill", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Text(String.format(Locale.getDefault(), "$%.2f", estBill), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Custom Bezier Line Chart Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Weekly Consumption Trend",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Liters consumed per day",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Chart Canvas
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    if (dailyUsage.isNotEmpty()) {
                        ConsumptionBezierChart(
                            data = dailyUsage.toSortedMap(),
                            colorScheme = MaterialTheme.colorScheme
                        )
                    } else {
                        // Empty state for chart
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No usage data available for this week.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                        }
                    }
                }
            }
        }

        // Saving Tips Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = "Tips",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Smart Saving Tip", fontWeight = FontWeight.Bold)
                    Text(
                        "Enabling Auto Protection Mode reduces water wastage by up to 15% by shutting off lines during unnoticed micro-leakages.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // AI Consumption Prediction Card
        if (aiPrediction.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "AI Forecast", tint = MaterialTheme.colorScheme.primary)
                        Text("AI Consumption Forecast", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Text(
                        "Predicted consumption for next week based on usage habits",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        aiPrediction.forEach { (day, liters) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(day, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(String.format(Locale.getDefault(), "%.0fL", liters), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }

        // PDF Export Panel
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Export Report", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Download detailed consumption logs as PDF", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
                Button(
                    onClick = onExportPdf,
                    enabled = pdfState !is PdfExportState.Generating,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (pdfState is PdfExportState.Generating) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(16.dp))
                    } else {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "PDF Icon", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export PDF", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnalyticsScreenPreview() {
    AquaGuardTheme {
        AnalyticsScreenContent(
            devices = listOf(
                Device("dev1", "Main Inlet", "user1", 1687957200000L, "v1.0.0", "online", 100),
                Device("dev2", "Backyard Tank", "user1", 1687957200000L, "v1.0.0", "offline", 80)
            ),
            selectedDeviceId = "dev1",
            dailyUsage = mapOf(
                "2026-06-22" to 112.5f,
                "2026-06-23" to 85.2f,
                "2026-06-24" to 132.0f,
                "2026-06-25" to 95.8f,
                "2026-06-26" to 120.4f,
                "2026-06-27" to 150.0f,
                "2026-06-28" to 75.5f
            ),
            totalSaved = 18.5f,
            avgConsumption = 110.2f,
            estBill = 3.85f,
            onSelectDevice = {}
        )
    }
}


@Composable
fun ConsumptionBezierChart(
    data: Map<String, Float>,
    colorScheme: ColorScheme
) {
    val values = data.values.toList()
    val dates = data.keys.toList()

    val maxVal = (values.maxOrNull() ?: 100f).coerceAtLeast(10f)

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp, end = 8.dp)
    ) {
        val width = size.width
        val height = size.height

        val paddingLeft = 40f
        val paddingBottom = 40f

        val chartWidth = width - paddingLeft
        val chartHeight = height - paddingBottom

        // Draw Y Axis Gridlines (4 levels)
        val gridLines = 4
        for (i in 0..gridLines) {
            val y = chartHeight - (chartHeight / gridLines) * i
            val gridVal = (maxVal / gridLines) * i
            
            // Draw line
            drawLine(
                color = Color.LightGray.copy(alpha = 0.3f),
                start = Offset(paddingLeft, y),
                end = Offset(width, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Plot points and build Bezier curve
        if (values.size > 1) {
            val points = mutableListOf<Offset>()
            val stepX = chartWidth / (values.size - 1)

            values.forEachIndexed { index, valItem ->
                val x = paddingLeft + (index * stepX)
                val y = chartHeight - ((valItem / maxVal) * chartHeight)
                points.add(Offset(x, y))
            }

            // Draw Bezier Curve Path
            val strokePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 0 until points.size - 1) {
                    val p0 = points[i]
                    val p1 = points[i + 1]
                    
                    // Control points for cubic bezier curves
                    val controlX1 = p0.x + (p1.x - p0.x) / 2f
                    val controlY1 = p0.y
                    val controlX2 = p0.x + (p1.x - p0.x) / 2f
                    val controlY2 = p1.y

                    cubicTo(controlX1, controlY1, controlX2, controlY2, p1.x, p1.y)
                }
            }

            // Draw Area Under the curve with gradient
            val fillPath = Path().apply {
                addPath(strokePath)
                lineTo(points.last().x, chartHeight)
                lineTo(points.first().x, chartHeight)
                close()
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colorScheme.primary.copy(alpha = 0.3f),
                        colorScheme.primary.copy(alpha = 0.0f)
                    )
                )
            )

            // Draw the curve line
            drawPath(
                path = strokePath,
                color = colorScheme.primary,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw points & Labels
            points.forEachIndexed { index, point ->
                // Draw circle dot
                drawCircle(
                    color = colorScheme.primary,
                    radius = 4.dp.toPx(),
                    center = point
                )
                drawCircle(
                    color = Color.White,
                    radius = 2.dp.toPx(),
                    center = point
                )
            }
        }
    }
}

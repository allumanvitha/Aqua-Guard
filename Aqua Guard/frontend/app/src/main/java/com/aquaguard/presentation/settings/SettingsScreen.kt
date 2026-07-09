package com.aquaguard.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val isLeakAlertEnabled by viewModel.isLeakAlertEnabled.collectAsState()
    val isOverflowAlertEnabled by viewModel.isOverflowAlertEnabled.collectAsState()
    val isDailyReportEnabled by viewModel.isDailyReportEnabled.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()

    var showAboutDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings & Privacy", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
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
            // 1. Theme Configuration
            Text("Display Options", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Select Theme Mode", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    listOf(
                        "system" to "System Default",
                        "light" to "Light Mode",
                        "dark" to "Dark Mode"
                    ).forEach { (modeVal, label) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setThemeMode(modeVal) }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = themeMode == modeVal,
                                onClick = { viewModel.setThemeMode(modeVal) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(label, fontSize = 14.sp)
                        }
                    }
                }
            }

            // 2. Alert Notification toggles
            Text("Smart Notifications", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Leakage Alarms", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Receive push alerts when line leak is detected", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                        Switch(
                            checked = isLeakAlertEnabled,
                            onCheckedChange = { viewModel.setLeakAlertEnabled(it) }
                        )
                    }

                    Divider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Overflow Alerts", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Get notified when level exceeds 98%", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                        Switch(
                            checked = isOverflowAlertEnabled,
                            onCheckedChange = { viewModel.setOverflowAlertEnabled(it) }
                        )
                    }

                    Divider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Daily Reports Summaries", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Compile consumption forecast updates daily", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                        Switch(
                            checked = isDailyReportEnabled,
                            onCheckedChange = { viewModel.setDailyReportEnabled(it) }
                        )
                    }
                }
            }

            // 3. Language settings
            Text("General Preferences", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Language, contentDescription = "Language", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("App Language", fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = if (appLanguage == "en") "English" else "Spanish",
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable {
                            val targetLang = if (appLanguage == "en") "es" else "en"
                            viewModel.setAppLanguage(targetLang)
                        }
                    )
                }
            }

            // 4. Support and Help
            Text("Support & Legal", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    ListItem(
                        headlineContent = { Text("Help Center & FAQ", fontWeight = FontWeight.Bold) },
                        leadingContent = { Icon(Icons.Default.Help, contentDescription = "Help", tint = MaterialTheme.colorScheme.primary) },
                        modifier = Modifier.clickable { showHelpDialog = true }
                    )
                    Divider()
                    ListItem(
                        headlineContent = { Text("About Aqua Guard", fontWeight = FontWeight.Bold) },
                        leadingContent = { Icon(Icons.Default.Info, contentDescription = "About", tint = MaterialTheme.colorScheme.primary) },
                        modifier = Modifier.clickable { showAboutDialog = true }
                    )
                }
            }
        }

        if (showAboutDialog) {
            AlertDialog(
                onDismissRequest = { showAboutDialog = false },
                title = { Text("About Aqua Guard") },
                text = {
                    Text(
                        "Aqua Guard v1.0.0\n\nDesigned for smart water monitoring and leakage protection.\n\nBuilt using Kotlin Jetpack Compose and Firebase Integration."
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showAboutDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }

        if (showHelpDialog) {
            AlertDialog(
                onDismissRequest = { showHelpDialog = false },
                title = { Text("Help Center") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("1. How do I pair my ESP32?", fontWeight = FontWeight.Bold)
                        Text("Go to Devices tab, click Add Device, enter the ID, click expanded card options and complete the Wi-Fi provisioning.", fontSize = 13.sp)
                        Text("2. How does leak protection work?", fontWeight = FontWeight.Bold)
                        Text("In auto protection mode, when the leak sensor values match warning thresholds, the solenoid valve shuts off flow.", fontSize = 13.sp)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showHelpDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

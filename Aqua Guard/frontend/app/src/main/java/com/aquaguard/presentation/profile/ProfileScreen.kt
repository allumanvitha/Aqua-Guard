package com.aquaguard.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.ui.tooling.preview.Preview
import com.aquaguard.presentation.theme.AquaGuardTheme
import com.aquaguard.domain.model.User

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToSettings: () -> Unit,
    onSignOutSuccess: () -> Unit
) {
    val userState by viewModel.currentUser.collectAsState()

    LaunchedEffect(userState) {
        if (userState == null) {
            onSignOutSuccess()
        }
    }

    ProfileScreenContent(
        user = userState,
        onNavigateToSettings = onNavigateToSettings,
        onUpdateDetails = { members, target -> viewModel.updateHouseholdDetails(members, target) },
        onSignOut = { viewModel.signOut() }
    )
}

@Composable
fun ProfileScreenContent(
    user: User?,
    onNavigateToSettings: () -> Unit,
    onUpdateDetails: (Int, Int) -> Unit,
    onSignOut: () -> Unit
) {
    var familyMembers by remember { mutableStateOf("4") }
    var dailyTarget by remember { mutableStateOf("300") }

    var leakAlertsEnabled by remember { mutableStateOf(true) }
    var overflowAlertsEnabled by remember { mutableStateOf(true) }
    var dailyReportsEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(user) {
        user?.let {
            familyMembers = it.familyMembers.toString()
            dailyTarget = it.dailyTargetLiters.toString()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "User Profile",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            IconButton(onClick = onNavigateToSettings) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.primary)
            }
        }

        user?.let { u ->
            // 1. Profile Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User Avatar",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(u.displayName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(u.email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                }
            }

            // 2. Household Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Home, contentDescription = "Household", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Household Details", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Divider()

                    OutlinedTextField(
                        value = familyMembers,
                        onValueChange = { familyMembers = it },
                        label = { Text("Number of Family Members") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = dailyTarget,
                        onValueChange = { dailyTarget = it },
                        label = { Text("Daily Water Target (Liters)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            val members = familyMembers.toIntOrNull() ?: 4
                            val target = dailyTarget.toIntOrNull() ?: 300
                            onUpdateDetails(members, target)
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Save Details")
                    }
                }
            }

            // 3. Notification Preferences Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Notification Preferences", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Divider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpacerBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Leakage Alarms")
                        Switch(checked = leakAlertsEnabled, onCheckedChange = { leakAlertsEnabled = it })
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpacerBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Overflow Alarms")
                        Switch(checked = overflowAlertsEnabled, onCheckedChange = { overflowAlertsEnabled = it })
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpacerBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Daily Reports & Consumption Summaries")
                        Switch(checked = dailyReportsEnabled, onCheckedChange = { dailyReportsEnabled = it })
                    }
                }
            }

            // 4. Sign Out Button
            Button(
                onClick = onSignOut,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign Out", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    AquaGuardTheme {
        ProfileScreenContent(
            user = User(
                uid = "user1",
                email = "john.doe@example.com",
                displayName = "John Doe",
                photoUrl = null,
                familyMembers = 4,
                dailyTargetLiters = 300,
                connectedDevices = listOf("dev1")
            ),
            onUpdateDetails = { _, _ -> },
            onSignOut = {}
        )
    }
}


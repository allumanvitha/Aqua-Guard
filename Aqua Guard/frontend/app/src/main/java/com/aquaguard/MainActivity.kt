package com.aquaguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aquaguard.presentation.alerts.AlertsScreen
import com.aquaguard.presentation.alerts.AlertsViewModel
import com.aquaguard.presentation.analytics.AnalyticsScreen
import com.aquaguard.presentation.analytics.AnalyticsViewModel
import com.aquaguard.presentation.auth.AuthViewModel
import com.aquaguard.presentation.auth.LoginScreen
import com.aquaguard.presentation.auth.RegisterScreen
import com.aquaguard.presentation.auth.ForgotPasswordScreen
import com.aquaguard.presentation.ai.AIAssistantScreen
import com.aquaguard.presentation.ai.AIAssistantViewModel
import com.aquaguard.presentation.settings.SettingsScreen
import com.aquaguard.presentation.settings.SettingsViewModel
import com.aquaguard.presentation.dashboard.DashboardScreen
import com.aquaguard.presentation.dashboard.DashboardViewModel
import com.aquaguard.presentation.devices.DevicesScreen
import com.aquaguard.presentation.devices.DevicesViewModel
import com.aquaguard.presentation.history.HistoryScreen
import com.aquaguard.presentation.history.HistoryViewModel
import com.aquaguard.presentation.navigation.Screen
import com.aquaguard.presentation.profile.ProfileScreen
import com.aquaguard.presentation.profile.ProfileViewModel
import com.aquaguard.presentation.theme.AquaGuardTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val themeMode by settingsViewModel.themeMode.collectAsState()

            AquaGuardTheme(themeMode = themeMode) {
                val navController = rememberNavController()
                val currentUser by authViewModel.currentUser.collectAsState()

                // Check authentication state
                val startDestination = if (currentUser != null) {
                    Screen.Dashboard.route
                } else {
                    Screen.Login.route
                }

                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Auth Flow
                    composable(Screen.Login.route) {
                        LoginScreen(
                            viewModel = authViewModel,
                            onNavigateToRegister = {
                                navController.navigate(Screen.Register.route)
                            },
                            onNavigateToForgotPassword = {
                                navController.navigate(Screen.ForgotPassword.route)
                            },
                            onLoginSuccess = {
                                navController.navigate(Screen.Dashboard.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(Screen.Register.route) {
                        RegisterScreen(
                            viewModel = authViewModel,
                            onNavigateToLogin = {
                                navController.popBackStack()
                            },
                            onRegisterSuccess = {
                                navController.navigate(Screen.Dashboard.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(Screen.ForgotPassword.route) {
                        ForgotPasswordScreen(
                            viewModel = authViewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    // Main Shell Flow (with Bottom Bar)
                    composable(Screen.Dashboard.route) {
                        MainAppShell(navController, startDestination = Screen.Dashboard.route)
                    }
                    composable(Screen.Devices.route) {
                        val devicesViewModel: DevicesViewModel = hiltViewModel()
                        DevicesScreen(
                            viewModel = devicesViewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable(Screen.AIAssistant.route) {
                        val aiViewModel: AIAssistantViewModel = hiltViewModel()
                        AIAssistantScreen(viewModel = aiViewModel)
                    }
                    composable(Screen.Settings.route) {
                        SettingsScreen(
                            viewModel = settingsViewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable(Screen.Analytics.route) {
                        MainAppShell(navController, startDestination = Screen.Analytics.route)
                    }
                    composable(Screen.Alerts.route) {
                        MainAppShell(navController, startDestination = Screen.Alerts.route)
                    }
                    composable(Screen.History.route) {
                        MainAppShell(navController, startDestination = Screen.History.route)
                    }
                    composable(Screen.Profile.route) {
                        MainAppShell(navController, startDestination = Screen.Profile.route)
                    }
                }
            }
        }
    }
}

@Composable
fun MainAppShell(
    parentNavController: NavHostController,
    startDestination: String
) {
    val shellNavController = rememberNavController()
    
    val items = listOf(
        Screen.Dashboard,
        Screen.Analytics,
        Screen.Alerts,
        Screen.History,
        Screen.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by shellNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon!!, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            shellNavController.navigate(screen.route) {
                                popUpTo(shellNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = shellNavController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                val dashboardViewModel: DashboardViewModel = hiltViewModel()
                DashboardScreen(
                    viewModel = dashboardViewModel,
                    onNavigateToDevices = {
                        parentNavController.navigate(Screen.Devices.route)
                    },
                    onNavigateToAIAssistant = {
                        parentNavController.navigate(Screen.AIAssistant.route)
                    }
                )
            }
            composable(Screen.Analytics.route) {
                val analyticsViewModel: AnalyticsViewModel = hiltViewModel()
                AnalyticsScreen(viewModel = analyticsViewModel)
            }
            composable(Screen.Alerts.route) {
                val alertsViewModel: AlertsViewModel = hiltViewModel()
                AlertsScreen(viewModel = alertsViewModel)
            }
            composable(Screen.History.route) {
                val historyViewModel: HistoryViewModel = hiltViewModel()
                HistoryScreen(viewModel = historyViewModel)
            }
            composable(Screen.Profile.route) {
                val profileViewModel: ProfileViewModel = hiltViewModel()
                ProfileScreen(
                    viewModel = profileViewModel,
                    onNavigateToSettings = {
                        parentNavController.navigate(Screen.Settings.route)
                    },
                    onSignOutSuccess = {
                        parentNavController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

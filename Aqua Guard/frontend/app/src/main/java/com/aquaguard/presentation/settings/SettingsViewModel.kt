package com.aquaguard.presentation.settings

import androidx.lifecycle.ViewModel
import com.aquaguard.data.local.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _themeMode = MutableStateFlow(preferencesManager.themeMode)
    val themeMode = _themeMode.asStateFlow()

    private val _isLeakAlertEnabled = MutableStateFlow(preferencesManager.isLeakAlertEnabled)
    val isLeakAlertEnabled = _isLeakAlertEnabled.asStateFlow()

    private val _isOverflowAlertEnabled = MutableStateFlow(preferencesManager.isOverflowAlertEnabled)
    val isOverflowAlertEnabled = _isOverflowAlertEnabled.asStateFlow()

    private val _isDailyReportEnabled = MutableStateFlow(preferencesManager.isDailyReportEnabled)
    val isDailyReportEnabled = _isDailyReportEnabled.asStateFlow()

    private val _appLanguage = MutableStateFlow(preferencesManager.appLanguage)
    val appLanguage = _appLanguage.asStateFlow()

    fun setThemeMode(mode: String) {
        preferencesManager.themeMode = mode
        _themeMode.value = mode
    }

    fun setLeakAlertEnabled(enabled: Boolean) {
        preferencesManager.isLeakAlertEnabled = enabled
        _isLeakAlertEnabled.value = enabled
    }

    fun setOverflowAlertEnabled(enabled: Boolean) {
        preferencesManager.isOverflowAlertEnabled = enabled
        _isOverflowAlertEnabled.value = enabled
    }

    fun setDailyReportEnabled(enabled: Boolean) {
        preferencesManager.isDailyReportEnabled = enabled
        _isDailyReportEnabled.value = enabled
    }

    fun setAppLanguage(language: String) {
        preferencesManager.appLanguage = language
        _appLanguage.value = language
    }
}

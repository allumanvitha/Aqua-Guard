package com.aquaguard.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("aquaguard_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_REMEMBER_ME = "remember_me"
        private const val KEY_EMAIL = "email"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_LEAK_ALERTS = "leak_alerts"
        private const val KEY_OVERFLOW_ALERTS = "overflow_alerts"
        private const val KEY_DAILY_REPORTS = "daily_reports"
        private const val KEY_LANGUAGE = "language"
    }

    var isRememberMeEnabled: Boolean
        get() = prefs.getBoolean(KEY_REMEMBER_ME, false)
        set(value) = prefs.edit().putBoolean(KEY_REMEMBER_ME, value).apply()

    var rememberedEmail: String
        get() = prefs.getString(KEY_EMAIL, "").orEmpty()
        set(value) = prefs.edit().putString(KEY_EMAIL, value).apply()

    var themeMode: String
        get() = prefs.getString(KEY_THEME_MODE, "system") ?: "system"
        set(value) = prefs.edit().putString(KEY_THEME_MODE, value).apply()

    var isLeakAlertEnabled: Boolean
        get() = prefs.getBoolean(KEY_LEAK_ALERTS, true)
        set(value) = prefs.edit().putBoolean(KEY_LEAK_ALERTS, value).apply()

    var isOverflowAlertEnabled: Boolean
        get() = prefs.getBoolean(KEY_OVERFLOW_ALERTS, true)
        set(value) = prefs.edit().putBoolean(KEY_OVERFLOW_ALERTS, value).apply()

    var isDailyReportEnabled: Boolean
        get() = prefs.getBoolean(KEY_DAILY_REPORTS, false)
        set(value) = prefs.edit().putBoolean(KEY_DAILY_REPORTS, value).apply()

    var appLanguage: String
        get() = prefs.getString(KEY_LANGUAGE, "en") ?: "en"
        set(value) = prefs.edit().putString(KEY_LANGUAGE, value).apply()
}

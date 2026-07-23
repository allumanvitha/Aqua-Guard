package com.aquaguard.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class PreferencesManager {
    private final SharedPreferences prefs;

    private static final String KEY_REMEMBER_ME = "remember_me";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_THEME_MODE = "theme_mode";
    private static final String KEY_LEAK_ALERTS = "leak_alerts";
    private static final String KEY_OVERFLOW_ALERTS = "overflow_alerts";
    private static final String KEY_DAILY_REPORTS = "daily_reports";
    private static final String KEY_LANGUAGE = "language";

    @Inject
    public PreferencesManager(@ApplicationContext Context context) {
        this.prefs = context.getSharedPreferences("aquaguard_prefs", Context.MODE_PRIVATE);
    }

    public boolean isRememberMeEnabled() {
        return prefs.getBoolean(KEY_REMEMBER_ME, false);
    }

    public void setRememberMeEnabled(boolean value) {
        prefs.edit().putBoolean(KEY_REMEMBER_ME, value).apply();
    }

    public String getRememberedEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    public void setRememberedEmail(String value) {
        prefs.edit().putString(KEY_EMAIL, value).apply();
    }

    public String getThemeMode() {
        return prefs.getString(KEY_THEME_MODE, "system");
    }

    public void setThemeMode(String value) {
        prefs.edit().putString(KEY_THEME_MODE, value).apply();
    }

    public boolean isLeakAlertEnabled() {
        return prefs.getBoolean(KEY_LEAK_ALERTS, true);
    }

    public void setLeakAlertEnabled(boolean value) {
        prefs.edit().putBoolean(KEY_LEAK_ALERTS, value).apply();
    }

    public boolean isOverflowAlertEnabled() {
        return prefs.getBoolean(KEY_OVERFLOW_ALERTS, true);
    }

    public void setOverflowAlertEnabled(boolean value) {
        prefs.edit().putBoolean(KEY_OVERFLOW_ALERTS, value).apply();
    }

    public boolean isDailyReportEnabled() {
        return prefs.getBoolean(KEY_DAILY_REPORTS, false);
    }

    public void setDailyReportEnabled(boolean value) {
        prefs.edit().putBoolean(KEY_DAILY_REPORTS, value).apply();
    }

    public String getAppLanguage() {
        return prefs.getString(KEY_LANGUAGE, "en");
    }

    public void setAppLanguage(String value) {
        prefs.edit().putString(KEY_LANGUAGE, value).apply();
    }
}

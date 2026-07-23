package com.aquaguard.presentation.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aquaguard.data.local.PreferencesManager;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SettingsViewModel extends ViewModel {
    private final PreferencesManager preferencesManager;

    private final MutableLiveData<String> themeMode = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLeakAlertEnabled = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isOverflowAlertEnabled = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isDailyReportEnabled = new MutableLiveData<>();
    private final MutableLiveData<String> appLanguage = new MutableLiveData<>();

    @Inject
    public SettingsViewModel(PreferencesManager preferencesManager) {
        this.preferencesManager = preferencesManager;

        // Load cached preference settings
        this.themeMode.setValue(preferencesManager.getThemeMode());
        this.isLeakAlertEnabled.setValue(preferencesManager.isLeakAlertEnabled());
        this.isOverflowAlertEnabled.setValue(preferencesManager.isOverflowAlertEnabled());
        this.isDailyReportEnabled.setValue(preferencesManager.isDailyReportEnabled());
        this.appLanguage.setValue(preferencesManager.getAppLanguage());
    }

    public LiveData<String> getThemeMode() { return themeMode; }
    public LiveData<Boolean> getIsLeakAlertEnabled() { return isLeakAlertEnabled; }
    public LiveData<Boolean> getIsOverflowAlertEnabled() { return isOverflowAlertEnabled; }
    public LiveData<Boolean> getIsDailyReportEnabled() { return isDailyReportEnabled; }
    public LiveData<String> getAppLanguage() { return appLanguage; }

    public void setThemeMode(String mode) {
        preferencesManager.setThemeMode(mode);
        themeMode.setValue(mode);
    }

    public void setLeakAlertEnabled(boolean enabled) {
        preferencesManager.setLeakAlertEnabled(enabled);
        isLeakAlertEnabled.setValue(enabled);
    }

    public void setOverflowAlertEnabled(boolean enabled) {
        preferencesManager.setOverflowAlertEnabled(enabled);
        isOverflowAlertEnabled.setValue(enabled);
    }

    public void setDailyReportEnabled(boolean enabled) {
        preferencesManager.setDailyReportEnabled(enabled);
        isDailyReportEnabled.setValue(enabled);
    }

    public void setAppLanguage(String language) {
        preferencesManager.setAppLanguage(language);
        appLanguage.setValue(language);
    }
}

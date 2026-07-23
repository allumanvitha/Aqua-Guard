package com.aquaguard.presentation.analytics;

import android.os.Handler;
import android.os.Looper;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aquaguard.domain.model.Device;
import com.aquaguard.domain.repository.DeviceRepository;
import com.aquaguard.domain.repository.WaterRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AnalyticsViewModel extends ViewModel {
    private final DeviceRepository deviceRepository;
    private final WaterRepository waterRepository;

    private final LiveData<List<Device>> devices;
    private final MutableLiveData<String> selectedDeviceId = new MutableLiveData<>(null);
    
    private final MediatorLiveData<Map<String, Float>> dailyUsage = new MediatorLiveData<>();
    private final MediatorLiveData<Map<String, Float>> monthlyUsage = new MediatorLiveData<>();
    
    private final MediatorLiveData<Float> totalWaterSaved = new MediatorLiveData<>();
    private final MediatorLiveData<Float> averageConsumption = new MediatorLiveData<>();
    private final MediatorLiveData<Float> estimatedBill = new MediatorLiveData<>();
    private final MediatorLiveData<List<Pair<String, Float>>> aiConsumptionPrediction = new MediatorLiveData<>();
    private final MediatorLiveData<List<ForecastItem>> predictedForecast = new MediatorLiveData<>();

    private final MutableLiveData<PdfExportState> pdfState = new MutableLiveData<>(PdfExportState.idle());
    private final Handler handler = new Handler(Looper.getMainLooper());

    private LiveData<Map<String, Float>> currentDailySource = null;
    private LiveData<Map<String, Float>> currentMonthlySource = null;

    @Inject
    public AnalyticsViewModel(DeviceRepository deviceRepository, WaterRepository waterRepository) {
        this.deviceRepository = deviceRepository;
        this.waterRepository = waterRepository;

        this.devices = deviceRepository.getDevices();

        // Select first device by default
        this.dailyUsage.addSource(this.devices, list -> {
            if (selectedDeviceId.getValue() == null && list != null && !list.isEmpty()) {
                selectDevice(list.get(0).getDeviceId());
            }
        });

        // Update usage source on device selection
        this.dailyUsage.addSource(selectedDeviceId, id -> {
            if (currentDailySource != null) {
                dailyUsage.removeSource(currentDailySource);
            }
            if (id != null) {
                currentDailySource = waterRepository.getDailyUsage(id);
                dailyUsage.addSource(currentDailySource, dailyUsage::setValue);
            } else {
                currentDailySource = null;
                dailyUsage.setValue(new HashMap<>());
            }
        });

        this.monthlyUsage.addSource(selectedDeviceId, id -> {
            if (currentMonthlySource != null) {
                monthlyUsage.removeSource(currentMonthlySource);
            }
            if (id != null) {
                currentMonthlySource = waterRepository.getMonthlyUsage(id);
                monthlyUsage.addSource(currentMonthlySource, monthlyUsage::setValue);
            } else {
                currentMonthlySource = null;
                monthlyUsage.setValue(new HashMap<>());
            }
        });

        // Compute helper metrics from dailyUsage
        this.totalWaterSaved.addSource(dailyUsage, usage -> {
            if (usage == null || usage.isEmpty()) {
                totalWaterSaved.setValue(0f);
                return;
            }
            float sum = 0f;
            for (float val : usage.values()) {
                sum += val;
            }
            totalWaterSaved.setValue(sum * 0.12f);
        });

        this.averageConsumption.addSource(dailyUsage, usage -> {
            if (usage == null || usage.isEmpty()) {
                averageConsumption.setValue(0f);
                return;
            }
            float sum = 0f;
            for (float val : usage.values()) {
                sum += val;
            }
            averageConsumption.setValue(sum / usage.size());
        });

        this.estimatedBill.addSource(dailyUsage, usage -> {
            if (usage == null || usage.isEmpty()) {
                estimatedBill.setValue(0f);
                return;
            }
            float sum = 0f;
            for (float val : usage.values()) {
                sum += val;
            }
            estimatedBill.setValue(sum * 0.005f);
        });

        this.aiConsumptionPrediction.addSource(averageConsumption, avg -> {
            if (avg == null || avg == 0f) {
                aiConsumptionPrediction.setValue(new ArrayList<>());
                return;
            }
            List<String> days = Arrays.asList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");
            List<Pair<String, Float>> predictions = new ArrayList<>();
            for (int i = 0; i < days.size(); i++) {
                float factor = 1.0f + 0.15f * (float) Math.sin(i);
                predictions.add(new Pair<>(days.get(i), avg * factor));
            }
            aiConsumptionPrediction.setValue(predictions);
        });

        this.predictedForecast.addSource(averageConsumption, avg -> {
            if (avg == null || avg == 0f) {
                predictedForecast.setValue(new ArrayList<>());
                return;
            }
            List<String> days = Arrays.asList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");
            List<ForecastItem> predictions = new ArrayList<>();
            for (int i = 0; i < days.size(); i++) {
                float factor = 1.0f + 0.15f * (float) Math.sin(i);
                predictions.add(new ForecastItem(days.get(i), avg * factor));
            }
            predictedForecast.setValue(predictions);
        });
    }

    public LiveData<List<Device>> getDevices() { return devices; }
    public LiveData<String> getSelectedDeviceId() { return selectedDeviceId; }
    public LiveData<Map<String, Float>> getDailyUsage() { return dailyUsage; }
    public LiveData<Map<String, Float>> getMonthlyUsage() { return monthlyUsage; }
    public LiveData<Float> getTotalWaterSaved() { return totalWaterSaved; }
    public LiveData<Float> getAverageConsumption() { return averageConsumption; }
    public LiveData<Float> getEstimatedBill() { return estimatedBill; }
    public LiveData<List<Pair<String, Float>>> getAiConsumptionPrediction() { return aiConsumptionPrediction; }
    public LiveData<PdfExportState> getPdfState() { return pdfState; }

    public LiveData<Map<String, Float>> getWeeklyConsumptionData() { return dailyUsage; }
    public LiveData<Float> getWaterSaved() { return totalWaterSaved; }
    public LiveData<List<ForecastItem>> getPredictedForecast() { return predictedForecast; }

    public void selectDevice(String deviceId) {
        selectedDeviceId.setValue(deviceId);
    }

    public void exportUsageReportAsPdf() {
        pdfState.setValue(PdfExportState.generating());
        handler.postDelayed(() -> {
            pdfState.setValue(PdfExportState.success("AquaGuard_Usage_Report.pdf"));
            handler.postDelayed(() -> pdfState.setValue(PdfExportState.idle()), 2000);
        }, 2000);
    }

    // Java equivalent of Kotlin sealed class
    public static class PdfExportState {
        public enum Status { IDLE, GENERATING, SUCCESS, ERROR }
        private final Status status;
        private final String fileName;
        private final String errorMessage;

        private PdfExportState(Status status, String fileName, String errorMessage) {
            this.status = status;
            this.fileName = fileName;
            this.errorMessage = errorMessage;
        }

        public static PdfExportState idle() { return new PdfExportState(Status.IDLE, "", ""); }
        public static PdfExportState generating() { return new PdfExportState(Status.GENERATING, "", ""); }
        public static PdfExportState success(String file) { return new PdfExportState(Status.SUCCESS, file, ""); }
        public static PdfExportState error(String err) { return new PdfExportState(Status.ERROR, "", err); }

        public Status getStatus() { return status; }
        public String getFileName() { return fileName; }
        public String getErrorMessage() { return errorMessage; }
    }

    public static class ForecastItem {
        private final String day;
        private final float predictedLiters;
        public ForecastItem(String day, float predictedLiters) {
            this.day = day;
            this.predictedLiters = predictedLiters;
        }
        public String getDay() { return day; }
        public float getPredictedLiters() { return predictedLiters; }
    }
}

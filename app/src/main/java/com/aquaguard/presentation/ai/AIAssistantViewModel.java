package com.aquaguard.presentation.ai;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AIAssistantViewModel extends ViewModel {

    public static class Message {
        private final String text;
        private final boolean isUser;
        private final long timestamp;

        public Message(String text, boolean isUser) {
            this.text = text;
            this.isUser = isUser;
            this.timestamp = System.currentTimeMillis();
        }

        public String getText() { return text; }
        public boolean isUser() { return isUser; }
        public long getTimestamp() { return timestamp; }
    }

    private final MutableLiveData<List<Message>> messages = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isTyping = new MutableLiveData<>(false);
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Inject
    public AIAssistantViewModel() {
        List<Message> initial = new ArrayList<>();
        initial.add(new Message("Hello! I am your Aqua Guard AI Assistant. Ask me anything about water conservation, household limits, leak predictions, or device diagnostics.", false));
        messages.setValue(initial);
    }

    public LiveData<List<Message>> getMessages() {
        return messages;
    }

    public LiveData<Boolean> getIsTyping() {
        return isTyping;
    }

    public void sendMessage(String text) {
        if (text == null || text.trim().isEmpty()) return;

        List<Message> current = new ArrayList<>(messages.getValue() != null ? messages.getValue() : new ArrayList<>());
        current.add(new Message(text, true));
        messages.setValue(current);

        isTyping.setValue(true);
        handler.postDelayed(() -> {
            String aiResponse = getAIResponse(text);
            List<Message> updated = new ArrayList<>(messages.getValue() != null ? messages.getValue() : new ArrayList<>());
            updated.add(new Message(aiResponse, false));
            messages.setValue(updated);
            isTyping.setValue(false);
        }, 1500);
    }

    private String getAIResponse(String query) {
        String q = query.toLowerCase();
        if (q.contains("leak") || q.contains("pipe")) {
            return "Heuristic analysis suggests checking the solenoid valve coupling. Standard ESP32 sensor values show micro-deviations in pressure (approx -0.05 bar) when flow rate stays above 0.2L/min for 10+ minutes during zero active household demand. Recommend inspecting line B.";
        } else if (q.contains("save") || q.contains("reduce") || q.contains("conserve")) {
            return "Based on your average daily consumption of 110L, you can save approximately 15% (16.5L/day) by: 1) Activating Auto Protection Mode to prevent unnoticed tank overflows; 2) Adjusting your daily alert limit from 300L to 250L to encourage conscious usage.";
        } else if (q.contains("bill") || q.contains("cost") || q.contains("charge")) {
            return "Your projected bill for this month is $3.85. Aggregated analytics suggest that shifting wash loads to off-peak slots doesn't affect volumetric pricing, but auto-closing lines during leak alerts saves an average of $2.40 per incidence.";
        } else if (q.contains("quality") || q.contains("tds") || q.contains("ppm")) {
            return "Your current TDS level is 185 PPM, which is well within the excellent drinking water range (50-250 PPM). An upward trend towards 300+ PPM might indicate filter saturation or sediment buildup in the main storage tank.";
        } else {
            return "Interesting question! Aqua Guard's integration model continuously samples flow volume, level distance, and leak sensor status. If you have specific questions about reducing water wastage, leak alerts, or setting targets, let me know!";
        }
    }
}

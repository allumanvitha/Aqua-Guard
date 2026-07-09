package com.aquaguard.presentation.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Message(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@HiltViewModel
class AIAssistantViewModel @Inject constructor() : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(
        listOf(
            Message("Hello! I am your Aqua Guard AI Assistant. Ask me anything about water conservation, household limits, leak predictions, or device diagnostics.", false)
        )
    )
    val messages = _messages.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping = _isTyping.asStateFlow()

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val userMessage = Message(text, true)
        _messages.value = _messages.value + userMessage

        viewModelScope.launch {
            _isTyping.value = true
            kotlinx.coroutines.delay(1500) // Simulate AI thinking delay

            val aiResponseText = getAIResponse(text)
            val aiMessage = Message(aiResponseText, false)

            _messages.value = _messages.value + aiMessage
            _isTyping.value = false
        }
    }

    private fun getAIResponse(query: String): String {
        val q = query.lowercase()
        return when {
            q.contains("leak") || q.contains("pipe") -> {
                "Heuristic analysis suggests checking the solenoid valve coupling. Standard ESP32 sensor values show micro-deviations in pressure (approx -0.05 bar) when flow rate stays above 0.2L/min for 10+ minutes during zero active household demand. Recommend inspecting line B."
            }
            q.contains("save") || q.contains("reduce") || q.contains("conserve") -> {
                "Based on your average daily consumption of 110L, you can save approximately 15% (16.5L/day) by: 1) Activating Auto Protection Mode to prevent unnoticed tank overflows; 2) Adjusting your daily alert limit from 300L to 250L to encourage conscious usage."
            }
            q.contains("bill") || q.contains("cost") || q.contains("charge") -> {
                "Your projected bill for this month is $3.85. Aggregated analytics suggest that shifting wash loads to off-peak slots doesn't affect volumetric pricing, but auto-closing lines during leak alerts saves an average of $2.40 per incidence."
            }
            q.contains("quality") || q.contains("tds") || q.contains("ppm") -> {
                "Your current TDS level is 185 PPM, which is well within the excellent drinking water range (50-250 PPM). An upward trend towards 300+ PPM might indicate filter saturation or sediment buildup in the main storage tank."
            }
            else -> {
                "Interesting question! Aqua Guard's integration model continuously samples flow volume, level distance, and leak sensor status. If you have specific questions about reducing water wastage, leak alerts, or setting targets, let me know!"
            }
        }
    }
}

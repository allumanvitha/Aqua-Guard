package com.aquaguard.domain.model

data class Alert(
    val alertId: String = "",
    val deviceId: String = "",
    val type: AlertType = AlertType.SENSOR_FAILURE,
    val severity: AlertSeverity = AlertSeverity.INFO,
    val message: String = "",
    val timestamp: Long = 0L,
    val resolved: Boolean = false
)

enum class AlertType {
    LEAK_DETECTED,
    OVERFLOW_PREVENTED,
    EXCESSIVE_USAGE,
    DEVICE_OFFLINE,
    SENSOR_FAILURE
}

enum class AlertSeverity {
    CRITICAL,
    WARNING,
    INFO
}

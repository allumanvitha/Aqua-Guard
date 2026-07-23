package com.aquaguard.domain.model

data class ValveLog(
    val logId: String = "",
    val deviceId: String = "",
    val triggeredBy: String = "", // "USER" or "SYSTEM_AUTO"
    val action: String = "",      // "OPEN" or "CLOSE"
    val reason: String = "",
    val timestamp: Long = 0L
)

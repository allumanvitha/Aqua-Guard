package com.aquaguard.domain.model

data class WaterReading(
    val flowRate: Float,        // L/min
    val waterLevelPct: Int,     // 0-100
    val leakDetected: Boolean,
    val valveOpen: Boolean,
    val autoMode: Boolean,
    val lastSeen: Long
)

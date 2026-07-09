package com.aquaguard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alerts")
data class AlertEntity(
    @PrimaryKey val alertId: String,
    val deviceId: String,
    val type: String,
    val severity: String,
    val message: String,
    val timestamp: Long,
    val resolved: Boolean
)

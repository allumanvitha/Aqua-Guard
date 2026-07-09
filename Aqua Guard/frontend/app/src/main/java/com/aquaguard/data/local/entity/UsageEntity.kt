package com.aquaguard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_usage")
data class UsageEntity(
    @PrimaryKey val id: String, // Format: deviceId_date
    val deviceId: String,
    val date: String,
    val totalLiters: Float,
    val waterSavedLiters: Float
)

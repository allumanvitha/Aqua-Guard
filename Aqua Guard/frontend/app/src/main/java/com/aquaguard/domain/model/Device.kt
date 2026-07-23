package com.aquaguard.domain.model

data class Device(
    val deviceId: String,
    val deviceName: String,
    val ownerUid: String,
    val registeredAt: Long,
    val firmwareVersion: String,
    val status: String, // "online" or "offline"
    val batteryLevel: Int
)

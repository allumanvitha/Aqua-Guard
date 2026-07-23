package com.aquaguard.domain.model

data class User(
    val uid: String,
    val email: String,
    val displayName: String,
    val photoUrl: String?,
    val familyMembers: Int,
    val dailyTargetLiters: Int,
    val connectedDevices: List<String>
)

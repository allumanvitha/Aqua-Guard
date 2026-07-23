package com.aquaguard.domain.repository

import com.aquaguard.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun signInWithEmail(email: String, password: String): Result<User>
    suspend fun signUpWithEmail(email: String, password: String, displayName: String): Result<User>
    suspend fun signOut()
    suspend fun updateHouseholdDetails(familyMembers: Int, dailyTargetLiters: Int): Result<Unit>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
}

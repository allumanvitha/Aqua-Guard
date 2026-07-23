package com.aquaguard.data.repository

import com.aquaguard.domain.model.User
import com.aquaguard.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override val currentUser: Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                // Fetch household details from Firestore
                firestore.collection("users").document(firebaseUser.uid)
                    .get()
                    .addOnSuccessListener { document ->
                        val familyMembers = document.getLong("householdDetails.familyMembers")?.toInt() ?: 4
                        val dailyTarget = document.getLong("householdDetails.dailyTargetLiters")?.toInt() ?: 300
                        @Suppress("UNCHECKED_CAST")
                        val devices = document.get("connectedDevices") as? List<String> ?: emptyList()
                        
                        trySend(
                            User(
                                uid = firebaseUser.uid,
                                email = firebaseUser.email.orEmpty(),
                                displayName = firebaseUser.displayName ?: "Aqua Guard User",
                                photoUrl = firebaseUser.photoUrl?.toString(),
                                familyMembers = familyMembers,
                                dailyTargetLiters = dailyTarget,
                                connectedDevices = devices
                            )
                        )
                    }
                    .addOnFailureListener {
                        trySend(
                            User(
                                uid = firebaseUser.uid,
                                email = firebaseUser.email.orEmpty(),
                                displayName = firebaseUser.displayName ?: "Aqua Guard User",
                                photoUrl = firebaseUser.photoUrl?.toString(),
                                familyMembers = 4,
                                dailyTargetLiters = 300,
                                connectedDevices = emptyList()
                            )
                        )
                    }
            } else {
                trySend(null)
            }
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<User> = runCatching {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        val firebaseUser = result.user ?: throw Exception("User is null")
        
        val userDoc = firestore.collection("users").document(firebaseUser.uid).get().await()
        if (!userDoc.exists()) {
            val userMap = mapOf(
                "uid" to firebaseUser.uid,
                "email" to firebaseUser.email,
                "displayName" to (firebaseUser.displayName ?: "Aqua Guard User"),
                "householdDetails" to mapOf("familyMembers" to 4, "dailyTargetLiters" to 300),
                "connectedDevices" to emptyList<String>()
            )
            firestore.collection("users").document(firebaseUser.uid).set(userMap).await()
        }
        
        User(
            uid = firebaseUser.uid,
            email = firebaseUser.email.orEmpty(),
            displayName = firebaseUser.displayName ?: "Aqua Guard User",
            photoUrl = firebaseUser.photoUrl?.toString(),
            familyMembers = userDoc.getLong("householdDetails.familyMembers")?.toInt() ?: 4,
            dailyTargetLiters = userDoc.getLong("householdDetails.dailyTargetLiters")?.toInt() ?: 300,
            @Suppress("UNCHECKED_CAST")
            connectedDevices = userDoc.get("connectedDevices") as? List<String> ?: emptyList()
        )
    }

    override suspend fun signUpWithEmail(email: String, password: String, displayName: String): Result<User> = runCatching {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = result.user ?: throw Exception("User creation failed")
        
        val userMap = mapOf(
            "uid" to firebaseUser.uid,
            "email" to firebaseUser.email,
            "displayName" to displayName,
            "householdDetails" to mapOf("familyMembers" to 4, "dailyTargetLiters" to 300),
            "connectedDevices" to emptyList<String>()
        )
        firestore.collection("users").document(firebaseUser.uid).set(userMap).await()
        
        User(
            uid = firebaseUser.uid,
            email = firebaseUser.email.orEmpty(),
            displayName = displayName,
            photoUrl = null,
            familyMembers = 4,
            dailyTargetLiters = 300,
            connectedDevices = emptyList()
        )
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override suspend fun updateHouseholdDetails(familyMembers: Int, dailyTargetLiters: Int): Result<Unit> = runCatching {
        val uid = firebaseAuth.currentUser?.uid ?: throw Exception("Not authenticated")
        firestore.collection("users").document(uid)
            .update(
                mapOf(
                    "householdDetails.familyMembers" to familyMembers,
                    "householdDetails.dailyTargetLiters" to dailyTargetLiters
                )
            ).await()
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = runCatching {
        firebaseAuth.sendPasswordResetEmail(email).await()
    }
}

package com.aquaguard.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquaguard.data.local.PreferencesManager
import com.aquaguard.domain.model.User
import com.aquaguard.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val currentUser: StateFlow<User?> = authRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    var isRememberMeEnabled: Boolean
        get() = preferencesManager.isRememberMeEnabled
        set(value) {
            preferencesManager.isRememberMeEnabled = value
        }

    val rememberedEmail: String
        get() = preferencesManager.rememberedEmail

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.signInWithEmail(email, password)
                .onSuccess {
                    if (preferencesManager.isRememberMeEnabled) {
                        preferencesManager.rememberedEmail = email
                    } else {
                        preferencesManager.rememberedEmail = ""
                    }
                    _authState.value = AuthState.Success(it)
                }
                .onFailure {
                    _authState.value = AuthState.Error(it.message ?: "Login failed")
                }
        }
    }

    fun signUp(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.signUpWithEmail(email, password, displayName)
                .onSuccess {
                    _authState.value = AuthState.Success(it)
                }
                .onFailure {
                    _authState.value = AuthState.Error(it.message ?: "Sign up failed")
                }
        }
    }

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.sendPasswordResetEmail(email)
                .onSuccess {
                    _authState.value = AuthState.Idle
                }
                .onFailure {
                    _authState.value = AuthState.Error(it.message ?: "Failed to send reset email")
                }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _authState.value = AuthState.Idle
        }
    }
}

sealed interface AuthState {
    object Idle : AuthState
    object Loading : AuthState
    data class Success(val user: User) : AuthState
    data class Error(val message: String) : AuthState
}

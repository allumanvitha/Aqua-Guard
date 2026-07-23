package com.aquaguard.presentation.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aquaguard.data.local.PreferencesManager;
import com.aquaguard.domain.model.User;
import com.aquaguard.domain.repository.AuthRepository;
import com.aquaguard.domain.repository.RepositoryCallback;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AuthViewModel extends ViewModel {
    private final AuthRepository authRepository;
    private final PreferencesManager preferencesManager;
    
    private final MutableLiveData<AuthState> authStateLiveData = new MutableLiveData<>(AuthState.idle());

    @Inject
    public AuthViewModel(AuthRepository authRepository, PreferencesManager preferencesManager) {
        this.authRepository = authRepository;
        this.preferencesManager = preferencesManager;
    }

    public LiveData<User> getCurrentUser() {
        return authRepository.getCurrentUser();
    }

    public LiveData<AuthState> getAuthState() {
        return authStateLiveData;
    }

    public boolean isRememberMeEnabled() {
        return preferencesManager.isRememberMeEnabled();
    }

    public void setRememberMeEnabled(boolean value) {
        preferencesManager.setRememberMeEnabled(value);
    }

    public String getRememberedEmail() {
        return preferencesManager.getRememberedEmail();
    }

    public void signIn(String email, String password) {
        authStateLiveData.setValue(AuthState.loading());
        // Supports automatic registration of default admin credentials if they do not exist.
        authRepository.signInWithEmail(email, password, new RepositoryCallback<User>() {
            @Override
            public void onSuccess(User user) {
                if (preferencesManager.isRememberMeEnabled()) {
                    preferencesManager.setRememberedEmail(email);
                } else {
                    preferencesManager.setRememberedEmail("");
                }
                authStateLiveData.postValue(AuthState.success(user));
            }

            @Override
            public void onFailure(Exception e) {
                authStateLiveData.postValue(AuthState.error(e.getMessage() != null ? e.getMessage() : "Login failed"));
            }
        });
    }

    public void signUp(String email, String password, String displayName) {
        authStateLiveData.setValue(AuthState.loading());
        authRepository.signUpWithEmail(email, password, displayName, new RepositoryCallback<User>() {
            @Override
            public void onSuccess(User user) {
                authStateLiveData.postValue(AuthState.success(user));
            }

            @Override
            public void onFailure(Exception e) {
                authStateLiveData.postValue(AuthState.error(e.getMessage() != null ? e.getMessage() : "Registration failed"));
            }
        });
    }

    public void sendPasswordResetEmail(String email) {
        authStateLiveData.setValue(AuthState.loading());
        authRepository.sendPasswordResetEmail(email, new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                authStateLiveData.postValue(AuthState.idle());
            }

            @Override
            public void onFailure(Exception e) {
                authStateLiveData.postValue(AuthState.error(e.getMessage() != null ? e.getMessage() : "Failed to send reset email"));
            }
        });
    }

    public void signOut() {
        authRepository.signOut(new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                authStateLiveData.postValue(AuthState.idle());
            }

            @Override
            public void onFailure(Exception e) {
                // Ignore signOut failures
                authStateLiveData.postValue(AuthState.idle());
            }
        });
    }

    // Java Sealed-class equivalent
    public static class AuthState {
        public enum Status { IDLE, LOADING, SUCCESS, ERROR }
        private final Status status;
        private final User user;
        private final String message;

        private AuthState(Status status, User user, String message) {
            this.status = status;
            this.user = user;
            this.message = message;
        }

        public static AuthState idle() { return new AuthState(Status.IDLE, null, ""); }
        public static AuthState loading() { return new AuthState(Status.LOADING, null, ""); }
        public static AuthState success(User user) { return new AuthState(Status.SUCCESS, user, ""); }
        public static AuthState error(String msg) { return new AuthState(Status.ERROR, null, msg); }

        public Status getStatus() { return status; }
        public User getUser() { return user; }
        public String getMessage() { return message; }
    }
}

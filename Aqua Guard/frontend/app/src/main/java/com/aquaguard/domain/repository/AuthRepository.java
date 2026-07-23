package com.aquaguard.domain.repository;

import androidx.lifecycle.LiveData;
import com.aquaguard.domain.model.User;

public interface AuthRepository {
    String DEFAULT_ADMIN_EMAIL = "admin@aquaguard.com";
    String DEFAULT_ADMIN_PASSWORD = "Admin@123";
    LiveData<User> getCurrentUser();
    void signInWithEmail(String email, String password, RepositoryCallback<User> callback);
    void signUpWithEmail(String email, String password, String displayName, RepositoryCallback<User> callback);
    void signOut(RepositoryCallback<Void> callback);
    void updateHouseholdDetails(int familyMembers, int dailyTargetLiters, RepositoryCallback<Void> callback);
    void sendPasswordResetEmail(String email, RepositoryCallback<Void> callback);
}

package com.aquaguard.presentation.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.aquaguard.domain.model.User;
import com.aquaguard.domain.repository.AuthRepository;
import com.aquaguard.domain.repository.RepositoryCallback;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProfileViewModel extends ViewModel {
    private final AuthRepository authRepository;

    @Inject
    public ProfileViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public LiveData<User> getCurrentUser() {
        return authRepository.getCurrentUser();
    }

    public void updateHouseholdDetails(int familyMembers, int dailyTargetLiters) {
        authRepository.updateHouseholdDetails(familyMembers, dailyTargetLiters, new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                // Successfully updated
            }

            @Override
            public void onFailure(Exception e) {
                // Log or handle error
            }
        });
    }

    public void signOut() {
        authRepository.signOut(new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {}
            @Override
            public void onFailure(Exception e) {}
        });
    }
}

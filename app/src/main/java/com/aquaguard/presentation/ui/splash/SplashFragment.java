package com.aquaguard.presentation.ui.splash;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.aquaguard.R;
import com.aquaguard.databinding.FragmentSplashBinding;
import com.aquaguard.presentation.auth.AuthViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SplashFragment extends Fragment {
    private FragmentSplashBinding binding;
    private AuthViewModel authViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSplashBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (binding == null) return;
            
            authViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    Navigation.findNavController(requireView()).navigate(R.id.action_splash_to_main);
                } else {
                    Navigation.findNavController(requireView()).navigate(R.id.action_splash_to_onboarding);
                }
            });
        }, 2000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

package com.aquaguard.presentation.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.aquaguard.databinding.FragmentForgotPasswordBinding;
import com.aquaguard.presentation.auth.AuthViewModel;
import com.aquaguard.presentation.auth.AuthViewModel.AuthState;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ForgotPasswordFragment extends Fragment {

    private FragmentForgotPasswordBinding binding;
    private AuthViewModel authViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        binding.btnResetPassword.setOnClickListener(v -> {

            String email = binding.etEmail.getText() != null
                    ? binding.etEmail.getText().toString().trim()
                    : "";

            if (email.isEmpty()) {
                Toast.makeText(getContext(),
                        "Please enter your email.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Note: Password resets for default administrator admin@aquaguard.com should be handled with care.
            authViewModel.sendPasswordResetEmail(email);
        });

        authViewModel.getAuthState().observe(getViewLifecycleOwner(), state -> {

            switch (state.getStatus()) {

                case LOADING:
                    binding.loadingOverlay.setVisibility(View.VISIBLE);
                    break;

                case SUCCESS:
                    binding.loadingOverlay.setVisibility(View.GONE);
                    Toast.makeText(
                            getContext(),
                            "Password reset link sent to your email.",
                            Toast.LENGTH_LONG
                    ).show();
                    Navigation.findNavController(requireView()).navigateUp();
                    break;

                case ERROR:
                    binding.loadingOverlay.setVisibility(View.GONE);
                    Toast.makeText(
                            getContext(),
                            state.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                    break;

                case IDLE:
                default:
                    binding.loadingOverlay.setVisibility(View.GONE);
                    break;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
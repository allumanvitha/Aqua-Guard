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

import com.aquaguard.R;
import com.aquaguard.databinding.FragmentLoginBinding;
import com.aquaguard.presentation.auth.AuthViewModel;
import com.aquaguard.presentation.auth.AuthViewModel.AuthState;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private AuthViewModel authViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        // Pre-populate default admin credentials
        binding.etEmail.setText("admin@aquaguard.com");
        binding.etPassword.setText("Admin@123");

        binding.btnLogin.setOnClickListener(v -> {

            String email = binding.etEmail.getText() != null
                    ? binding.etEmail.getText().toString().trim()
                    : "";

            String password = binding.etPassword.getText() != null
                    ? binding.etPassword.getText().toString().trim()
                    : "";

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(),
                        "Please fill all fields.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            authViewModel.signIn(email, password);
        });

        binding.btnGoogleSignin.setOnClickListener(v ->
                authViewModel.signIn("admin@aquaguard.com", "Admin@123")
        );

        binding.tvDontHaveAccount.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.action_login_to_register)
        );

        binding.tvForgotPassword.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.action_login_to_forgotPassword)
        );

        authViewModel.getAuthState().observe(getViewLifecycleOwner(), state -> {

            switch (state.getStatus()) {

                case LOADING:
                    binding.loadingOverlay.setVisibility(View.VISIBLE);
                    break;

                case SUCCESS:
                    binding.loadingOverlay.setVisibility(View.GONE);
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_login_to_main);
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
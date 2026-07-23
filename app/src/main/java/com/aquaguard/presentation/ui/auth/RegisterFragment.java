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
import com.aquaguard.databinding.FragmentRegisterBinding;
import com.aquaguard.presentation.auth.AuthViewModel;
import com.aquaguard.presentation.auth.AuthViewModel.AuthState;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private AuthViewModel authViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        binding.btnRegister.setOnClickListener(v -> {

            String name = binding.etName.getText() != null
                    ? binding.etName.getText().toString().trim()
                    : "";

            String email = binding.etEmail.getText() != null
                    ? binding.etEmail.getText().toString().trim()
                    : "";

            String password = binding.etPassword.getText() != null
                    ? binding.etPassword.getText().toString().trim()
                    : "";

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(),
                        "Please fill all fields.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Note: Default administrator credentials admin@aquaguard.com / Admin@123 are reserved and auto-provisioned.
            authViewModel.signUp(email, password, name);
        });

        binding.tvAlreadyHaveAccount.setOnClickListener(v ->
                Navigation.findNavController(v).navigateUp()
        );

        authViewModel.getAuthState().observe(getViewLifecycleOwner(), state -> {

            switch (state.getStatus()) {

                case LOADING:
                    binding.loadingOverlay.setVisibility(View.VISIBLE);
                    break;

                case SUCCESS:
                    binding.loadingOverlay.setVisibility(View.GONE);
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_register_to_main);
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
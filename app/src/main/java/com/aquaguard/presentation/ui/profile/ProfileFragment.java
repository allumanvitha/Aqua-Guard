package com.aquaguard.presentation.ui.profile;

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
import com.aquaguard.databinding.FragmentProfileBinding;
import com.aquaguard.presentation.profile.ProfileViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        observeViewModel();
        setupClickListeners();
    }

    private void observeViewModel() {
        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null) {
                // Navigate to Login Fragment
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                        .navigate(R.id.action_main_to_login);
                return;
            }

            String name = user.getName();
            binding.tvProfileEmail.setText(user.getEmail() != null ? user.getEmail() : "");

            if (name != null && !name.isEmpty()) {
                binding.tvProfileName.setText(name);
                binding.tvAvatarPlaceholder.setText(name.substring(0, 1).toUpperCase());
            } else {
                binding.tvProfileName.setText("Aqua Guard User");
                binding.tvAvatarPlaceholder.setText("U");
            }

            binding.etFamilyMembers.setText(String.valueOf(user.getFamilyMembers()));
            binding.etDailyTarget.setText(String.valueOf(user.getDailyTargetLiters()));
        });
    }

    private void setupClickListeners() {
        binding.btnSaveProfile.setOnClickListener(v -> {
            String familyStr = binding.etFamilyMembers.getText() != null ? binding.etFamilyMembers.getText().toString().trim() : "";
            String targetStr = binding.etDailyTarget.getText() != null ? binding.etDailyTarget.getText().toString().trim() : "";

            if (familyStr.isEmpty() || targetStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all configuration fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int family = Integer.parseInt(familyStr);
                int target = Integer.parseInt(targetStr);
                viewModel.updateHouseholdDetails(family, target);
                Toast.makeText(getContext(), "Household limits updated successfully.", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter valid numbers.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnSettings.setOnClickListener(v -> {
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.action_main_to_settings);
        });

        binding.btnSignOut.setOnClickListener(v -> {
            viewModel.signOut();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

package com.aquaguard.presentation.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.aquaguard.databinding.FragmentSettingsBinding;
import com.aquaguard.presentation.settings.SettingsViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    private SettingsViewModel viewModel;
    private boolean isInitialLanguageSelect = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        binding.ivBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        setupPreferenceOptions();
        setupInfoDialogClicks();
    }

    private void setupPreferenceOptions() {
        // Observe Theme Mode
        viewModel.getThemeMode().observe(getViewLifecycleOwner(), mode -> {
            boolean isDark = mode != null && mode.equals("dark");
            binding.switchThemeDark.setChecked(isDark);
        });

        binding.switchThemeDark.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String targetMode = isChecked ? "dark" : "light";
            viewModel.setThemeMode(targetMode);
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        // Observe Alerts
        viewModel.getIsLeakAlertEnabled().observe(getViewLifecycleOwner(), enabled -> 
            binding.switchLeakAlerts.setChecked(enabled != null && enabled)
        );

        binding.switchLeakAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> 
            viewModel.setLeakAlertEnabled(isChecked)
        );

        viewModel.getIsOverflowAlertEnabled().observe(getViewLifecycleOwner(), enabled -> 
            binding.switchOverflowAlerts.setChecked(enabled != null && enabled)
        );

        binding.switchOverflowAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> 
            viewModel.setOverflowAlertEnabled(isChecked)
        );

        // Observe Language
        viewModel.getAppLanguage().observe(getViewLifecycleOwner(), language -> {
            if (language != null) {
                int index = 0; // Default English
                if (language.equalsIgnoreCase("spanish")) index = 1;
                else if (language.equalsIgnoreCase("hindi")) index = 2;
                binding.spinnerLanguage.setSelection(index);
            }
        });

        binding.spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isInitialLanguageSelect) {
                    isInitialLanguageSelect = false;
                    return;
                }
                String[] langs = {"english", "spanish", "hindi"};
                viewModel.setAppLanguage(langs[position]);
                Toast.makeText(getContext(), "Language changed to: " + parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupInfoDialogClicks() {
        binding.tvAboutBtn.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Aqua Guard v1.0.0 - AI Smart Water Monitor.", Toast.LENGTH_SHORT).show()
        );

        binding.tvPrivacyBtn.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Opening privacy policy guidelines...", Toast.LENGTH_SHORT).show()
        );

        binding.tvTermsBtn.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Opening application Terms of Service...", Toast.LENGTH_SHORT).show()
        );

        binding.tvHelpContactBtn.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Contact support at: support@aquaguard.io", Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

package com.aquaguard.presentation.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.aquaguard.R;
import com.aquaguard.databinding.FragmentMainShellBinding;
import com.aquaguard.presentation.ui.dashboard.DashboardFragment;
import com.aquaguard.presentation.ui.analytics.AnalyticsFragment;
import com.aquaguard.presentation.ui.alerts.AlertsFragment;
import com.aquaguard.presentation.ui.history.HistoryFragment;
import com.aquaguard.presentation.ui.profile.ProfileFragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainFragment extends Fragment {
    private FragmentMainShellBinding binding;

    private Fragment dashboardFragment;
    private Fragment analyticsFragment;
    private Fragment alertsFragment;
    private Fragment historyFragment;
    private Fragment profileFragment;
    private Fragment activeFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainShellBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentManager fm = getChildFragmentManager();

        // Check if savedInstanceState exists to restore fragments
        if (savedInstanceState != null) {
            dashboardFragment = fm.findFragmentByTag("dashboard");
            analyticsFragment = fm.findFragmentByTag("analytics");
            alertsFragment = fm.findFragmentByTag("alerts");
            historyFragment = fm.findFragmentByTag("history");
            profileFragment = fm.findFragmentByTag("profile");

            // Determine active fragment
            if (dashboardFragment != null && !dashboardFragment.isHidden()) {
                activeFragment = dashboardFragment;
            } else if (analyticsFragment != null && !analyticsFragment.isHidden()) {
                activeFragment = analyticsFragment;
            } else if (alertsFragment != null && !alertsFragment.isHidden()) {
                activeFragment = alertsFragment;
            } else if (historyFragment != null && !historyFragment.isHidden()) {
                activeFragment = historyFragment;
            } else if (profileFragment != null && !profileFragment.isHidden()) {
                activeFragment = profileFragment;
            }
        }

        // Initialize fragments if not restored
        if (dashboardFragment == null) {
            dashboardFragment = new DashboardFragment();
            analyticsFragment = new AnalyticsFragment();
            alertsFragment = new AlertsFragment();
            historyFragment = new HistoryFragment();
            profileFragment = new ProfileFragment();

            activeFragment = dashboardFragment;

            fm.beginTransaction().add(R.id.tab_container, dashboardFragment, "dashboard").commit();
            fm.beginTransaction().add(R.id.tab_container, analyticsFragment, "analytics").hide(analyticsFragment).commit();
            fm.beginTransaction().add(R.id.tab_container, alertsFragment, "alerts").hide(alertsFragment).commit();
            fm.beginTransaction().add(R.id.tab_container, historyFragment, "history").hide(historyFragment).commit();
            fm.beginTransaction().add(R.id.tab_container, profileFragment, "profile").hide(profileFragment).commit();
        }

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment targetFragment = null;

            if (itemId == R.id.nav_dashboard) {
                targetFragment = dashboardFragment;
            } else if (itemId == R.id.nav_analytics) {
                targetFragment = analyticsFragment;
            } else if (itemId == R.id.nav_alerts) {
                targetFragment = alertsFragment;
            } else if (itemId == R.id.nav_history) {
                targetFragment = historyFragment;
            } else if (itemId == R.id.nav_profile) {
                targetFragment = profileFragment;
            }

            if (targetFragment != null && targetFragment != activeFragment) {
                fm.beginTransaction().hide(activeFragment).show(targetFragment).commit();
                activeFragment = targetFragment;
                return true;
            }
            return false;
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

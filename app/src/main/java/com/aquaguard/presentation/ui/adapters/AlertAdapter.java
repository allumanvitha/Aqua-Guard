package com.aquaguard.presentation.ui.adapters;

import android.graphics.Color;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aquaguard.databinding.ItemAlertBinding;
import com.aquaguard.domain.model.Alert;

import java.util.ArrayList;
import java.util.List;

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.ViewHolder> {
    private List<Alert> alerts = new ArrayList<>();
    private final OnAlertResolveListener listener;

    public interface OnAlertResolveListener {
        void onResolveClick(Alert alert);
    }

    public AlertAdapter(OnAlertResolveListener listener) {
        this.listener = listener;
    }

    public void setAlerts(List<Alert> newAlerts) {
        this.alerts = newAlerts != null ? newAlerts : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAlertBinding binding = ItemAlertBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Alert alert = alerts.get(position);

        holder.binding.tvAlertTitle.setText(alert.getTitle().toUpperCase());
        holder.binding.tvAlertMsg.setText(alert.getMessage());

        // Format timestamp relative
        CharSequence relTime = DateUtils.getRelativeTimeSpanString(
                alert.getTimestamp(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS
        );
        holder.binding.tvAlertTime.setText(relTime);

        // Customize severity indicators
        String severity = alert.getSeverity() != null ? alert.getSeverity().name() : "INFO";
        if (severity.equals("CRITICAL")) {
            holder.binding.vSeverityIndicator.setBackgroundColor(Color.parseColor("#d32f2f"));
        } else if (severity.equals("WARNING")) {
            holder.binding.vSeverityIndicator.setBackgroundColor(Color.parseColor("#ff9800"));
        } else {
            holder.binding.vSeverityIndicator.setBackgroundColor(Color.parseColor("#4caf50"));
        }

        // Toggle Resolve Button visibility
        if (alert.isActive() && severity.equals("CRITICAL")) {
            holder.binding.btnResolveAlert.setVisibility(View.VISIBLE);
            holder.binding.btnResolveAlert.setOnClickListener(v -> {
                if (listener != null) listener.onResolveClick(alert);
            });
        } else {
            holder.binding.btnResolveAlert.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return alerts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemAlertBinding binding;

        ViewHolder(ItemAlertBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

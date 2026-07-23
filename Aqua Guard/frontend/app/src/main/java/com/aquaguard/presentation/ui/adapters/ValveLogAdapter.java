package com.aquaguard.presentation.ui.adapters;

import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aquaguard.databinding.ItemValveLogBinding;
import com.aquaguard.domain.model.ValveLog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ValveLogAdapter extends RecyclerView.Adapter<ValveLogAdapter.ViewHolder> {
    private List<ValveLog> logs = new ArrayList<>();

    public void setLogs(List<ValveLog> newLogs) {
        this.logs = newLogs != null ? newLogs : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemValveLogBinding binding = ItemValveLogBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ValveLog log = logs.get(position);

        String action = log.getAction() != null ? log.getAction().toUpperCase() : "CLOSE";
        boolean isOpen = action.contains("OPEN");

        if (isOpen) {
            holder.binding.tvActionIndicator.setText("ON");
            holder.binding.tvActionIndicator.getBackground().setTint(Color.parseColor("#4caf50"));
            holder.binding.tvLogActionTitle.setText("VALVE OPENED");
        } else {
            holder.binding.tvActionIndicator.setText("OFF");
            holder.binding.tvActionIndicator.getBackground().setTint(Color.parseColor("#f44336"));
            holder.binding.tvLogActionTitle.setText("VALVE CLOSED");
        }

        holder.binding.tvLogReason.setText("Reason: " + log.getReason());
        holder.binding.tvLogTriggeredBy.setText("Triggered by: " + log.getTriggeredBy().toUpperCase());

        // Parse absolute timestamp
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(log.getTimestamp());
        String dateStr = DateFormat.format("hh:mm a", cal).toString();
        holder.binding.tvLogTime.setText(dateStr);
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemValveLogBinding binding;

        ViewHolder(ItemValveLogBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

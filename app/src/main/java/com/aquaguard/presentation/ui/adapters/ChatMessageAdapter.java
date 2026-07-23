package com.aquaguard.presentation.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aquaguard.databinding.ItemChatMessageBinding;
import com.aquaguard.presentation.ai.AIAssistantViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {
    private List<AIAssistantViewModel.Message> messages = new ArrayList<>();

    public void setMessages(List<AIAssistantViewModel.Message> newMessages) {
        this.messages = newMessages != null ? newMessages : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChatMessageBinding binding = ItemChatMessageBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AIAssistantViewModel.Message msg = messages.get(position);

        if (msg.isUser()) {
            holder.binding.llAiContainer.setVisibility(View.GONE);
            holder.binding.llUserContainer.setVisibility(View.VISIBLE);
            holder.binding.tvUserMsg.setText(msg.getText());
        } else {
            holder.binding.llUserContainer.setVisibility(View.GONE);
            holder.binding.llAiContainer.setVisibility(View.VISIBLE);
            holder.binding.tvAiMsg.setText(msg.getText());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemChatMessageBinding binding;

        ViewHolder(ItemChatMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

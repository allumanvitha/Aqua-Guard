package com.aquaguard.presentation.ui.ai;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.aquaguard.databinding.FragmentAiAssistantBinding;
import com.aquaguard.presentation.ai.AIAssistantViewModel;
import com.aquaguard.presentation.ui.adapters.ChatMessageAdapter;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AIAssistantFragment extends Fragment {
    private FragmentAiAssistantBinding binding;
    private AIAssistantViewModel viewModel;
    private ChatMessageAdapter chatAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAiAssistantBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AIAssistantViewModel.class);

        binding.ivBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        setupChatRecyclerView();
        observeViewModel();

        binding.btnSendChat.setOnClickListener(v -> {
            String text = binding.etChatInput.getText() != null ? binding.etChatInput.getText().toString().trim() : "";
            if (!text.isEmpty()) {
                viewModel.sendMessage(text);
                binding.etChatInput.setText("");
            }
        });
    }

    private void setupChatRecyclerView() {
        chatAdapter = new ChatMessageAdapter();
        binding.rvChatMessages.setAdapter(chatAdapter);
    }

    private void observeViewModel() {
        viewModel.getMessages().observe(getViewLifecycleOwner(), list -> {
            chatAdapter.setMessages(list != null ? list : new ArrayList<>());
            if (list != null && !list.isEmpty()) {
                binding.rvChatMessages.smoothScrollToPosition(list.size() - 1);
            }
        });

        viewModel.getIsTyping().observe(getViewLifecycleOwner(), typing -> {
            binding.tvTypingIndicator.setVisibility(typing != null && typing ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

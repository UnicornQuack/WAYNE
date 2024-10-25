package com.example.chatapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ChatMessage> chatMessages;
    private final String currentUserEmail;

    private static final int VIEW_TYPE_MY_MESSAGE = 1;
    private static final int VIEW_TYPE_OTHER_MESSAGE = 2;

    public ChatAdapter(List<ChatMessage> chatMessages, String currentUserEmail) {
        this.chatMessages = chatMessages;
        this.currentUserEmail = currentUserEmail;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        if (chatMessage.getSender().equals(currentUserEmail)) {
            return VIEW_TYPE_MY_MESSAGE;
        } else {
            return VIEW_TYPE_OTHER_MESSAGE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MY_MESSAGE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_my_chat_message, parent, false);
            return new MyMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_other_chat_message, parent, false);
            return new OtherMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        if (holder instanceof MyMessageViewHolder) {
            ((MyMessageViewHolder) holder).bind(chatMessage);
        } else {
            ((OtherMessageViewHolder) holder).bind(chatMessage);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public static class MyMessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;

        public MyMessageViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.my_message_text_view);
        }

        public void bind(ChatMessage chatMessage) {
            messageTextView.setText(chatMessage.getMessage());
        }
    }

    public static class OtherMessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;

        public OtherMessageViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.other_message_text_view);
        }

        public void bind(ChatMessage chatMessage) {
            messageTextView.setText(chatMessage.getMessage());
        }
    }
}

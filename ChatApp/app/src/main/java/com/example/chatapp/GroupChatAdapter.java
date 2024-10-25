package com.example.chatapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class GroupChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<GroupChatMessage> chatMessages;
    private final String currentUserUsername;
    private String currentUserUsername_2;

    private static final int VIEW_TYPE_MY_MESSAGE = 1;
    private static final int VIEW_TYPE_OTHER_MESSAGE = 2;

    public GroupChatAdapter(List<GroupChatMessage> chatMessages, String currentUserUsername) {
        this.chatMessages = chatMessages;
        this.currentUserUsername = currentUserUsername;
    }

    @Override
    public int getItemViewType(int position) {
        GroupChatMessage chatMessage = chatMessages.get(position);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").whereEqualTo("email", currentUserUsername)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        currentUserUsername_2 = task.getResult().getDocuments().get(0).getString("username");
                    }
                    notifyDataSetChanged();
                });
        if (chatMessage.getSenderName().equals(currentUserUsername_2)) {
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
                    .inflate(R.layout.item_group_my_chat_message, parent, false);
            return new MyMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_group_other_chat_message, parent, false);
            return new OtherMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        GroupChatMessage chatMessage = chatMessages.get(position);
        if (holder instanceof MyMessageViewHolder) {
            ((MyMessageViewHolder) holder).messageTextView.setText(chatMessage.getMessage());
        } else if (holder instanceof OtherMessageViewHolder) {
            ((OtherMessageViewHolder) holder).senderNameTextView.setText(chatMessage.getSenderName());
            ((OtherMessageViewHolder) holder).messageTextView.setText(chatMessage.getMessage());
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
            messageTextView = itemView.findViewById(R.id.message_text_view);
        }
    }

    public static class OtherMessageViewHolder extends RecyclerView.ViewHolder {
        public TextView senderNameTextView;
        public TextView messageTextView;

        public OtherMessageViewHolder(View itemView) {
            super(itemView);
            senderNameTextView = itemView.findViewById(R.id.sender_name_text_view);
            messageTextView = itemView.findViewById(R.id.message_text_view);
        }
    }
}

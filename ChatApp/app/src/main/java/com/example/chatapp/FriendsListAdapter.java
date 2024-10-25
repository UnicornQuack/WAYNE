package com.example.chatapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.FriendViewHolder> {

    private final List<String> friendsList;
    private OnFriendDeleteListener onFriendDeleteListener;
    private OnFriendChatListener onFriendChatListener;

    public FriendsListAdapter(List<String> friendsList, OnFriendDeleteListener onFriendDeleteListener) {
        this.friendsList = friendsList;
        this.onFriendDeleteListener = onFriendDeleteListener;
    }

    public FriendsListAdapter(List<String> friendsList, OnFriendChatListener onFriendChatListener) {
        this.friendsList = friendsList;
        this.onFriendChatListener = onFriendChatListener;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        String friendEmail = friendsList.get(position);
        holder.tvFriendEmail.setText(friendEmail);

        if (onFriendDeleteListener != null) {
            holder.itemView.setOnLongClickListener(v -> {
                onFriendDeleteListener.onDelete(friendEmail);
                return true;
            });
        }

        if (onFriendChatListener != null) {
            holder.itemView.setOnClickListener(v -> onFriendChatListener.onChat(friendEmail));
        }
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        public TextView tvFriendEmail;

        public FriendViewHolder(View itemView) {
            super(itemView);
            tvFriendEmail = itemView.findViewById(R.id.tv_friend_email);
        }
    }

    public interface OnFriendDeleteListener {
        void onDelete(String email);
    }

    public interface OnFriendChatListener {
        void onChat(String email);
    }
}

package com.example.chatapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SelectFriendsAdapter extends RecyclerView.Adapter<SelectFriendsAdapter.FriendViewHolder> {

    private final List<String> friendsList;
    private final Set<String> selectedFriends = new HashSet<>();

    public SelectFriendsAdapter(List<String> friendsList) {
        this.friendsList = friendsList;
    }

    public Set<String> getSelectedFriends() {
        return selectedFriends;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend_select, parent, false);
        return new FriendViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        String friendEmail = friendsList.get(position);
        holder.tvFriendEmail.setText(friendEmail);

        if (selectedFriends.contains(friendEmail)) {
            holder.ivCheckMark.setVisibility(View.VISIBLE);
        } else {
            holder.ivCheckMark.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (selectedFriends.contains(friendEmail)) {
                selectedFriends.remove(friendEmail);
            } else {
                selectedFriends.add(friendEmail);
            }
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        public TextView tvFriendEmail;
        public ImageView ivCheckMark;

        public FriendViewHolder(View itemView) {
            super(itemView);
            tvFriendEmail = itemView.findViewById(R.id.tv_friend_email);
            ivCheckMark = itemView.findViewById(R.id.iv_check_mark);
        }
    }
}

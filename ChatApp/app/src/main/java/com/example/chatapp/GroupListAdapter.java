package com.example.chatapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.GroupViewHolder> {

    private final List<String> groupList;
    private final OnGroupInteractionListener onGroupInteractionListener;

    public GroupListAdapter(List<String> groupList, OnGroupInteractionListener onGroupInteractionListener) {
        this.groupList = groupList;
        this.onGroupInteractionListener = onGroupInteractionListener;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        String groupName = groupList.get(position);
        holder.tvGroupName.setText(groupName);
        holder.itemView.setOnClickListener(v -> onGroupInteractionListener.onOpenGroupChat(groupName));
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        public TextView tvGroupName;

        public GroupViewHolder(View itemView) {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.tv_group_name);
        }
    }

    public interface OnGroupInteractionListener {
        void onOpenGroupChat(String groupName);
    }
}

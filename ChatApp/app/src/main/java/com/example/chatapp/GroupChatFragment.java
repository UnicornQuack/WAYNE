package com.example.chatapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroupChatFragment extends Fragment {

    private static final int REQUEST_CODE_SELECT_FRIENDS = 1001;
    private RecyclerView recyclerViewGroups;
    private Button btnCreateGroup;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GroupListAdapter groupListAdapter;
    private List<String> groupList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_chat, container, false);

        recyclerViewGroups = view.findViewById(R.id.recycler_view_groups);
        btnCreateGroup = view.findViewById(R.id.btn_create_group);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        groupList = new ArrayList<>();
        groupListAdapter = new GroupListAdapter(groupList, this::openGroupChat);
        recyclerViewGroups.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewGroups.setAdapter(groupListAdapter);

        loadGroupList();

        btnCreateGroup.setOnClickListener(v -> createNewGroup());

        return view;
    }

    private void loadGroupList() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            CollectionReference groupsRef = db.collection("group_chats");

            groupsRef.whereArrayContains("members", user.getEmail())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        groupList.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String groupName = document.getString("groupName");
                            groupList.add(groupName);
                        }
                        groupListAdapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "無法加載群組列表", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void createNewGroup() {
        Intent intent = new Intent(getActivity(), SelectFriendsActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SELECT_FRIENDS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_FRIENDS && resultCode == Activity.RESULT_OK) {
            List<String> selectedFriends = data.getStringArrayListExtra("selectedFriends");
            String groupName = data.getStringExtra("groupName");
            createGroupInFirestore(groupName, selectedFriends);
        }
    }

    private void createGroupInFirestore(String groupName, List<String> selectedFriends) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference newGroupRef = db.collection("group_chats").document();

        selectedFriends.add(currentUser.getEmail());

        newGroupRef.set(new HashMap<String, Object>() {{
            put("groupName", groupName);
            put("members", selectedFriends);
            put("createdBy", currentUser.getEmail());
            put("timestamp", System.currentTimeMillis());
        }}).addOnSuccessListener(aVoid -> {
            Toast.makeText(getActivity(), "群組創建成功", Toast.LENGTH_SHORT).show();
            openGroupChat(newGroupRef.getId());
        }).addOnFailureListener(e -> Toast.makeText(getActivity(), "群組創建失敗", Toast.LENGTH_SHORT).show());
    }

    private void openGroupChat(String groupId) {
        Intent intent = new Intent(getActivity(), GroupChatActivity.class);
        intent.putExtra("groupId", groupId);
        startActivity(intent);
    }
}

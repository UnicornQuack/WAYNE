package com.example.chatapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SelectFriendsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFriends;
    private SelectFriendsAdapter selectFriendsAdapter;
    private List<String> friendsList;
    private EditText etGroupName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friends);

        etGroupName = findViewById(R.id.et_group_name);
        recyclerViewFriends = findViewById(R.id.recycler_view_friends);
        Button btnCreateGroup = findViewById(R.id.btn_create_group);

        friendsList = new ArrayList<>();
        selectFriendsAdapter = new SelectFriendsAdapter(friendsList);
        recyclerViewFriends.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFriends.setAdapter(selectFriendsAdapter);

        loadFriendsList();

        btnCreateGroup.setOnClickListener(v -> {
            String groupName = etGroupName.getText().toString().trim();
            if (TextUtils.isEmpty(groupName)) {
                Toast.makeText(this, "請輸入群組名稱", Toast.LENGTH_SHORT).show();
                return;
            }
            List<String> selectedFriends = new ArrayList<>(selectFriendsAdapter.getSelectedFriends());
            if (selectedFriends.isEmpty()) {
                Toast.makeText(this, "請選擇至少一位好友", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent resultIntent = new Intent();
            resultIntent.putStringArrayListExtra("selectedFriends", new ArrayList<>(selectedFriends));
            resultIntent.putExtra("groupName", groupName);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });
    }

    private void loadFriendsList() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (user != null) {
            String uid = user.getUid();
            DocumentReference docRef = db.collection("users").document(uid);

            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    List<String> friends = (List<String>) documentSnapshot.get("friends");
                    if (friends != null) {
                        friendsList.clear();
                        friendsList.addAll(friends);
                        selectFriendsAdapter.notifyDataSetChanged();
                    }
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "無法加載好友列表", Toast.LENGTH_SHORT).show();
            });
        }
    }
}

package com.example.chatapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private EditText etUsername, etEmail, etPhone, etID, etFriendEmail;
    private Button btnSave, btnAddFriend, btnLogout;
    private RecyclerView recyclerFriends;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FriendsListAdapter friendsListAdapter;
    private List<String> friendsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        etUsername = view.findViewById(R.id.profile_username);
        etEmail = view.findViewById(R.id.profile_email);
        etPhone = view.findViewById(R.id.profile_phone);
        etID = view.findViewById(R.id.profile_ID);
        etFriendEmail = view.findViewById(R.id.friend_email);
        btnSave = view.findViewById(R.id.profile_save_button);
        btnAddFriend = view.findViewById(R.id.add_friend_button);
        btnLogout = view.findViewById(R.id.logout_button);
        recyclerFriends = view.findViewById(R.id.friend_recycler_view);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        friendsList = new ArrayList<>();
        friendsListAdapter = new FriendsListAdapter(friendsList, (FriendsListAdapter.OnFriendDeleteListener) this::removeFriend);
        recyclerFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerFriends.setAdapter(friendsListAdapter);

        loadUserProfile();
        loadFriendsList();

        btnSave.setOnClickListener(v -> saveUserProfile());
        btnAddFriend.setOnClickListener(v -> addFriend());
        btnLogout.setOnClickListener(v -> logout());

        return view;
    }

    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DocumentReference docRef = db.collection("users").document(uid);

            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String username = documentSnapshot.getString("username");
                    String email = documentSnapshot.getString("email");
                    String phone = documentSnapshot.getString("phone");
                    String id = documentSnapshot.getString("id");

                    etUsername.setText(username);
                    etEmail.setText(email);
                    etPhone.setText(phone);
                    etID.setText(id);
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(getActivity(), "無法加載用戶資料", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void saveUserProfile() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String id = etID.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(id)) {
            Toast.makeText(getActivity(), "所有欄位都是必填的", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DocumentReference userDocRef = db.collection("users").document(uid);
            CollectionReference idsCollectionRef = db.collection("ids");

            // 檢查 ids 集合中是否已經存在相同的 ID
            idsCollectionRef.whereEqualTo("id", id).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        // 如果 ID 已經存在，顯示錯誤訊息
                        Toast.makeText(getActivity(), "此ID已有人取", Toast.LENGTH_SHORT).show();
                    } else {
                        // 如果 ID 不存在，繼續保存用戶資料
                        Map<String, Object> userUpdates = new HashMap<>();
                        userUpdates.put("username", username);
                        userUpdates.put("email", email);
                        userUpdates.put("phone", phone);
                        userUpdates.put("id", id);

                        // 更新用戶的基本資料
                        userDocRef.update(userUpdates)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getActivity(), "個人資料已更新", Toast.LENGTH_SHORT).show();

                                    // 更新 ids 集合中的該用戶的 ID
                                    DocumentReference idsDocRef = idsCollectionRef.document(uid);
                                    Map<String, Object> idUpdate = new HashMap<>();
                                    idUpdate.put("id", id);

                                    idsDocRef.set(idUpdate)
                                            .addOnSuccessListener(aVoid1 -> {
                                                Toast.makeText(getActivity(), "ID 已更新", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(getActivity(), "更新 ID 失敗", Toast.LENGTH_SHORT).show();
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getActivity(), "更新失敗", Toast.LENGTH_SHORT).show();
                                });
                    }
                } else {
                    Toast.makeText(getActivity(), "檢查 ID 時出錯", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



    private void addFriend() {
        String friendEmail = etFriendEmail.getText().toString().trim();

        if (TextUtils.isEmpty(friendEmail)) {
            Toast.makeText(getActivity(), "請輸入好友的電子郵件", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(friendEmail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            DocumentReference docRef = db.collection("users").document(uid);

                            docRef.update("friends", FieldValue.arrayUnion(friendEmail))
                                    .addOnSuccessListener(aVoid -> {
                                        friendsList.add(friendEmail);
                                        friendsListAdapter.notifyItemInserted(friendsList.size() - 1);
                                        etFriendEmail.setText("");
                                        Toast.makeText(getActivity(), "好友已添加", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getActivity(), "添加好友失敗", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(getActivity(), "檢查電子郵件時出錯", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeFriend(String email) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DocumentReference docRef = db.collection("users").document(uid);

            docRef.update("friends", FieldValue.arrayRemove(email))
                    .addOnSuccessListener(aVoid -> {
                        int index = friendsList.indexOf(email);
                        if (index >= 0) {
                            friendsList.remove(index);
                            friendsListAdapter.notifyItemRemoved(index);
                        }
                        Toast.makeText(getActivity(), "好友已刪除", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "刪除好友失敗", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void loadFriendsList() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DocumentReference docRef = db.collection("users").document(uid);

            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    List<String> friends = (List<String>) documentSnapshot.get("friends");
                    if (friends != null) {
                        friendsList.clear();
                        friendsList.addAll(friends);
                        friendsListAdapter.notifyDataSetChanged();
                    }
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(getActivity(), "無法加載好友列表", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void logout() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ChatApp", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        mAuth.signOut();

        // 跳轉到 LoginActivity 並清除活動堆棧
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}

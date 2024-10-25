package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class PersonalChatFragment extends Fragment {

    private RecyclerView recyclerViewFriends;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FriendsListAdapter friendsListAdapter;
    private List<String> friendsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_chat, container, false);

        recyclerViewFriends = view.findViewById(R.id.recycler_view_friends);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        friendsList = new ArrayList<>();
        friendsListAdapter = new FriendsListAdapter(friendsList, (FriendsListAdapter.OnFriendDeleteListener) this::openChatWithFriend);
        recyclerViewFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewFriends.setAdapter(friendsListAdapter);

        loadFriendsList();

        return view;
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

    private void openChatWithFriend(String friendEmail) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("friendEmail", friendEmail);
        startActivity(intent);
    }
}

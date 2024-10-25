package com.example.chatapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupChatActivity extends AppCompatActivity {

    private RecyclerView recyclerViewChat;
    private EditText etMessageInput;
    private Button btnSendMessage;
    private GroupChatAdapter groupChatAdapter;
    private List<GroupChatMessage> chatMessages;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        recyclerViewChat = findViewById(R.id.recycler_view_group_chat);
        etMessageInput = findViewById(R.id.et_group_message_input);
        btnSendMessage = findViewById(R.id.btn_send_group_message);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        groupId = getIntent().getStringExtra("groupId");

        chatMessages = new ArrayList<>();
        groupChatAdapter = new GroupChatAdapter(chatMessages, currentUser.getEmail());
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChat.setAdapter(groupChatAdapter);

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        loadChatMessages();
    }

    private void sendMessage() {
        String message = etMessageInput.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            return;
        }

        String uid = currentUser.getUid();
        DocumentReference userRef = db.collection("users").document(uid);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String username = documentSnapshot.getString("username");

                Map<String, Object> chatMessage = new HashMap<>();
                chatMessage.put("sender", currentUser.getEmail());
                chatMessage.put("message", message);
                chatMessage.put("senderName", username);
                chatMessage.put("timestamp", System.currentTimeMillis());

                CollectionReference chatRef = db.collection("group_chats").document(groupId).collection("messages");
                chatRef.add(chatMessage).addOnSuccessListener(documentReference -> etMessageInput.setText(""));
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "無法取得用戶名稱", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadChatMessages() {
        CollectionReference chatRef = db.collection("group_chats").document(groupId)
                .collection("messages");

        chatRef.orderBy("timestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                chatMessages.clear();
                for (QueryDocumentSnapshot doc : snapshots) {
                    GroupChatMessage chatMessage = doc.toObject(GroupChatMessage.class);
                    chatMessages.add(chatMessage);
                }
                groupChatAdapter.notifyDataSetChanged();
                recyclerViewChat.scrollToPosition(chatMessages.size() - 1);
            }
        });
    }
}

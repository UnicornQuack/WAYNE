package com.example.chatapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerViewChat;
    private EditText etMessageInput;
    private Button btnSendMessage;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private String friendEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerViewChat = findViewById(R.id.recycler_view_chat);
        etMessageInput = findViewById(R.id.et_message_input);
        btnSendMessage = findViewById(R.id.btn_send_message);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        friendEmail = getIntent().getStringExtra("friendEmail");

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages, currentUser.getEmail());
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChat.setAdapter(chatAdapter);

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

        CollectionReference chatRefCurrentUser = db.collection("chats").document(currentUser.getEmail())
                .collection(friendEmail);
        CollectionReference chatRefFriend = db.collection("chats").document(friendEmail)
                .collection(currentUser.getEmail());

        Map<String, Object> chatMessage = new HashMap<>();
        chatMessage.put("sender", currentUser.getEmail());
        chatMessage.put("message", message);
        chatMessage.put("timestamp", System.currentTimeMillis());

        chatRefCurrentUser.add(chatMessage);
        chatRefFriend.add(chatMessage).addOnSuccessListener(documentReference -> etMessageInput.setText(""));
    }

    private void loadChatMessages() {
        CollectionReference chatRef = db.collection("chats").document(currentUser.getEmail())
                .collection(friendEmail);

        chatRef.orderBy("timestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                chatMessages.clear();
                for (QueryDocumentSnapshot doc : snapshots) {
                    ChatMessage chatMessage = doc.toObject(ChatMessage.class);
                    chatMessages.add(chatMessage);
                }
                chatAdapter.notifyDataSetChanged();
                recyclerViewChat.scrollToPosition(chatMessages.size() - 1);
            }
        });
    }
}

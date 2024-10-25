package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText emailEditText, usernameEditText, passwordEditText, confirmPasswordEditText, phoneEditText;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailEditText = findViewById(R.id.email);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirmPassword);
        phoneEditText = findViewById(R.id.phone);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> createAccount(emailEditText.getText().toString(), passwordEditText.getText().toString(), usernameEditText.getText().toString(), phoneEditText.getText().toString(), confirmPasswordEditText.getText().toString()));
    }

    private void createAccount(String email, String password, String username, String phone, String confirmPassword) {
        Log.d(TAG, "CreateAccount:" + email);
        if (!validateForm(email, password, username, phone, confirmPassword)) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        saveUserToFirestore(user, username, email, phone);
                    } else {
                        Exception e = task.getException();
                        if (e instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(RegisterActivity.this, "此電子郵件已經註冊。", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(RegisterActivity.this, "無效的憑據。", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof FirebaseAuthWeakPasswordException) {
                            Toast.makeText(RegisterActivity.this, "密碼太弱。", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof FirebaseNetworkException) {
                            Toast.makeText(RegisterActivity.this, "網絡錯誤。", Toast.LENGTH_SHORT).show();
                        } else{
                            Toast.makeText(RegisterActivity.this, "註冊失敗，原因為" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void saveUserToFirestore(FirebaseUser user, String username, String email, String phone) {
        if (user != null) {
            String uid = user.getUid();
            User newUser = new User(uid, username, email, phone);

            db.collection("users").document(uid).set(newUser)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(RegisterActivity.this, "註冊成功，即將返回登入頁面！", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(() -> {
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }, 2000);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(RegisterActivity.this, "註冊失敗。", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private boolean validateForm(String email, String password, String username, String phone, String confirmPassword) {
        boolean valid = true;
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("必要。");
            valid = false;
        } else {
            emailEditText.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("必要。");
            valid = false;
        } else {
            passwordEditText.setError(null);
        }

        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError("必要。");
            valid = false;
        } else {
            usernameEditText.setError(null);
        }

        if (TextUtils.isEmpty(phone)) {
            phoneEditText.setError("必要。");
            valid = false;
        } else {
            phoneEditText.setError(null);
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordEditText.setError("必要。");
            valid = false;
        } else if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("密碼與確認密碼不相符。");
            valid = false;
        } else {
            confirmPasswordEditText.setError(null);
        }

        return valid;
    }

    public static class User {
        private String uid;
        private String username;
        private String email;
        private String phone;

        public User() {
        }

        public User(String uid, String username, String email, String phone) {
            this.uid = uid;
            this.username = username;
            this.email = email;
            this.phone = phone;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }
}

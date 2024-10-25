package com.example.chatapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 延遲1.5秒後檢查登入狀態
        new Handler().postDelayed(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences("ChatApp", MODE_PRIVATE);
            String userId = sharedPreferences.getString("user_id", null);
            String userEmail = sharedPreferences.getString("user_email", null);

            Intent intent;
            if (userId != null && userEmail != null) {
                // 如果已經登入，跳轉到 MainActivity
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                // 如果沒有登入，跳轉到 LoginActivity
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }, 1500); // 延遲1.5秒
    }
}

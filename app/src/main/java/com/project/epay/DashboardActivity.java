package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private String emailKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        emailKey = getIntent().getStringExtra("emailKey");
        if (emailKey == null || emailKey.isEmpty()) {
            finish();
            return;
        }

        ImageView profileIcon = findViewById(R.id.profileIcon);
        profileIcon.setOnClickListener(v -> {
            // Profile placeholder
        });

        findViewById(R.id.payAnyoneButton).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, Contacts.class);
            intent.putExtra("emailKey", emailKey);
            startActivity(intent);
        });

        findViewById(R.id.logoutButton).setOnClickListener(v -> finish());
    }
}

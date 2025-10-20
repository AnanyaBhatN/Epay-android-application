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
            // You can add profile page navigation here later
        });

        // "Pay Anyone" — goes to Contacts
        findViewById(R.id.payAnyoneButton).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, Contacts.class);
            intent.putExtra("emailKey", emailKey);
            startActivity(intent);
        });

        // Mobile Recharge button
        findViewById(R.id.mobileRechargeButton).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, Recharge.class);
            intent.putExtra("emailKey", emailKey); // pass logged-in user info
            startActivity(intent);
        });

        // ✅ Logout button — go back to MainActivity and clear back stack
        findViewById(R.id.logoutButton).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}

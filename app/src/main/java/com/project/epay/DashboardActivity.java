package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private String email;
    private String emailKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Get email from login activity
        email = getIntent().getStringExtra("email");

        // If no email found, return to login
        if (email == null || email.isEmpty()) {
            Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Convert email to valid Firebase key
        emailKey = email.replace(".", "_");

        // Profile icon → open user settings
        ImageView profileIcon = findViewById(R.id.profileIcon);
        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, UserSettingsActivity.class);
            intent.putExtra("email", email); // actual email (e.g., abc@gmail.com)
            intent.putExtra("emailKey", emailKey); // encoded key (e.g., abc@gmail_com)
            startActivity(intent);
        });

        // "Bank" button
        findViewById(R.id.bankButton).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, BankSelectionActivity.class);
            intent.putExtra("emailKey", emailKey); // pass logged-in user info
            startActivity(intent);
        });

        // "Pay Anyone" → Contacts
        findViewById(R.id.payAnyoneButton).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, Contacts.class);
            intent.putExtra("email", email);
            intent.putExtra("emailKey", emailKey);
            startActivity(intent);
        });

        // Mobile Recharge → Recharge Activity
        findViewById(R.id.mobileRechargeButton).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, Recharge.class);
            intent.putExtra("email", email);
            intent.putExtra("emailKey", emailKey);
            startActivity(intent);
        });

        // --- NEW CODE ---
        // "Offers & Rewards" button
        findViewById(R.id.offersRewardsButton).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, OfferMainActivity.class);
            intent.putExtra("emailKey", emailKey); // pass logged-in user info
            startActivity(intent);
        });
        // --- END OF NEW CODE ---


        // "Pay Anyone" → Open PayWithUpi Activity
        findViewById(R.id.payWithUpiButton).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, PayWithUpi.class);
            intent.putExtra("email", email);       // actual email
            intent.putExtra("emailKey", emailKey); // encoded key
            startActivity(intent);
        });

        // Logout → Back to login screen
        findViewById(R.id.logoutButton).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
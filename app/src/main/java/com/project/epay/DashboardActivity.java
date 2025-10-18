package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Header
        ImageView profileIcon = findViewById(R.id.profileIcon);
        profileIcon.setOnClickListener(v -> {
            // Placeholder for future activity
        });

        // Money Management
        findViewById(R.id.bankButton).setOnClickListener(v -> {
            // Placeholder for future activity
        });

        // Pay Anyone → Open Contacts page
        findViewById(R.id.payAnyoneButton).setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, ContactActivity.class));
        });

        findViewById(R.id.checkBalanceButton).setOnClickListener(v -> {
            // Placeholder for future activity
        });
        findViewById(R.id.addToWalletButton).setOnClickListener(v -> {
            // Placeholder for future activity
        });
        findViewById(R.id.transactionHistoryButton).setOnClickListener(v -> {
            // Placeholder for future activity
        });
        findViewById(R.id.payWithUpiButton).setOnClickListener(v -> {
            // Placeholder for future activity
        });

        // Recharge & Bills → Open Recharge page
        findViewById(R.id.mobileRechargeButton).setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, Recharge.class));
        });

        // Offers & Rewards
        findViewById(R.id.offersRewardsButton).setOnClickListener(v -> {
            // Placeholder for future activity
        });

        // Logout
        findViewById(R.id.logoutButton).setOnClickListener(v -> {
            // Placeholder for logout (to be implemented later)
        });
    }
}

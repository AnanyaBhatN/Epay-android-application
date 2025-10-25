package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;

public class DashboardActivity extends AppCompatActivity {

    private String email;
    private String emailKey;
    private TextView tvWalletBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tvWalletBalance = findViewById(R.id.tvWalletBalance);

        // Get email from previous activity
        email = getIntent().getStringExtra("email");

        if (email == null || email.isEmpty()) {
            Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Sanitize email for Firebase key
        emailKey = email.replace(".", "_");

        // Fetch wallet balance
        fetchWalletBalance();

        // Setup all dashboard buttons
        setupButtons();
    }

    private void fetchWalletBalance() {
        // fetch balance from Firebase (same as before)
        FirebaseDatabase.getInstance()
                .getReference("wallet")
                .child(emailKey)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        Object value = task.getResult().getValue();
                        double balance = 0;
                        if (value instanceof Long) balance = ((Long) value).doubleValue();
                        else if (value instanceof Double) balance = (Double) value;

                        tvWalletBalance.setText("₹ " + String.format("%.2f", balance));
                    } else {
                        tvWalletBalance.setText("₹ 0.00");
                    }
                }).addOnFailureListener(e -> {
                    tvWalletBalance.setText("₹ 0.00");
                    Toast.makeText(this, "Failed to fetch wallet balance", Toast.LENGTH_SHORT).show();
                });
    }

    private void setupButtons() {
        // Profile / Settings
        ImageView profileIcon = findViewById(R.id.profileIcon);
        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, UserSettingsActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("emailKey", emailKey);
            startActivity(intent);
        });

        // Add to Wallet
        findViewById(R.id.addToWalletButton).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AddAmountActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("emailKey", emailKey);
            startActivity(intent);
        });

        // Check Balance
        findViewById(R.id.checkBalanceButton).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, CheckBalanceActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("emailKey", emailKey);
            startActivity(intent);
        });

        // Logout
        findViewById(R.id.logoutButton).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Bank Selection
        findViewById(R.id.bankButton).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, BankSelectionActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("emailKey", emailKey);
            startActivity(intent);
        });

        // Bank Offers / Rewards
        findViewById(R.id.offersRewardsButton).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, OffersActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("emailKey", emailKey);
            startActivity(intent);
        });

        // Mobile Recharge
        findViewById(R.id.mobileRechargeButton).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, Recharge.class);
            intent.putExtra("email", email);
            intent.putExtra("emailKey", emailKey);
            startActivity(intent);
        });

        // Contacts / Pay Anyone
        findViewById(R.id.payAnyoneButton).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, Contacts.class);
            intent.putExtra("email", email);
            intent.putExtra("emailKey", emailKey);
            startActivity(intent);
        });

        // Pay with UPI
        findViewById(R.id.payWithUpiButton).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, PayWithUpi.class);
            intent.putExtra("email", email);
            intent.putExtra("emailKey", emailKey);
            startActivity(intent);
        });
    }
}

package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
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

        // Get email from login activity
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

        // Fetch balance from wallet
        fetchWalletBalance();

        // Existing buttons setup
        setupButtons();
    }

    private void fetchWalletBalance() {
        DatabaseReference walletRef = FirebaseDatabase.getInstance()
                .getReference("wallet")
                .child(emailKey);

        walletRef.get().addOnCompleteListener(task -> {
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
        ImageView profileIcon = findViewById(R.id.profileIcon);
        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, UserSettingsActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("emailKey", emailKey);
            startActivity(intent);
        });

        findViewById(R.id.addToWalletButton).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AddAmountActivity.class);
            intent.putExtra("userId", emailKey);
            startActivity(intent);
        });

        findViewById(R.id.checkBalanceButton).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, CheckBalanceActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("emailKey", emailKey);
            startActivity(intent);
        });

        findViewById(R.id.logoutButton).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Add other buttons similarly...
    }
}

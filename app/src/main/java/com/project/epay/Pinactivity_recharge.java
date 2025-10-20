package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Pinactivity_recharge extends AppCompatActivity {

    private View[] pinDots;
    private CardView cardPin;
    private TextView tvTransactionDetails;
    private String mobile, operator, amount, emailKey;
    private StringBuilder pinBuilder = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_rechrage);

        // Get references
        cardPin = findViewById(R.id.card_pin);
        tvTransactionDetails = findViewById(R.id.tv_transaction_details);

        // Pin dots (4 dots)
        pinDots = new View[]{
                findViewById(R.id.pin_dot_1),
                findViewById(R.id.pin_dot_2),
                findViewById(R.id.pin_dot_3),
                findViewById(R.id.pin_dot_4)
        };

        // Get data from Intent
        Intent intent = getIntent();
        mobile = intent.getStringExtra("mobile");
        operator = intent.getStringExtra("operator");
        amount = intent.getStringExtra("amount");
        emailKey = intent.getStringExtra("emailKey");

        if (emailKey == null || emailKey.trim().isEmpty()) {
            Toast.makeText(this, "User not found. Please log in again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Replace "." with "_" for Firebase key safety
        emailKey = emailKey.replace(".", "_");

        // Show transaction details
        String cleanAmount = (amount != null) ? amount.replace("₹", "") : "N/A";
        String detailsText = String.format("Mobile: %s | Operator: %s | Amount: ₹%s",
                mobile != null ? mobile : "N/A",
                operator != null ? operator : "N/A",
                cleanAmount);
        tvTransactionDetails.setText(detailsText);

        // Setup keypad buttons
        GridLayout keypadGrid = findViewById(R.id.keypad_grid);
        for (int i = 0; i < keypadGrid.getChildCount(); i++) {
            View child = keypadGrid.getChildAt(i);
            if (child instanceof Button) {
                Button btn = (Button) child;
                String text = btn.getText().toString();
                if (text.equalsIgnoreCase("Clear")) {
                    btn.setOnClickListener(v -> clearPin());
                } else if (text.equalsIgnoreCase("Cancel")) {
                    btn.setOnClickListener(v -> finish());
                } else {
                    btn.setOnClickListener(v -> appendPin(text));
                }
            }
        }

        // Enter button click listener
        Button btnEnter = findViewById(R.id.btn_enter);
        btnEnter.setOnClickListener(v -> {
            if (pinBuilder.length() == 4) {
                saveRechargeToFirebase();
            } else {
                Toast.makeText(this, "Enter 4-digit PIN", Toast.LENGTH_SHORT).show();
            }
        });

        // Optional: Home button if exists in layout
        ImageView btnHome = findViewById(R.id.btn_home);
        if (btnHome != null) {
            btnHome.setOnClickListener(v -> {
                Intent homeIntent = new Intent(Pinactivity_recharge.this, DashboardActivity.class);
                homeIntent.putExtra("emailKey", emailKey);
                startActivity(homeIntent);
                finish();
            });
        }
    }

    private void appendPin(String digit) {
        if (pinBuilder.length() < 4) {
            pinBuilder.append(digit);
            updateDots();
        }
    }

    private void clearPin() {
        if (pinBuilder.length() > 0) {
            pinBuilder.deleteCharAt(pinBuilder.length() - 1);
            updateDots();
        }
    }

    private void updateDots() {
        int filled = pinBuilder.length();
        for (int i = 0; i < pinDots.length; i++) {
            pinDots[i].setBackgroundResource(i < filled ? R.drawable.pin_dot_filled : R.drawable.pin_dot_empty);
        }
    }

    // Save recharge under the specific user's email
    private void saveRechargeToFirebase() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReference("Recharges")
                .child(emailKey); // store under logged-in email

        String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        Map<String, Object> rechargeData = new HashMap<>();
        rechargeData.put("mobileNumber", mobile);
        rechargeData.put("operator", operator);
        rechargeData.put("amount", amount);
        rechargeData.put("dateTime", dateTime);
        rechargeData.put("status", "Success");

        dbRef.push().setValue(rechargeData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Recharge successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Pinactivity_recharge.this, SuccessActivity_recharge.class);
                    intent.putExtra("emailKey", emailKey);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save recharge. Try again.", Toast.LENGTH_SHORT).show();
                });
    }
}

package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CheckBalanceActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PIN_LENGTH = 4;
    private int pinIndex = 0;
    private View[] pinDots = new View[PIN_LENGTH];
    private StringBuilder currentPin = new StringBuilder();

    private String emailKey; // sanitized email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkbalance);

        emailKey = getIntent().getStringExtra("emailKey");
        if (emailKey == null || emailKey.isEmpty()) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize PIN dots
        pinDots[0] = findViewById(R.id.pin_dot_1);
        pinDots[1] = findViewById(R.id.pin_dot_2);
        pinDots[2] = findViewById(R.id.pin_dot_3);
        pinDots[3] = findViewById(R.id.pin_dot_4);

        // Keypad buttons
        GridLayout keypad = findViewById(R.id.keypad_grid);
        for (int i = 0; i < keypad.getChildCount(); i++) {
            keypad.getChildAt(i).setOnClickListener(this);
        }

        // Header buttons
        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_home).setOnClickListener(this);
        findViewById(R.id.btn_enter).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_clear).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_back || id == R.id.btn_home) {
            finish();

        } else if (id == R.id.btn_clear) {
            if (pinIndex > 0) {
                pinIndex--;
                pinDots[pinIndex].setBackgroundResource(R.drawable.pin_dot_empty);
                currentPin.deleteCharAt(currentPin.length() - 1);
            }

        } else if (id == R.id.btn_cancel) {
            resetPinDots();

        } else if (id == R.id.btn_enter) {
            if (pinIndex < PIN_LENGTH) {
                Toast.makeText(this, "Enter 4-digit PIN", Toast.LENGTH_SHORT).show();
                return;
            }
            verifyPin();

        } else if (v instanceof Button) {
            if (pinIndex < PIN_LENGTH) {
                pinDots[pinIndex].setBackgroundResource(R.drawable.pin_dot_filled);
                currentPin.append(((Button) v).getText().toString());
                pinIndex++;
            }
        }
    }

    private void resetPinDots() {
        for (View dot : pinDots) {
            dot.setBackgroundResource(R.drawable.pin_dot_empty);
        }
        pinIndex = 0;
        currentPin.setLength(0);
    }

    private void verifyPin() {
        String enteredPin = currentPin.toString();

        // Use sanitized emailKey for BankAccounts
        DatabaseReference pinRef = FirebaseDatabase.getInstance()
                .getReference("BankAccounts")
                .child(emailKey)
                .child("pin");

        pinRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                String storedPin = task.getResult().getValue(String.class);
                if (storedPin != null && storedPin.equals(enteredPin)) {
                    fetchWalletBalance();
                } else {
                    Toast.makeText(this, "Invalid PIN", Toast.LENGTH_SHORT).show();
                    resetPinDots();
                }
            } else {
                Toast.makeText(this, "Error fetching PIN", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchWalletBalance() {
        DatabaseReference walletRef = FirebaseDatabase.getInstance()
                .getReference("wallet")
                .child(emailKey); // sanitized email

        walletRef.get().addOnCompleteListener(task -> {
            double balance = 0;
            if (task.isSuccessful() && task.getResult().exists()) {
                Object value = task.getResult().getValue();
                if (value instanceof Long) balance = ((Long) value).doubleValue();
                else if (value instanceof Double) balance = (Double) value;
            }

            // Go to BalanceActivity
            Intent intent = new Intent(CheckBalanceActivity.this, BalanceActivity.class);
            intent.putExtra("balance", balance);
            startActivity(intent);
        });
    }
}

package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PinActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PIN_LENGTH = 4;
    private int pinIndex = 0;
    private View[] pinDots = new View[PIN_LENGTH];
    private TextView tvTransactionDetails;

    private String name, phone, amount, email, emailKey;
    private boolean transactionSaved = false;

    private StringBuilder currentPin = new StringBuilder(); // store entered PIN

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        // Initialize PIN dots
        pinDots[0] = findViewById(R.id.pin_dot_1);
        pinDots[1] = findViewById(R.id.pin_dot_2);
        pinDots[2] = findViewById(R.id.pin_dot_3);
        pinDots[3] = findViewById(R.id.pin_dot_4);

        tvTransactionDetails = findViewById(R.id.tv_transaction_details);

        // Get data from previous activity
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        phone = intent.getStringExtra("phone");
        amount = intent.getStringExtra("amount");
        email = intent.getStringExtra("email");       // actual email
        emailKey = intent.getStringExtra("emailKey"); // sanitized emailKey

        if (email == null || email.trim().isEmpty() || emailKey == null || emailKey.trim().isEmpty()) {
            Toast.makeText(this, "User not found. Please log in again.", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(PinActivity.this, MainActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
            return;
        }

        // Show transaction details
        if (name != null && !name.isEmpty()) {
            tvTransactionDetails.setText("To " + name + " | Amount: ₹" + amount);
        } else if (phone != null && !phone.isEmpty()) {
            tvTransactionDetails.setText("To " + phone + " | Amount: ₹" + amount);
        } else {
            tvTransactionDetails.setText("Enter PIN to confirm payment");
        }

        // Setup keypad listeners
        GridLayout keypad = findViewById(R.id.keypad_grid);
        for (int i = 0; i < keypad.getChildCount(); i++) {
            View child = keypad.getChildAt(i);
            child.setOnClickListener(this);
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
            // Go to DashboardActivity
            Intent homeIntent = new Intent(PinActivity.this, DashboardActivity.class);
            homeIntent.putExtra("email", email);
            homeIntent.putExtra("emailKey", emailKey);
            startActivity(homeIntent);
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
                Toast.makeText(this, "Please enter 4-digit PIN", Toast.LENGTH_SHORT).show();
                return;
            }

            verifyPin(); // ✅ Check PIN from Firebase before saving

        } else if (v instanceof Button) {
            // Number buttons
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

    // ✅ Verify PIN before transaction
    private void verifyPin() {
        String enteredPin = currentPin.toString();

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("BankAccounts")
                .child(emailKey)
                .child("pin");

        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                String storedPin = task.getResult().getValue(String.class);

                if (storedPin != null && storedPin.equals(enteredPin)) {
                    saveTransactionToFirebase();
                } else {
                    Toast.makeText(PinActivity.this, "Invalid PIN. Please try again.", Toast.LENGTH_SHORT).show();
                    resetPinDots();
                }
            } else {
                Toast.makeText(PinActivity.this, "Unable to verify PIN. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveTransactionToFirebase() {
        if (transactionSaved) return;
        transactionSaved = true;

        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReference("SendMoney")
                .child(emailKey);

        String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        Map<String, Object> txnData = new HashMap<>();
        txnData.put("fromEmail", email);
        txnData.put("toName", name != null ? name : "");
        txnData.put("toPhone", phone != null ? phone : "");
        txnData.put("amount", amount);
        txnData.put("dateTime", dateTime);
        txnData.put("type", "UPI Payment");

        dbRef.push().setValue(txnData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(PinActivity.this, "Transaction successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PinActivity.this, SuccessActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("amount", amount);
                intent.putExtra("email", email);
                intent.putExtra("emailKey", emailKey);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(PinActivity.this, "Failed to save transaction. Try again.", Toast.LENGTH_SHORT).show();
                transactionSaved = false;
            }
        });
    }
}

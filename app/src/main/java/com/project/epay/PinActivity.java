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

import java.util.HashMap;
import java.util.Map;

public class PinActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PIN_LENGTH = 4;
    private int pinIndex = 0;
    private View[] pinDots = new View[PIN_LENGTH];
    private TextView tvTransactionDetails;

    private String name, phone, amount;
    private boolean transactionSaved = false; // ✅ ensure one-time save

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

        // Show transaction details
        if (name != null && !name.isEmpty()) {
            tvTransactionDetails.setText("To " + name + " | Send Amt: ₹" + amount);
        } else if (phone != null && !phone.isEmpty()) {
            tvTransactionDetails.setText("To " + phone + " | Send Amt: ₹" + amount);
        } else {
            tvTransactionDetails.setText("Enter PIN to confirm payment");
        }

        // Setup keypad listeners
        GridLayout keypad = findViewById(R.id.keypad_grid);
        for (int i = 0; i < keypad.getChildCount(); i++) {
            View child = keypad.getChildAt(i);
            child.setOnClickListener(this);
        }

        // Header and action buttons
        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_home).setOnClickListener(this);
        findViewById(R.id.btn_enter).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_clear).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_back) {
            finish();
        } else if (id == R.id.btn_home) {
            // Navigate to home screen
        } else if (id == R.id.btn_clear) {
            if (pinIndex > 0) {
                pinIndex--;
                pinDots[pinIndex].setBackgroundResource(R.drawable.pin_dot_empty);
            }
        } else if (id == R.id.btn_cancel) {
            resetPinDots();
        } else if (id == R.id.btn_enter) {
            if (pinIndex < PIN_LENGTH) {
                Toast.makeText(this, "Please enter 4-digit PIN", Toast.LENGTH_SHORT).show();
                return;
            }

            // Prevent multiple clicks
            if (!transactionSaved) {
                transactionSaved = true;
                v.setEnabled(false); // disable enter button
                saveTransactionToFirebase();
            }

        } else if (v instanceof Button) {
            // Number buttons
            if (pinIndex < PIN_LENGTH) {
                pinDots[pinIndex].setBackgroundResource(R.drawable.pin_dot_filled);
                pinIndex++;
            }
        }
    }

    private void resetPinDots() {
        for (View dot : pinDots) {
            dot.setBackgroundResource(R.drawable.pin_dot_empty);
        }
        pinIndex = 0;
    }

    private void saveTransactionToFirebase() {


        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReference("sendMoneyToContacts")
                .child("user123"); // Replace with actual user id

        String dateTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());

        Map<String, Object> txnData = new HashMap<>();
        txnData.put("name", name != null ? name : "");
        txnData.put("phone", phone != null ? phone : "");
        txnData.put("amount", amount);
        txnData.put("dateTime", dateTime);
        txnData.put("type", "UPI Payment"); // or "Recharge" for recharge module

        dbRef.push().setValue(txnData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(PinActivity.this, "Transaction successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PinActivity.this, SuccessActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("amount", amount);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(PinActivity.this, "Failed to save transaction. Try again.", Toast.LENGTH_SHORT).show();
                transactionSaved = false; // allow retry
            }
        });
    }
}

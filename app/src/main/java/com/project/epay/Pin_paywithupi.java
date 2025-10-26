package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Pin_paywithupi extends AppCompatActivity implements View.OnClickListener {

    private static final int PIN_LENGTH = 4;
    private View[] pinDots = new View[PIN_LENGTH];
    private StringBuilder pinBuilder = new StringBuilder();
    private TextView tvTransactionDetails;

    private String recipientUpi, recipientName;
    private int amount;
    private String email, emailKey;

    private DatabaseReference transactionsRef;
    private DatabaseReference bankAccountsRef;
    private DatabaseReference walletRef;

    private boolean transactionInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin_paywithupi);

        recipientUpi = getIntent().getStringExtra("recipientUpi");
        recipientName = getIntent().getStringExtra("recipientName");
        amount = getIntent().getIntExtra("amount", 0);
        email = getIntent().getStringExtra("email");
        emailKey = getIntent().getStringExtra("emailKey");

        if (recipientName == null || recipientName.trim().isEmpty() || recipientUpi == null || amount <= 0) {
            Toast.makeText(this, "Invalid transaction data.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String sanitizedEmailKey = email.replace(".", "_");

        pinDots[0] = findViewById(R.id.pin_dot_1);
        pinDots[1] = findViewById(R.id.pin_dot_2);
        pinDots[2] = findViewById(R.id.pin_dot_3);
        pinDots[3] = findViewById(R.id.pin_dot_4);

        tvTransactionDetails = findViewById(R.id.tv_transaction_details);
        tvTransactionDetails.setText("To: " + recipientName + " | Amount: ₹" + amount);

        transactionsRef = FirebaseDatabase.getInstance().getReference("transactions").child(sanitizedEmailKey);
        bankAccountsRef = FirebaseDatabase.getInstance().getReference("BankAccounts").child(sanitizedEmailKey);
        walletRef = FirebaseDatabase.getInstance().getReference("wallet").child(sanitizedEmailKey);

        GridLayout keypad = findViewById(R.id.keypad_grid);
        for (int i = 0; i < keypad.getChildCount(); i++) {
            keypad.getChildAt(i).setOnClickListener(this);
        }

        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_home).setOnClickListener(this);
        findViewById(R.id.btn_enter).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_clear).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_home || id == R.id.btn_back) {
            goToDashboard();
        } else if (id == R.id.btn_clear) {
            clearLastDigit();
        } else if (id == R.id.btn_cancel) {
            resetPinDots();
        } else if (id == R.id.btn_enter) {
            if (pinBuilder.length() < PIN_LENGTH) {
                Toast.makeText(this, "Please enter 4-digit PIN", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!transactionInProgress) {
                transactionInProgress = true;
                verifyPinAndDeductMoney();
            }
        } else if (v instanceof Button) {
            appendDigit(((Button) v).getText().toString());
        }
    }

    private void appendDigit(String digit) {
        if (pinBuilder.length() < PIN_LENGTH) {
            pinBuilder.append(digit);
            pinDots[pinBuilder.length() - 1].setBackgroundResource(R.drawable.pin_dot_filled);
        }
    }

    private void clearLastDigit() {
        if (pinBuilder.length() > 0) {
            pinBuilder.deleteCharAt(pinBuilder.length() - 1);
            pinDots[pinBuilder.length()].setBackgroundResource(R.drawable.pin_dot_empty);
        }
    }

    private void resetPinDots() {
        pinBuilder.setLength(0);
        for (View dot : pinDots) {
            dot.setBackgroundResource(R.drawable.pin_dot_empty);
        }
    }

    private void verifyPinAndDeductMoney() {
        bankAccountsRef.child("pin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String storedPin = snapshot.getValue(String.class);
                if (storedPin != null && storedPin.trim().equals(pinBuilder.toString().trim())) {
                    deductMoneyFromWallet();
                } else {
                    Toast.makeText(Pin_paywithupi.this, "Incorrect PIN. Try again.", Toast.LENGTH_SHORT).show();
                    resetPinDots();
                    transactionInProgress = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(Pin_paywithupi.this, "Error verifying PIN: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                transactionInProgress = false;
            }
        });
    }

    private void deductMoneyFromWallet() {
        walletRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Integer currentBalance = task.getResult().getValue(Integer.class);
                if (currentBalance == null) currentBalance = 0;

                if (currentBalance >= amount) {
                    walletRef.setValue(currentBalance - amount).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            saveTransaction();
                        } else {
                            Toast.makeText(Pin_paywithupi.this, "Failed to update wallet balance.", Toast.LENGTH_SHORT).show();
                            transactionInProgress = false;
                        }
                    });
                } else {
                    Toast.makeText(Pin_paywithupi.this, "Insufficient balance!", Toast.LENGTH_SHORT).show();
                    transactionInProgress = false;
                }
            } else {
                Toast.makeText(Pin_paywithupi.this, "Wallet not found for user.", Toast.LENGTH_SHORT).show();
                transactionInProgress = false;
            }
        });
    }

    private void saveTransaction() {
        String txnId = transactionsRef.push().getKey();
        if (txnId == null) return;

        String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        Map<String, Object> txn = new HashMap<>();
        txn.put("recipientUpi", recipientUpi);
        txn.put("recipientName", recipientName); // fixed
        txn.put("amount", amount);
        txn.put("dateTime", dateTime);
        txn.put("status", "Success");
        txn.put("type", "Sent");

        transactionsRef.child(txnId).setValue(txn)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Payment successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Pin_paywithupi.this, SuccessActivity.class);
                    intent.putExtra("recipientName", recipientName);
                    intent.putExtra("recipientUpi", recipientUpi);
                    intent.putExtra("amount", amount);
                    intent.putExtra("email", email);
                    intent.putExtra("emailKey", emailKey);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Pin_paywithupi.this, "Transaction failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    transactionInProgress = false;
                });
    }

    private void goToDashboard() {
        Intent intent = new Intent(Pin_paywithupi.this, DashboardActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("emailKey", emailKey);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        goToDashboard();
    }
}

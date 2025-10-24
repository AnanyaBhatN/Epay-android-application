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
    private int pinIndex = 0;
    private View[] pinDots = new View[PIN_LENGTH];
    private TextView tvTransactionDetails;

    private String recipientUpi, amount;
    private String email, emailKey;
    private StringBuilder pinBuilder = new StringBuilder();

    private DatabaseReference contactsRef;
    private DatabaseReference transactionsRef;
    private DatabaseReference bankAccountsRef;

    private boolean transactionInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin_paywithupi);

        // Get data from Intent
        recipientUpi = getIntent().getStringExtra("recipientUpi");
        amount = getIntent().getStringExtra("amount");
        email = getIntent().getStringExtra("email");
        emailKey = getIntent().getStringExtra("emailKey");

        if (email == null || email.trim().isEmpty()) {
            Toast.makeText(this, "User not found. Please log in again.", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(Pin_paywithupi.this, MainActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
            return;
        }

        String sanitizedEmailKey = email.replace(".", "_");

        // Initialize PIN dots
        pinDots[0] = findViewById(R.id.pin_dot_1);
        pinDots[1] = findViewById(R.id.pin_dot_2);
        pinDots[2] = findViewById(R.id.pin_dot_3);
        pinDots[3] = findViewById(R.id.pin_dot_4);

        // Show transaction details
        tvTransactionDetails = findViewById(R.id.tv_transaction_details);
        tvTransactionDetails.setText("To: " + recipientUpi + " | Amount: ₹" + amount);

        // Firebase references
        contactsRef = FirebaseDatabase.getInstance().getReference("contacts").child("user123");
        transactionsRef = FirebaseDatabase.getInstance().getReference("transactions").child(sanitizedEmailKey);
        bankAccountsRef = FirebaseDatabase.getInstance().getReference("BankAccounts").child(sanitizedEmailKey);

        // Setup keypad
        GridLayout keypad = findViewById(R.id.keypad_grid);
        for (int i = 0; i < keypad.getChildCount(); i++) {
            keypad.getChildAt(i).setOnClickListener(this);
        }

        // Buttons
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
                verifyPinAndProceed();
            }

        } else if (v instanceof Button) {
            String digit = ((Button) v).getText().toString();
            appendDigit(digit);
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

    private void verifyPinAndProceed() {
        // ✅ Fetch PIN from BankAccounts/<emailKey>/pin
        bankAccountsRef.child("pin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String storedPin = snapshot.getValue(String.class);

                if (storedPin == null) {
                    Toast.makeText(Pin_paywithupi.this, "No PIN found in Bank Account. Please set your PIN first.", Toast.LENGTH_SHORT).show();
                    transactionInProgress = false;
                } else if (storedPin.equals(pinBuilder.toString())) {
                    verifyUpiAndSaveTransaction();
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

    private void verifyUpiAndSaveTransaction() {
        contactsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean upiFound = false;

                for (DataSnapshot contactSnap : snapshot.getChildren()) {
                    String contactUpi = contactSnap.child("upiId").getValue(String.class);
                    if (recipientUpi.equals(contactUpi)) {
                        upiFound = true;
                        break;
                    }
                }

                if (upiFound) {
                    saveTransaction();
                } else {
                    Toast.makeText(Pin_paywithupi.this, "UPI ID not found in contacts.", Toast.LENGTH_SHORT).show();
                    transactionInProgress = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(Pin_paywithupi.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                transactionInProgress = false;
            }
        });
    }

    private void saveTransaction() {
        String txnId = transactionsRef.push().getKey();
        if (txnId == null) return;

        String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // --- Sender transaction (money sent) ---
        Map<String, Object> senderTxn = new HashMap<>();
        senderTxn.put("recipientUpi", recipientUpi);
        senderTxn.put("amount", amount);
        senderTxn.put("dateTime", dateTime);
        senderTxn.put("status", "Success");
        senderTxn.put("type", "Sent");

        // Save in sender's transaction node
        transactionsRef.child(txnId).setValue(senderTxn)
                .addOnSuccessListener(aVoid -> {

                    // --- Receiver transaction (money received) ---
                    String receiverKey = recipientUpi.replace("@", "_").replace(".", "_"); // safe key
                    DatabaseReference receiverTransRef = FirebaseDatabase.getInstance()
                            .getReference("transactions")
                            .child(receiverKey);

                    Map<String, Object> receiverTxn = new HashMap<>();
                    receiverTxn.put("senderUpi", email.replace(".", "_")); // optional: sender’s UPI/email
                    receiverTxn.put("amount", amount);
                    receiverTxn.put("dateTime", dateTime);
                    receiverTxn.put("status", "Success");
                    receiverTxn.put("type", "Received");

                    receiverTransRef.push().setValue(receiverTxn)
                            .addOnSuccessListener(v -> {
                                Toast.makeText(this, "Payment successful!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Pin_paywithupi.this, SuccessActivity.class);
                                intent.putExtra("recipientUpi", recipientUpi);
                                intent.putExtra("amount", amount);
                                intent.putExtra("email", email);
                                intent.putExtra("emailKey", email.replace(".", "_"));
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Receiver transaction save failed.", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Transaction failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    transactionInProgress = false;
                });
    }


    private void goToDashboard() {
        Intent intent = new Intent(Pin_paywithupi.this, DashboardActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("emailKey", email.replace(".", "_"));
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        goToDashboard();
    }
}

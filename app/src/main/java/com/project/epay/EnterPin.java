package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EnterPin extends AppCompatActivity implements View.OnClickListener {

    private static final int PIN_LENGTH = 4;
    private View[] pinDots = new View[PIN_LENGTH];
    private StringBuilder pinBuilder = new StringBuilder();

    private double amountToAdd;
    private String emailKey; // logged-in user's sanitized email
    private DatabaseReference walletRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pin);

        // Get amount and emailKey from AddAmountActivity
        amountToAdd = getIntent().getDoubleExtra("amountToAdd", 0);
        emailKey = getIntent().getStringExtra("userId");

        if (emailKey == null || amountToAdd <= 0) {
            Toast.makeText(this, "Invalid data. Please try again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        walletRef = FirebaseDatabase.getInstance().getReference("wallet");

        // Initialize PIN dots (keep black)
        pinDots[0] = findViewById(R.id.pin_dot_1);
        pinDots[1] = findViewById(R.id.pin_dot_2);
        pinDots[2] = findViewById(R.id.pin_dot_3);
        pinDots[3] = findViewById(R.id.pin_dot_4);

        // Buttons
        findViewById(R.id.btn_enter).setOnClickListener(this);
        findViewById(R.id.btn_clear).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);

        // Number buttons 0-9
        for (int i = 0; i <= 9; i++) {
            int resId = getResources().getIdentifier("btn_" + i, "id", getPackageName());
            findViewById(resId).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_clear) {
            if (pinBuilder.length() > 0) {
                pinBuilder.deleteCharAt(pinBuilder.length() - 1);
                pinDots[pinBuilder.length()].setBackgroundResource(R.drawable.pin_dot_empty);
            }

        } else if (id == R.id.btn_cancel) {
            resetPinDots();

        } else if (id == R.id.btn_enter) {
            if (pinBuilder.length() < PIN_LENGTH) {
                Toast.makeText(this, "Enter 4-digit PIN", Toast.LENGTH_SHORT).show();
                return;
            }
            verifyPinAndSaveAmount();

        } else if (v instanceof Button) {
            String digit = ((Button) v).getText().toString();
            if (pinBuilder.length() < PIN_LENGTH) {
                pinBuilder.append(digit);
                pinDots[pinBuilder.length() - 1].setBackgroundResource(R.drawable.pin_dot_filled); // black dot
            }
        }
    }

    private void resetPinDots() {
        pinBuilder.setLength(0);
        for (View dot : pinDots) {
            dot.setBackgroundResource(R.drawable.pin_dot_empty);
        }
    }

    private void verifyPinAndSaveAmount() {
        DatabaseReference pinRef = FirebaseDatabase.getInstance()
                .getReference("BankAccounts")
                .child(emailKey)
                .child("pin");

        pinRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String storedPin = snapshot.getValue(String.class);
                if (storedPin != null && storedPin.equals(pinBuilder.toString())) {
                    saveAmountToWallet();
                } else {
                    Toast.makeText(EnterPin.this, "Incorrect PIN", Toast.LENGTH_SHORT).show();
                    resetPinDots();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(EnterPin.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveAmountToWallet() {
        DatabaseReference userWalletRef = walletRef.child(emailKey);

        userWalletRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                double currentAmount = 0;
                if (snapshot.exists()) {
                    Double val = snapshot.getValue(Double.class);
                    if (val != null) currentAmount = val;
                }

                double updatedAmount = currentAmount + amountToAdd;

                userWalletRef.setValue(updatedAmount)
                        .addOnSuccessListener(aVoid -> {
                            // Go to AddSuccessActivity after wallet updated
                            Intent intent = new Intent(EnterPin.this, AddSuccessActivity.class);
                            intent.putExtra("emailKey", emailKey);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(EnterPin.this, "Failed to add amount", Toast.LENGTH_SHORT).show()
                        );
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(EnterPin.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

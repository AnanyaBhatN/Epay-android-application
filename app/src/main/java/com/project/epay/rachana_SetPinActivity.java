package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
// Imports for SharedPreferences
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

// --- ADD THESE IMPORTS ---
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
// --- END OF NEW IMPORTS ---

// Firebase Database imports
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
// --- ADD THESE IMPORTS ---
import java.util.HashMap;
import java.util.List;
import java.util.Map;
// --- END OF NEW IMPORTS ---

public class rachana_SetPinActivity extends AppCompatActivity {
    // These keys must match BankDetailsActivity
    public static final String EXTRA_BANK_NAME = "extra_bank_name";
    public static final String EXTRA_ACCOUNT = "extra_account";
    public static final String EXTRA_CARD = "extra_card";
    public static final String EXTRA_EXPIRY = "extra_expiry";

    public static final String EXTRA_PIN = "extra_pin";

    private List<EditText> pinFields;
    private Button btnSet;

    // Variables to hold all bank details
    private String bankName, account, card, expiry;

    // --- MODIFIED: This must be the ROOT reference ---
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rachana_activity_set_pin);

        // --- MODIFIED: Initialize to the ROOT of your database ---
        rootRef = FirebaseDatabase.getInstance().getReference();

        // --- View Initialization ---
        ImageView imgBack = findViewById(R.id.imgBack);
        ImageView imgClose = findViewById(R.id.imgClose);
        imgBack.setOnClickListener(v -> finish());
        imgClose.setOnClickListener(v -> finishAffinity());

        pinFields = new ArrayList<>();
        pinFields.add(findViewById(R.id.pin1));
        pinFields.add(findViewById(R.id.pin2));
        pinFields.add(findViewById(R.id.pin3));
        pinFields.add(findViewById(R.id.pin4));
        btnSet = findViewById(R.id.btnSetPin);

        setupPinListeners(); // Your existing method
        // --- End of View Initialization ---

        // --- Get ALL data from the Intent ---
        bankName = getIntent().getStringExtra(EXTRA_BANK_NAME);
        account = getIntent().getStringExtra(EXTRA_ACCOUNT);
        card = getIntent().getStringExtra(EXTRA_CARD);
        expiry = getIntent().getStringExtra(EXTRA_EXPIRY);

        btnSet.setOnClickListener(v -> {
            StringBuilder pinBuilder = new StringBuilder();
            for (EditText field : pinFields) {
                pinBuilder.append(field.getText().toString());
            }
            String pin = pinBuilder.toString();

            if (pin.length() < 4) {
                Toast.makeText(this, "Enter all 4 digits", Toast.LENGTH_SHORT).show();
                return;
            }

            // --- MODIFIED: Disable button and run the check ---
            btnSet.setEnabled(false);
            Toast.makeText(this, "Checking account...", Toast.LENGTH_SHORT).show();
            checkAndSaveBankDetails(pin);
        });
    }

    /**
     * This method REPLACES your old saveBankDetails method.
     * It checks for uniqueness *before* saving.
     */
    private void checkAndSaveBankDetails(String pin) {

        // --- 1. Get User's Email Key ---
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String userEmail = prefs.getString("user_email_key", null);

        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "User email not found. Cannot save.", Toast.LENGTH_LONG).show();
            btnSet.setEnabled(true); // Re-enable button
            return;
        }
        String emailKey = userEmail.replace(".", "_");

        // --- 2. Create the BankAccount Object ---
        BankAccount bankAccount = new BankAccount(bankName, account, card, expiry, pin);

        // --- 3. Create the Unique Key to check (e.g., "SBI_123456789") ---
        String compositeKey = bankName.toUpperCase().replaceAll("[^a-zA-Z0-9]", "") + "_" + account;

        // --- 4. Check the "RegisteredBankAccounts" index first ---
        DatabaseReference indexRef = rootRef.child("RegisteredBankAccounts").child(compositeKey);

        indexRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // --- ACCOUNT ALREADY EXISTS ---
                    Toast.makeText(rachana_SetPinActivity.this, "This bank account is already registered.", Toast.LENGTH_LONG).show();
                    btnSet.setEnabled(true); // Re-enable button

                } else {
                    // --- ACCOUNT IS UNIQUE ---
                    // Save it in TWO places at once using a multi-path update

                    // Get a new unique push key for the user's bank list
                    String newBankPushKey = rootRef.child("BankAccounts").child(emailKey).push().getKey();

                    // Create the map for the atomic update
                    Map<String, Object> updates = new HashMap<>();

                    // Path 1: The user's bank list
                    updates.put("/BankAccounts/" + emailKey + "/" + newBankPushKey, bankAccount);

                    // Path 2: The new index entry
                    updates.put("/RegisteredBankAccounts/" + compositeKey, emailKey);

                    // Perform the atomic update
                    rootRef.updateChildren(updates).addOnSuccessListener(aVoid -> {
                        // Data saved successfully in BOTH places!
                        Toast.makeText(rachana_SetPinActivity.this, "Bank Account Added!", Toast.LENGTH_SHORT).show();

                        // Now, go to the Success screen
                        Intent i = new Intent(rachana_SetPinActivity.this, rachana_SuccessActivity.class);
                        i.putExtra(EXTRA_BANK_NAME, bankName);
                        i.putExtra(EXTRA_ACCOUNT, account);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();

                    }).addOnFailureListener(e -> {
                        // Failed to save
                        Toast.makeText(rachana_SetPinActivity.this, "Failed to add account: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        btnSet.setEnabled(true); // Re-enable button
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read the database
                Toast.makeText(rachana_SetPinActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                btnSet.setEnabled(true); // Re-enable button
            }
        });
    }


    /**
     * Sets up listeners for each PIN field to handle auto-focus.
     * (This is your existing method, no changes needed)
     */
    private void setupPinListeners() {
        for (int i = 0; i < pinFields.size(); i++) {
            final int currentIndex = i;
            EditText currentField = pinFields.get(currentIndex);

            // This is the TextWatcher that moves the cursor FORWARD
            currentField.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    // If a single character is entered and it's not the last box
                    if (s.length() == 1 && currentIndex < pinFields.size() - 1) {
                        // Move focus to the next box
                        pinFields.get(currentIndex + 1).requestFocus();
                    }
                }
            });

            // This listener handles the backspace to move the cursor BACKWARD
            currentField.setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // If backspace is pressed on an empty field, move focus to the previous one
                    if (currentIndex > 0 && currentField.getText().toString().isEmpty()) {
                        pinFields.get(currentIndex - 1).requestFocus();
                    }
                }
                return false;
            });
        }
    }
}
package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
// --- ADD THESE IMPORTS ---
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
// ---
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
// --- REMOVE FIREBASE AUTH IMPORTS ---
// import com.google.firebase.auth.FirebaseAuth;
// import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class rachana_SetPinActivity extends AppCompatActivity {
    // --- These keys must match BankDetailsActivity ---
    public static final String EXTRA_BANK_NAME = "extra_bank_name";
    public static final String EXTRA_ACCOUNT = "extra_account";
    public static final String EXTRA_CARD = "extra_card";
    public static final String EXTRA_EXPIRY = "extra_expiry";

    public static final String EXTRA_PIN = "extra_pin";

    private List<EditText> pinFields;
    private Button btnSet;

    // --- Variables to hold all bank details ---
    private String bankName, account, card, expiry;

    // --- Firebase Database Reference ---
    private DatabaseReference databaseReference;
    // --- REMOVE FIREBASE AUTH VARIABLE ---
    // private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rachana_activity_set_pin);

        // --- Initialize Firebase ---
        // --- REMOVE FIREBASE AUTH INITIALIZATION ---
        // mAuth = FirebaseAuth.getInstance();

        // Get reference to the "BankAccounts" node (table)
        databaseReference = FirebaseDatabase.getInstance().getReference("BankAccounts");

        // --- View Initialization (same as before) ---
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

            // --- All data is ready. Now save to Firebase ---
            saveBankDetails(pin);
        });
    }

    /**
     * New method to save the collected bank details to Firebase.
     */
    private void saveBankDetails(String pin) {

        // --- GET EMAIL FROM SHARED PREFERENCES ---
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // This key ("user_email_key") MUST match the key from MainActivity
        String userEmail = prefs.getString("user_email_key", null);

        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "User email not found. Cannot save details.", Toast.LENGTH_LONG).show();
            // Optional: Redirect to login screen
            // startActivity(new Intent(this, MainActivity.class));
            return;
        }

        // --- IMPORTANT: Create a valid Firebase Key from the email ---
        // We replace '.' with '_' to match your Users table.
        String emailKey = userEmail.replace(".", "_");

        // 1. Create the BankAccount object with all the data
        // (Make sure you have created the BankAccount.java class)
        BankAccount bankAccount = new BankAccount(bankName, account, card, expiry, pin);

        // 2. Save the object to Firebase under the user's sanitized email key
        databaseReference.child(emailKey).setValue(bankAccount)
                .addOnSuccessListener(aVoid -> {
                    // Data saved successfully!
                    Toast.makeText(rachana_SetPinActivity.this, "Bank Account Added!", Toast.LENGTH_SHORT).show();

                    // Now, go to the Success screen
                    Intent i = new Intent(rachana_SetPinActivity.this, rachana_SuccessActivity.class);

                    // You can still pass data if the success screen needs it
                    i.putExtra(EXTRA_BANK_NAME, bankName);
                    i.putExtra(EXTRA_ACCOUNT, account);

                    // Clear the back stack and start the success activity
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish(); // Finish this activity
                })
                .addOnFailureListener(e -> {
                    // Failed to save data
                    Toast.makeText(rachana_SetPinActivity.this, "Failed to add account: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class BankDetailsActivity extends AppCompatActivity {
    public static final String EXTRA_BANK_NAME = "extra_bank_name";
    public static final String EXTRA_ACCOUNT = "extra_account";
    public static final String EXTRA_CARD = "extra_card";
    public static final String EXTRA_EXPIRY = "extra_expiry";

    TextView tvSelectedBank;
    EditText etAccount, etCard, etExpiry, etCvv;
    Button btnNext;
    String bankName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_details);

        // --- View Initialization ---
        ImageView imgBack = findViewById(R.id.imgBack);
        ImageView imgClose = findViewById(R.id.imgClose); // This is your Home icon
        imgBack.setOnClickListener(v -> finish()); // Goes back to BankSelectionActivity

        // ---*** THIS IS THE CHANGED BLOCK ***---
        imgClose.setOnClickListener(v -> {
            // Go to Dashboard
            Intent intent = new Intent(BankDetailsActivity.this, DashboardActivity.class);
            // Clear the activities on top
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish(); // Close this activity
        });
        // ---*** END OF CHANGE ***---

        tvSelectedBank = findViewById(R.id.tvSelectedBank);
        etAccount = findViewById(R.id.etAccountNumber);
        etCard = findViewById(R.id.etCardNumber);
        etExpiry = findViewById(R.id.etExpiry);
        etCvv = findViewById(R.id.etCvv);
        btnNext = findViewById(R.id.btnNextDetails);

        bankName = getIntent().getStringExtra(EXTRA_BANK_NAME);
        if (bankName == null) bankName = "N/A";
        tvSelectedBank.setText("Selected Bank: " + bankName);
        // --- End of View Initialization ---

        btnNext.setOnClickListener(v -> {
            // Only proceed if all input fields are valid
            if (validateInput()) {
                String account = etAccount.getText().toString().trim();
                String card = etCard.getText().toString().trim();
                String expiry = etExpiry.getText().toString().trim();

                Intent i = new Intent(BankDetailsActivity.this, rachana_SetPinActivity.class);
                i.putExtra(EXTRA_BANK_NAME, bankName);
                i.putExtra(EXTRA_ACCOUNT, account);
                i.putExtra(EXTRA_CARD, card);
                i.putExtra(EXTRA_EXPIRY, expiry);
                startActivity(i);
            }
        });
    }

    // ... (validateInput() and isValidExpiryDate() methods are unchanged) ...

    private boolean validateInput() {
        String account = etAccount.getText().toString().trim();
        String card = etCard.getText().toString().trim();
        String expiry = etExpiry.getText().toString().trim();
        String cvv = etCvv.getText().toString().trim();

        if (account.length() < 9 || account.length() > 18) {
            etAccount.setError("Account number must be between 9 and 18 digits");
            etAccount.requestFocus();
            return false;
        }
        if (card.length() != 16) {
            etCard.setError("Please enter a valid 16-digit card number");
            etCard.requestFocus();
            return false;
        }
        if (!isValidExpiryDate(expiry)) {
            etExpiry.setError("Invalid date. Use MM/YY format and a future date.");
            etExpiry.requestFocus();
            return false;
        }
        if (cvv.length() < 3 || cvv.length() > 4) {
            etCvv.setError("CVV must be 3 or 4 digits");
            etCvv.requestFocus();
            return false;
        }
        etAccount.setError(null);
        etCard.setError(null);
        etExpiry.setError(null);
        etCvv.setError(null);
        return true;
    }

    private boolean isValidExpiryDate(String expiryDate) {
        if (!expiryDate.matches("^(0[1-9]|1[0-2])\\/([0-9]{2})$")) {
            return false;
        }
        String[] parts = expiryDate.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]);
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR) % 100;
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        if (year < currentYear || (year == currentYear && month < currentMonth)) {
            return false;
        }
        return true;
    }
}
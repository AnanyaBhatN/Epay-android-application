package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

// --- ADD THIS IMPORT ---
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PayWithUpi extends AppCompatActivity {

    private MaterialCardView cardUpiEntry, cardAmountEntry;
    private EditText etUpiId, etAmount;
    private TextView tvVerifiedUpiId;
    private Button btnVerify, btnPay;

    private DatabaseReference contactsRef;
    private final String fixedUserId = "user123"; // fixed user
    private String verifiedUpiId;

    // Email passed from DashboardActivity
    private String email;
    private String emailKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paywithupi);

        // Get email/emailKey from DashboardActivity
        email = getIntent().getStringExtra("email");
        emailKey = getIntent().getStringExtra("emailKey");

        // Initialize views
        cardUpiEntry = findViewById(R.id.card_upi_entry);
        cardAmountEntry = findViewById(R.id.card_amount_entry);
        etUpiId = findViewById(R.id.et_upi_id);
        etAmount = findViewById(R.id.et_amount);
        tvVerifiedUpiId = findViewById(R.id.tv_verified_upi_id);
        btnVerify = findViewById(R.id.btn_verify);
        btnPay = findViewById(R.id.btn_pay);

        // Hide amount card initially
        cardAmountEntry.setVisibility(View.GONE);

        // Firebase reference
        contactsRef = FirebaseDatabase.getInstance().getReference("contacts").child(fixedUserId);

        // Verify UPI ID
        btnVerify.setOnClickListener(v -> verifyUpi());

        // Proceed to PIN entry
        btnPay.setOnClickListener(v -> proceedToPin());

        // ---*** THIS IS THE UPDATED SECTION ***---

        // Back icon click listener
        View backIcon = findViewById(R.id.back_icon); // <-- Correct ID
        if (backIcon != null) {
            backIcon.setOnClickListener(v -> {
                // finish() closes this activity and goes to the previous one
                finish();
            });
        }

        // Home icon click listener
        View homeIcon = findViewById(R.id.home_icon); // <-- Correct ID
        if (homeIcon != null) {
            homeIcon.setOnClickListener(v -> goToDashboard());
        }

        // This replaces the deprecated onBackPressed() method
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // This will close the current activity and go back
                finish();
            }
        });
        // ---*** END OF UPDATED SECTION ***---
    }

    private void verifyUpi() {
        String upiId = etUpiId.getText().toString().trim();

        if (!isValidUpiId(upiId)) {
            etUpiId.setError("Enter valid UPI ID (e.g., user@upi)");
            return;
        }

        btnVerify.setText("Verifying...");
        btnVerify.setEnabled(false);

        contactsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean found = false;

                for (DataSnapshot contactSnap : snapshot.getChildren()) {
                    String contactUpi = contactSnap.child("upiId").getValue(String.class);
                    if (upiId.equals(contactUpi)) {
                        found = true;
                        break;
                    }
                }

                if (found) {
                    verifiedUpiId = upiId;
                    tvVerifiedUpiId.setText(verifiedUpiId);
                    cardAmountEntry.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(PayWithUpi.this, "UPI ID not found in contacts", Toast.LENGTH_SHORT).show();
                }

                btnVerify.setText("Verify");
                btnVerify.setEnabled(true);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(PayWithUpi.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                btnVerify.setText("Verify");
                btnVerify.setEnabled(true);
            }
        });
    }

    private void proceedToPin() {
        String amount = etAmount.getText().toString().trim();
        if (TextUtils.isEmpty(amount)) {
            etAmount.setError("Enter amount");
            return;
        }

        Intent intent = new Intent(PayWithUpi.this, Pin_paywithupi.class);
        intent.putExtra("recipientUpi", verifiedUpiId);
        intent.putExtra("amount", amount);
        intent.putExtra("email", email);
        intent.putExtra("emailKey", emailKey);
        startActivity(intent);
    }

    private boolean isValidUpiId(String upiId) {
        if (TextUtils.isEmpty(upiId)) return false;
        String regex = "[a-zA-Z0-9.\\-_]{2,256}@[a-zA-Z]{2,64}";
        return upiId.matches(regex);
    }

    // Navigate to DashboardActivity
    private void goToDashboard() {
        Intent dashboardIntent = new Intent(PayWithUpi.this, DashboardActivity.class);
        dashboardIntent.putExtra("email", email);
        dashboardIntent.putExtra("emailKey", emailKey);
        dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(dashboardIntent);
        finish();
    }

    // --- The old onBackPressed() method is no longer needed ---
}
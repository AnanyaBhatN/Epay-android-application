package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    private final String fixedUserId = "user123";
    private String verifiedUpiId;
    private String receiverName;

    private String email;
    private String emailKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paywithupi);

        email = getIntent().getStringExtra("email");
        emailKey = getIntent().getStringExtra("emailKey");

        cardUpiEntry = findViewById(R.id.card_upi_entry);
        cardAmountEntry = findViewById(R.id.card_amount_entry);
        etUpiId = findViewById(R.id.et_upi_id);
        etAmount = findViewById(R.id.et_amount);
        tvVerifiedUpiId = findViewById(R.id.tv_verified_upi_id);
        btnVerify = findViewById(R.id.btn_verify);
        btnPay = findViewById(R.id.btn_pay);

        cardAmountEntry.setVisibility(View.GONE);
        contactsRef = FirebaseDatabase.getInstance().getReference("contacts").child(fixedUserId);

        btnVerify.setOnClickListener(v -> verifyUpi());
        btnPay.setOnClickListener(v -> proceedToPin());

        View homeIcon = findViewById(R.id.btn_home);
        if (homeIcon != null) homeIcon.setOnClickListener(v -> goToDashboard());
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
                    receiverName = contactSnap.child("contactName").getValue(String.class); // fixed key

                    if (upiId.equals(contactUpi)) {
                        if (receiverName == null || receiverName.trim().isEmpty()) {
                            Toast.makeText(PayWithUpi.this,
                                    "Contact name missing in Firebase", Toast.LENGTH_SHORT).show();
                            btnVerify.setText("Verify");
                            btnVerify.setEnabled(true);
                            return;
                        }

                        verifiedUpiId = upiId;
                        tvVerifiedUpiId.setText("Paying to: " + receiverName);
                        cardAmountEntry.setVisibility(View.VISIBLE);
                        found = true;
                        break;
                    }
                }

                if (!found) {
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
        String amountStr = etAmount.getText().toString().trim();
        if (TextUtils.isEmpty(amountStr)) {
            etAmount.setError("Enter amount");
            return;
        }

        if (verifiedUpiId == null || receiverName == null) {
            Toast.makeText(this, "Verify UPI ID first", Toast.LENGTH_SHORT).show();
            return;
        }

        int amountInt;
        try {
            amountInt = Integer.parseInt(amountStr);
            if (amountInt <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            etAmount.setError("Enter valid amount");
            return;
        }

        Intent intent = new Intent(PayWithUpi.this, Pin_paywithupi.class);
        intent.putExtra("recipientUpi", verifiedUpiId);
        intent.putExtra("recipientName", receiverName);
        intent.putExtra("amount", amountInt);
        intent.putExtra("email", email);
        intent.putExtra("emailKey", emailKey);
        startActivity(intent);
    }

    private boolean isValidUpiId(String upiId) {
        if (TextUtils.isEmpty(upiId)) return false;
        String regex = "[a-zA-Z0-9.\\-_]{2,256}@[a-zA-Z]{2,64}";
        return upiId.matches(regex);
    }

    private void goToDashboard() {
        Intent dashboardIntent = new Intent(PayWithUpi.this, DashboardActivity.class);
        dashboardIntent.putExtra("email", email);
        dashboardIntent.putExtra("emailKey", emailKey);
        dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(dashboardIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        goToDashboard();
    }
}

package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddAmountActivity extends AppCompatActivity {

    private EditText amountEdit;
    private Button btnPay;
    private ImageView btnBack, btnHome;

    private String userId; // logged-in user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_amount);

        amountEdit = findViewById(R.id.amountEdit);
        btnPay = findViewById(R.id.btnPay);
        btnBack = findViewById(R.id.btnBack);
        btnHome = findViewById(R.id.btn_home);

        // Receive logged-in user ID from Dashboard
        userId = getIntent().getStringExtra("userId");
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "User not found. Please login again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddAmountActivity.this, MainActivity.class));
            finish();
            return;
        }

        btnBack.setOnClickListener(v -> finish());

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(AddAmountActivity.this, DashboardActivity.class);
            intent.putExtra("emailKey", userId);
            startActivity(intent);
        });

        btnPay.setOnClickListener(v -> {
            String amountStr = amountEdit.getText().toString().trim();
            if (TextUtils.isEmpty(amountStr)) {
                Toast.makeText(this, "Enter amount", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            // Pass actual userId and amount to EnterPin
            Intent intent = new Intent(AddAmountActivity.this, EnterPin.class);
            intent.putExtra("amountToAdd", amount);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }
}

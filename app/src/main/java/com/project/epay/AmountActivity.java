package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AmountActivity extends AppCompatActivity {

    private TextView toLabel, phoneLabel;
    private EditText amountEdit;
    private Button btnPay;
    private ImageView btnBack, btnHome;

    private String name, phone, email, emailKey; // actual email + sanitized key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount);

        // Initialize UI components
        toLabel = findViewById(R.id.toLabel);
        phoneLabel = findViewById(R.id.phoneLabel);
        amountEdit = findViewById(R.id.amountEdit);
        btnPay = findViewById(R.id.btnPay);
        btnBack = findViewById(R.id.btnBack);
        btnHome = findViewById(R.id.btn_home);

        // Back button → return to previous screen
        btnBack.setOnClickListener(v -> finish());

        // Get data passed from ContactsAdapter
        Intent receivedIntent = getIntent();
        if (receivedIntent != null) {
            name = receivedIntent.getStringExtra("name");
            phone = receivedIntent.getStringExtra("phone");
            email = receivedIntent.getStringExtra("email");       // actual email
            emailKey = receivedIntent.getStringExtra("emailKey"); // sanitized emailKey
        }

        if (email == null || email.isEmpty() || emailKey == null || emailKey.isEmpty()) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Home button → go to DashboardActivity
        btnHome.setOnClickListener(v -> {
            Intent dashboardIntent = new Intent(AmountActivity.this, DashboardActivity.class);
            dashboardIntent.putExtra("email", email);       // pass actual email
            dashboardIntent.putExtra("emailKey", emailKey); // pass sanitized key
            startActivity(dashboardIntent);
            finish();
        });

        // Display selected contact info
        toLabel.setText(name != null ? name : "Unknown");
        phoneLabel.setText(phone != null ? phone : "N/A");

        // Amount input setup
        amountEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
        amountEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});

        // Pay button logic
        btnPay.setOnClickListener(v -> {
            String amtStr = amountEdit.getText().toString().trim();

            if (amtStr.isEmpty()) {
                Toast.makeText(this, "Enter amount", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent pinIntent = new Intent(AmountActivity.this, PinActivity.class);
            pinIntent.putExtra("name", name);
            pinIntent.putExtra("phone", phone);
            pinIntent.putExtra("amount", amtStr);
            pinIntent.putExtra("email", email);       // pass actual email
            pinIntent.putExtra("emailKey", emailKey); // pass sanitized key
            startActivity(pinIntent);
        });
    }
}

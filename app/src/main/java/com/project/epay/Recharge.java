package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class Recharge extends AppCompatActivity {

    private EditText editTextMobile;
    private LinearLayout opAirtel, opJio, opVi, opBsnl;
    private String selectedOperator = null;
    private String email;    // actual logged-in email
    private String emailKey; // sanitized key for Firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recharge);

        // Get email and emailKey from intent
        email = getIntent().getStringExtra("email");
        emailKey = getIntent().getStringExtra("emailKey");

        if (email == null || email.isEmpty()) {
            // If no email, go back to login
            Intent intent = new Intent(Recharge.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        editTextMobile = findViewById(R.id.editTextMobile);
        opAirtel = findViewById(R.id.opAirtel);
        opJio = findViewById(R.id.opJio);
        opVi = findViewById(R.id.opVi);
        opBsnl = findViewById(R.id.opBsnl);

        View.OnClickListener opClick = v -> {
            opAirtel.setBackgroundResource(R.drawable.recharge_operator_tile);
            opJio.setBackgroundResource(R.drawable.recharge_operator_tile);
            opVi.setBackgroundResource(R.drawable.recharge_operator_tile);
            opBsnl.setBackgroundResource(R.drawable.recharge_operator_tile);

            v.setBackgroundResource(R.drawable.recharge_selected_border);

            if (v == opAirtel) selectedOperator = "Airtel";
            else if (v == opJio) selectedOperator = "Jio";
            else if (v == opVi) selectedOperator = "Vi";
            else if (v == opBsnl) selectedOperator = "BSNL";
        };

        opAirtel.setOnClickListener(opClick);
        opJio.setOnClickListener(opClick);
        opVi.setOnClickListener(opClick);
        opBsnl.setOnClickListener(opClick);

        findViewById(R.id.btnProceedToPlan).setOnClickListener(v -> {
            String mobile = editTextMobile.getText().toString().trim();

            // Validation
            if (mobile.isEmpty()) {
                editTextMobile.setError("Enter mobile number");
                editTextMobile.requestFocus();
                return;
            }

            if (!mobile.matches("\\d+")) {
                editTextMobile.setError("Mobile number should contain only digits");
                editTextMobile.requestFocus();
                return;
            }

            if (mobile.length() != 10) {
                editTextMobile.setError("Enter a 10-digit mobile number");
                editTextMobile.requestFocus();
                return;
            }

            char firstDigit = mobile.charAt(0);
            if (firstDigit != '9' && firstDigit != '8' && firstDigit != '7' && firstDigit != '6') {
                editTextMobile.setError("Mobile number should start with 9, 8, 7, or 6");
                editTextMobile.requestFocus();
                return;
            }

            if (selectedOperator == null) selectedOperator = "Airtel";

            // Pass email and emailKey along with mobile & operator
            Intent intent = new Intent(Recharge.this, PlanActivity.class);
            intent.putExtra("mobile", mobile);
            intent.putExtra("operator", selectedOperator);
            intent.putExtra("email", email);       // pass actual email
            intent.putExtra("emailKey", emailKey); // pass sanitized key
            startActivity(intent);
        });

        // Home button → DashboardActivity
        findViewById(R.id.btn_home).setOnClickListener(v -> {
            Intent homeIntent = new Intent(Recharge.this, DashboardActivity.class);
            homeIntent.putExtra("email", email);       // pass actual email
            homeIntent.putExtra("emailKey", emailKey); // pass sanitized key
            startActivity(homeIntent);
            finish();
        });
    }
}

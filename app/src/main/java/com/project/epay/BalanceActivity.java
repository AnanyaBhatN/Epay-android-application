package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BalanceActivity extends AppCompatActivity {

    private TextView tvBalance;
    private ImageView btnHome;
    private String emailKey; // sanitized email
    private String email;    // actual email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance);

        tvBalance = findViewById(R.id.tvBalance);
        btnHome = findViewById(R.id.btn_home); // make sure your layout has an ImageView with id btnHome

        // Get balance and email info from intent
        double balance = getIntent().getDoubleExtra("balance", 0);
        emailKey = getIntent().getStringExtra("emailKey"); // sanitized email
        email = getIntent().getStringExtra("email");       // actual email

        tvBalance.setText("₹ " + String.format("%.2f", balance));

        // Home button click → DashboardActivity
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(BalanceActivity.this, DashboardActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("emailKey", emailKey);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        // Back also goes to DashboardActivity
        Intent intent = new Intent(BalanceActivity.this, DashboardActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("emailKey", emailKey);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}

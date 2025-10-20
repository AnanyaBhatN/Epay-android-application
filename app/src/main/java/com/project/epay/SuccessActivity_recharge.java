package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SuccessActivity_recharge extends AppCompatActivity {

    ImageView btnHome;
    private String emailKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_2);

        // Receive logged-in email from Intent
        emailKey = getIntent().getStringExtra("emailKey");
        if (emailKey == null) {
            // fallback, just finish if not available
            finish();
            return;
        }

        // Initialize Home button (make sure you have a button with this ID in your layout)
        btnHome = findViewById(R.id.btn_home);

        if (btnHome != null) {
            btnHome.setOnClickListener(v -> {
                Intent homeIntent = new Intent(SuccessActivity_recharge.this, DashboardActivity.class);
                homeIntent.putExtra("emailKey", emailKey);
                startActivity(homeIntent);
                finish();
            });
        }
    }
}

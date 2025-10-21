package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SuccessActivity_recharge extends AppCompatActivity {

    private ImageView btnHome;
    private String email;
    private String emailKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_2);

        // Get email and emailKey from intent
        email = getIntent().getStringExtra("email");       // actual email
        emailKey = getIntent().getStringExtra("emailKey"); // sanitized emailKey

        if (email == null || email.isEmpty()) {
            // If email missing, go back to login
            Intent intent = new Intent(SuccessActivity_recharge.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        btnHome = findViewById(R.id.btn_home);
        if (btnHome != null) {
            btnHome.setOnClickListener(v -> {
                Intent homeIntent = new Intent(SuccessActivity_recharge.this, DashboardActivity.class);
                homeIntent.putExtra("email", email);       // pass actual email
                homeIntent.putExtra("emailKey", emailKey); // pass sanitized key
                startActivity(homeIntent);
                finish();
            });
        }
    }
}

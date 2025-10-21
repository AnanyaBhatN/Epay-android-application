package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class SuccessActivity extends AppCompatActivity {

    private String email;
    private String emailKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        // Get email and emailKey from previous activity
        Intent intent = getIntent();
        email = intent.getStringExtra("email");       // actual email
        emailKey = intent.getStringExtra("emailKey"); // sanitized key

        if (email == null || email.isEmpty()) {
            // If email missing, go to login
            Intent loginIntent = new Intent(SuccessActivity.this, MainActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
            return;
        }

        // Home ImageView (baseline_home)
        ImageView btnHome = findViewById(R.id.btn_home);
        if (btnHome != null) {
            btnHome.setOnClickListener(v -> {
                Intent homeIntent = new Intent(SuccessActivity.this, DashboardActivity.class);
                homeIntent.putExtra("email", email);       // pass actual email
                homeIntent.putExtra("emailKey", emailKey); // pass sanitized key
                startActivity(homeIntent);
                finish();
            });
        }
    }

    @Override
    public void onBackPressed() {
        // Back press also goes to DashboardActivity
        if (email != null && !email.isEmpty()) {
            Intent intent = new Intent(SuccessActivity.this, DashboardActivity.class);
            intent.putExtra("email", email);       // pass actual email
            intent.putExtra("emailKey", emailKey); // pass sanitized key
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }
}

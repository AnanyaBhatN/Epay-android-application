package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class AddSuccessActivity extends AppCompatActivity {

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

        // Home button
        ImageView btnHome = findViewById(R.id.btn_home);
        btnHome.setOnClickListener(v -> goToDashboard());
    }

    @Override
    public void onBackPressed() {
        goToDashboard();
    }

    private void goToDashboard() {
        Intent homeIntent = new Intent(AddSuccessActivity.this, DashboardActivity.class);
        if (email != null && !email.isEmpty() && emailKey != null) {
            homeIntent.putExtra("email", email);
            homeIntent.putExtra("emailKey", emailKey);
        }
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(homeIntent);
        finish();
    }
}

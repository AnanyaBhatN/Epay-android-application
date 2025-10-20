package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class SuccessActivity extends AppCompatActivity {

    private String emailKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        // ✅ Get emailKey from previous activity
        Intent intent = getIntent();
        emailKey = intent.getStringExtra("emailKey");

        // ✅ Home ImageView (like baseline_home)
        ImageView btnHome = findViewById(R.id.btn_home);
        if (btnHome != null) {
            btnHome.setOnClickListener(v -> {
                Intent homeIntent = new Intent(SuccessActivity.this, DashboardActivity.class);
                homeIntent.putExtra("emailKey", emailKey);
                startActivity(homeIntent);
                finish();
            });
        }
    }

    @Override
    public void onBackPressed() {
        // ✅ Back press also takes you to Dashboard
        Intent intent = new Intent(SuccessActivity.this, DashboardActivity.class);
        intent.putExtra("emailKey", emailKey);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}

package com.project.epay;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;

public class rachana_SuccessActivity extends AppCompatActivity {
    Button btnGoHome;
    TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rachana_activity_success);

        // --- View Initialization (same as before) ---
        ImageView imgBack = findViewById(R.id.imgBack);
        ImageView imgClose = findViewById(R.id.imgClose);
        imgBack.setOnClickListener(v -> finishAffinity());
        imgClose.setOnClickListener(v -> finishAffinity());

        btnGoHome = findViewById(R.id.btnGoHome);
        tvInfo = findViewById(R.id.tvInfo);

        // --- Message Logic (same as before) ---
        String bank = getIntent().getStringExtra(rachana_SetPinActivity.EXTRA_BANK_NAME);
        String account = getIntent().getStringExtra(rachana_SetPinActivity.EXTRA_ACCOUNT);

        if (tvInfo != null && bank != null && account != null && account.length() > 3) {
            String lastThreeDigits = account.substring(account.length() - 3);
            StringBuilder dots = new StringBuilder();
            for (int i = 0; i < account.length() - 3; i++) {
                dots.append(".");
            }
            String maskedAccount = dots.toString() + lastThreeDigits;
            String successMessage = bank + " account " + maskedAccount + " has been successfully linked.";
            tvInfo.setText(successMessage);
        } else if (bank != null) {
            tvInfo.setText(bank + " account has been successfully linked.");
        }

        btnGoHome.setOnClickListener(v -> finishAffinity());

        // --- THIS IS THE UPDATED BACK PRESS LOGIC ---
        // Get the dispatcher
        OnBackPressedDispatcher dispatcher = getOnBackPressedDispatcher();

        // Create a callback that will finish the entire task (close the flow)
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // This is the same action as your old onBackPressed()
                finishAffinity();
            }
        };

        // Add the callback to the dispatcher
        dispatcher.addCallback(this, callback);
    }

    // The old onBackPressed() override is no longer needed and can be removed.
}
package com.project.epay;

import android.content.Intent; // <-- **1. ADD THIS IMPORT**
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AboutEpayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_epay); // Make sure this is the correct layout file

        // Setup the custom toolbar
        setupToolbar("About Epay");
    }

    /**
     * Setup toolbar with title, back button, and home icon.
     */
    private void setupToolbar(String title) {
        // Access toolbar views directly from XML
        ImageView backArrow = findViewById(R.id.btnBack);
        TextView titleText = findViewById(R.id.tv_toolbar_title);
        ImageView homeIcon = findViewById(R.id.iv_home_icon);

        // Set the toolbar title
        titleText.setText(title);

        // Back button click listener
        backArrow.setOnClickListener(v -> finish()); // Return to previous activity

        // ---*** THIS IS THE UPDATED SECTION ***---
        // Home icon click listener
        homeIcon.setOnClickListener(v -> {
            // Create an Intent to go to DashboardActivity
            Intent intent = new Intent(AboutEpayActivity.this, DashboardActivity.class);
            // Add flags to prevent creating a new Dashboard on top of the old one
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish(); // Close this activity
        });
        // ---*** END OF UPDATE ***---
    }
}
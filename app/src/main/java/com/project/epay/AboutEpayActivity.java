package com.project.epay;

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

        // Home icon click listener (placeholder)
        homeIcon.setOnClickListener(v ->
                        Toast.makeText(AboutEpayActivity.this, "Navigating to Home...", Toast.LENGTH_SHORT).show()
                // Example if you have MainActivity:
                // Intent intent = new Intent(AboutEpayActivity.this, MainActivity.class);
                // startActivity(intent);
        );
    }
}

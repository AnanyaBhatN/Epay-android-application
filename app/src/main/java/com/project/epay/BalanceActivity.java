package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class BalanceActivity extends AppCompatActivity {

    private ImageView backArrow;
    private ImageView homeIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout for this activity
        setContentView(R.layout.activity_balance);

        // Initialize the views from the header
        backArrow = findViewById(R.id.backArrow);
        homeIcon = findViewById(R.id.homeIcon);

        // Set click listener for the back arrow
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the MainActivity (PIN entry screen)
                navigateToMain();
            }
        });

        // Set click listener for the home icon
        homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the MainActivity (PIN entry screen)
                navigateToMain();
            }
        });
    }

    /**
     * Navigates the user back to the MainActivity.
     */
    private void navigateToMain() {
        Intent intent = new Intent(BalanceActivity.this, checkbalance.class);
        // Clear the activity stack and start a new task for MainActivity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Finish this activity
    }
}


package com.project.epay;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class HelpCenterActivity extends AppCompatActivity {

    private ImageView btnBackHelp, ivHomeHelp;
    private CardView card1, card2, card3, card4, card5, card6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_center);

        // Header buttons
        btnBackHelp = findViewById(R.id.btnBackHelp);
        ivHomeHelp = findViewById(R.id.ivHomeHelp);

        // FAQ cards
        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);
        card4 = findViewById(R.id.card4);
        card5 = findViewById(R.id.card5);
        card6 = findViewById(R.id.card6);

        // Back button
        btnBackHelp.setOnClickListener(v -> finish());

        // Home button
        ivHomeHelp.setOnClickListener(v ->
                Toast.makeText(HelpCenterActivity.this, "Home clicked", Toast.LENGTH_SHORT).show()
        );

        // Card clicks
        card1.setOnClickListener(v ->
                Toast.makeText(this, "How to send money clicked", Toast.LENGTH_SHORT).show());
        card2.setOnClickListener(v ->
                Toast.makeText(this, "Check transaction history clicked", Toast.LENGTH_SHORT).show());
        card3.setOnClickListener(v ->
                Toast.makeText(this, "Payment fails info clicked", Toast.LENGTH_SHORT).show());
        card4.setOnClickListener(v ->
                Toast.makeText(this, "Update phone number clicked", Toast.LENGTH_SHORT).show());
        card5.setOnClickListener(v ->
                Toast.makeText(this, "Reset PIN clicked", Toast.LENGTH_SHORT).show());
        card6.setOnClickListener(v ->
                Toast.makeText(this, "Contact support clicked", Toast.LENGTH_SHORT).show());
    }
}

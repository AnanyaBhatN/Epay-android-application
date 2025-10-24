package com.project.epay;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class HelpCenterActivity extends AppCompatActivity {

    private ImageView ivBackHelp, ivHelpHome;
    private CardView card1, card2, card3, card4, card5, card6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_center);

        // ✅ Initialize Views
        ivBackHelp = findViewById(R.id.iv_back_help);
        ivHelpHome = findViewById(R.id.iv_help_home); // ← NEW Home icon

        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);
        card4 = findViewById(R.id.card4);
        card5 = findViewById(R.id.card5);
        card6 = findViewById(R.id.card6);

        // ✅ Back Button Click
        ivBackHelp.setOnClickListener(view -> finish());

        // ✅ Home Button Click
        ivHelpHome.setOnClickListener(v ->
                Toast.makeText(HelpCenterActivity.this, "Home clicked", Toast.LENGTH_SHORT).show()
        );

        // ✅ Card Click Listeners
        card1.setOnClickListener(view ->
                Toast.makeText(HelpCenterActivity.this, "How to send money clicked", Toast.LENGTH_SHORT).show()
        );
        card2.setOnClickListener(view ->
                Toast.makeText(HelpCenterActivity.this, "Check transaction history clicked", Toast.LENGTH_SHORT).show()
        );
        card3.setOnClickListener(view ->
                Toast.makeText(HelpCenterActivity.this, "Payment fails info clicked", Toast.LENGTH_SHORT).show()
        );
        card4.setOnClickListener(view ->
                Toast.makeText(HelpCenterActivity.this, "Update phone number clicked", Toast.LENGTH_SHORT).show()
        );
        card5.setOnClickListener(view ->
                Toast.makeText(HelpCenterActivity.this, "Reset PIN clicked", Toast.LENGTH_SHORT).show()
        );
        card6.setOnClickListener(view ->
                Toast.makeText(HelpCenterActivity.this, "Contact support clicked", Toast.LENGTH_SHORT).show()
        );
    }
}

package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AmountActivity extends AppCompatActivity {

    private TextView toLabel, phoneLabel;
    private EditText amountEdit;
    private Button btnPay;
    private ImageView btnBack;
    private String name, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount);

        // Initialize views
        toLabel = findViewById(R.id.toLabel);
        phoneLabel = findViewById(R.id.phoneLabel);
        amountEdit = findViewById(R.id.amountEdit);
        btnPay = findViewById(R.id.btnPay);
        btnBack = findViewById(R.id.btnBack);

        // Back button -> go to Contacts
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(AmountActivity.this, Contacts.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Get contact details
        Intent intent = getIntent();
        if (intent != null) {
            name = intent.getStringExtra("name");
            phone = intent.getStringExtra("phone");
        }

        toLabel.setText(name != null ? name : "Unknown");
        phoneLabel.setText(phone != null ? phone : "N/A");

        // Restrict input to numbers only
        amountEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
        amountEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});

        // Pay button click
        btnPay.setOnClickListener(v -> {
            String amtStr = amountEdit.getText().toString().trim();

            if (amtStr.isEmpty()) {
                Toast.makeText(this, "Enter amount", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int amount = Integer.parseInt(amtStr);

                if (amount <= 0) {
                    Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (amount > 10000) {
                    Toast.makeText(this, "Amount cannot exceed ₹10000", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Send name, phone, and amount to PinActivity
                Intent i = new Intent(AmountActivity.this, PinActivity.class);
                i.putExtra("name", name);
                i.putExtra("phone", phone);
                i.putExtra("amount", amtStr);
                startActivity(i);

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid number", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AmountActivity.this, Contacts.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

}

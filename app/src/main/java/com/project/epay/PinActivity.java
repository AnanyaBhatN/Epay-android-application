package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class PinActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PIN_LENGTH = 4;
    private int pinIndex = 0;
    private List<View> pinDots = new ArrayList<>();
    private TextView tvTransactionDetails;

    private String name, phone, amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        // Initialize PIN dots
        pinDots.add(findViewById(R.id.pin_dot_1));
        pinDots.add(findViewById(R.id.pin_dot_2));
        pinDots.add(findViewById(R.id.pin_dot_3));
        pinDots.add(findViewById(R.id.pin_dot_4));

        tvTransactionDetails = findViewById(R.id.tv_transaction_details);

        // Get data from AmountActivity
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        phone = intent.getStringExtra("phone");
        amount = intent.getStringExtra("amount");

        // Dynamically set transaction info
        if (name != null && amount != null && !amount.isEmpty()) {
            tvTransactionDetails.setText("To " + name + " | Send Amt: ₹" + amount);
        } else if (phone != null && amount != null && !amount.isEmpty()) {
            tvTransactionDetails.setText("To " + phone + " | Send Amt: ₹" + amount);
        } else {
            tvTransactionDetails.setText("Enter PIN to confirm payment");
        }

        // Setup keypad listeners
        setupKeypadListeners();

        // Header and actions
        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_home).setOnClickListener(this);
        findViewById(R.id.btn_enter).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_clear).setOnClickListener(this);
    }

    private void setupKeypadListeners() {
        GridLayout keypad = findViewById(R.id.keypad_grid);
        for (int i = 0; i < keypad.getChildCount(); i++) {
            View child = keypad.getChildAt(i);
            child.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_back) {
            Intent intent = new Intent(PinActivity.this, AmountActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("phone", phone);
            startActivity(intent);
            finish();
        } else if (id == R.id.btn_home) {
            // TODO: Navigate to home
        } else if (id == R.id.btn_clear) {
            if (pinIndex > 0) {
                pinIndex--;
                pinDots.get(pinIndex).setBackgroundResource(R.drawable.pin_dot_empty);
            }
        } else if (id == R.id.btn_cancel) {
            resetPinDots();
        } else if (id == R.id.btn_enter) {
            // Validation: ensure 4 digits are entered
            if (pinIndex < PIN_LENGTH) {
                Toast.makeText(this, "Please enter 4-digit PIN", Toast.LENGTH_SHORT).show();
                return;
            }

            // PIN entered successfully → move to success screen
            Intent intent = new Intent(PinActivity.this, SuccessActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("amount", amount);
            startActivity(intent);
            finish();
        } else if (v instanceof Button) {
            if (pinIndex < PIN_LENGTH) {
                pinDots.get(pinIndex).setBackgroundResource(R.drawable.pin_dot_filled);
                pinIndex++;
            }
        }
    }

    private void resetPinDots() {
        for (View dot : pinDots) {
            dot.setBackgroundResource(R.drawable.pin_dot_empty);
        }
        pinIndex = 0;
    }

}

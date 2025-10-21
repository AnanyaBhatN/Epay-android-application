package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class checkbalance extends AppCompatActivity {

    private EditText pinEditText;
    private Button enterButton;

    // You can change the correct PIN here
    private static final String CORRECT_PIN = "1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkbalance);

        // Initialize views
        pinEditText = findViewById(R.id.pinEditText);
        enterButton = findViewById(R.id.enterButton);

        // Set a click listener on the Enter button
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredPin = pinEditText.getText().toString();
                validatePin(enteredPin);
            }
        });
    }

    /**
     * Validates the entered PIN.
     * @param pin The PIN entered by the user.
     */
    private void validatePin(String pin) {
        if (pin.equals(CORRECT_PIN)) {
            // If the PIN is correct, navigate to the BalanceActivity
            Intent intent = new Intent(checkbalance.this, BalanceActivity.class);
            startActivity(intent);
            // Finish this activity so the user can't go back to it with the back button
            finish();
        } else {
            // If the PIN is incorrect, show an error message
            Toast.makeText(checkbalance.this, "Invalid PIN", Toast.LENGTH_SHORT).show();
            // Clear the PIN field
            pinEditText.setText("");
        }
    }
}

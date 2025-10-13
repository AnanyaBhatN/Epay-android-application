package com.project.epay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity {

    private EditText etMobileNo, etEmail, etPassword;
    private Button btnSignup;
    private TextView tvLoginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content view to the XML layout
        setContentView(R.layout.signup);

        // 1. Initialize UI components using their defined IDs
        etMobileNo = findViewById(R.id.et_mobile_no);
        etEmail = findViewById(R.id.et_email_signup);
        etPassword = findViewById(R.id.et_password_signup);
        btnSignup = findViewById(R.id.btn_signup);
        tvLoginLink = findViewById(R.id.tv_login_link);

        // 2. Setup click listener for the Sign Up Button
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = etMobileNo.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (mobile.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mobile.length() != 10) {
                    Toast.makeText(SignupActivity.this, "Mobile number must be 10 digits.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // TODO: Implement actual user registration logic here
                Toast.makeText(SignupActivity.this, "Attempting to create account...", Toast.LENGTH_SHORT).show();

                // Example: Navigate to LoginActivity after successful registration
                // Intent loginIntent = new Intent(SignupActivity.this, LoginActivity.class);
                // startActivity(loginIntent);
                // finish();
            }
        });

        // 3. Setup click listener for the Log in Link
        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the LoginActivity
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Finish current activity to prevent going back to Signup
            }
        });
    }
}

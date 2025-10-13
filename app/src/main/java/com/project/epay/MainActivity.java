package com.project.epay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignupLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content view to the XML layout
        setContentView(R.layout.activity_main);

        // 1. Initialize UI components using their defined IDs
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvSignupLink = findViewById(R.id.tv_signup_link);

        // 2. Setup click listener for the Login Button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter email and password.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // TODO: Implement actual authentication logic here (e.g., Firebase, API call)
                Toast.makeText(MainActivity.this, "Attempting to log in...", Toast.LENGTH_SHORT).show();

                // Example: Navigate to a main activity upon successful login
                // Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                // startActivity(mainIntent);
                // finish();
            }
        });

        // 3. Setup click listener for the Sign Up Link
        tvSignupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the SignupActivity
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
                // Optionally, finish the current activity if you don't want to return to it
                // finish();
            }
        });
    }
}

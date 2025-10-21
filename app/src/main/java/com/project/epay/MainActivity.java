package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
// --- ADD THESE IMPORTS ---
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
// ---
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private EditText email, password;
    private Button loginButton;
    private TextView signUpLink;
    private DatabaseReference databaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseUsers = FirebaseDatabase.getInstance().getReference("Users");

        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        loginButton = findViewById(R.id.btn_login);
        signUpLink = findViewById(R.id.tv_login_signup);

        loginButton.setOnClickListener(v -> {
            String emailInput = email.getText().toString().trim();
            String passwordInput = password.getText().toString().trim();

            if (emailInput.isEmpty() || passwordInput.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter email and password.", Toast.LENGTH_SHORT).show();
                return;
            }

            String sanitizedEmail = emailInput.replace(".", "_");

            databaseUsers.child(sanitizedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        Toast.makeText(MainActivity.this, "User not found. Please sign up first.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    User user = snapshot.getValue(User.class);
                    if (user == null) {
                        Toast.makeText(MainActivity.this, "User data error!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (passwordInput.equals(user.password)) {
                        Toast.makeText(MainActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                        // --- THIS IS THE NEW CODE TO FIX THE ERROR ---
                        // Save the user's *original* email to SharedPreferences
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        SharedPreferences.Editor editor = prefs.edit();

                        // This key MUST match the one in rachana_SetPinActivity
                        editor.putString("user_email_key", emailInput);
                        editor.apply();
                        // --- END OF NEW CODE ---

                        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                        intent.putExtra("email", user.email);       // display only
                        intent.putExtra("emailKey", sanitizedEmail); // Firebase key
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Incorrect password.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        signUpLink.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, Signup_Activity.class));
        });
    }
}
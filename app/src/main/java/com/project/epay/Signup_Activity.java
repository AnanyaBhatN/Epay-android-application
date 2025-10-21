package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
// --- ADD THESE IMPORTS ---
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.CheckBox;
// ---
import android.util.Patterns;
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

public class Signup_Activity extends AppCompatActivity {

    private EditText name, mobile, email, password;
    private Button signUpButton;
    private TextView loginLink;

    // --- ADD THIS ---
    private CheckBox showPasswordCheckbox;

    private DatabaseReference databaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Database reference
        databaseUsers = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize Views
        name = findViewById(R.id.et_signup_name);
        mobile = findViewById(R.id.et_signup_mobile);
        email = findViewById(R.id.et_signup_email);
        password = findViewById(R.id.et_signup_password);
        signUpButton = findViewById(R.id.btn_signup);
        loginLink = findViewById(R.id.tv_login_signup);

        // --- ADD THIS CODE FOR SHOW PASSWORD ---
        showPasswordCheckbox = findViewById(R.id.cb_show_password); // Make sure this ID is in your XML
        showPasswordCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show password
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                // Hide password
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            // Move cursor to the end
            password.setSelection(password.length());
        });
        // --- END OF NEW CODE ---

        // Sign Up Button Click
        signUpButton.setOnClickListener(v -> {
            if (validateInputs()) {
                checkUserExistsAndSave();
            }
        });

        // Login Link Click
        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(Signup_Activity.this, MainActivity.class));
            finish();
        });
    }

    // Check if email already exists
    private void checkUserExistsAndSave() {
        String emailId = email.getText().toString().trim();
        String sanitizedEmail = emailId.replace(".", "_"); // sanitize for Firebase key

        databaseUsers.child(sanitizedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(Signup_Activity.this, "Email already registered!", Toast.LENGTH_LONG).show();
                } else {
                    saveUserToFirebase(sanitizedEmail);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(Signup_Activity.this, "Database error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Save user details to Firebase
    private void saveUserToFirebase(String sanitizedEmail) {
        String fullName = name.getText().toString().trim();
        String mobileNo = mobile.getText().toString().trim();
        String emailId = email.getText().toString().trim();
        String pass = password.getText().toString().trim();

        // Store as an object or direct key-value pairs
        DatabaseReference userRef = databaseUsers.child(sanitizedEmail);
        userRef.child("name").setValue(fullName);
        userRef.child("mobile").setValue(mobileNo);
        userRef.child("email").setValue(emailId);
        userRef.child("password").setValue(pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Signup_Activity.this, "Sign up successful!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(Signup_Activity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(Signup_Activity.this, "Sign up failed. Try again.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Input Validation
    private boolean validateInputs() {
        String fullName = name.getText().toString().trim();
        String mobileNo = mobile.getText().toString().trim();
        String emailId = email.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if (fullName.isEmpty()) {
            name.setError("Name is required");
            name.requestFocus();
            return false;
        }

        if (mobileNo.isEmpty()) {
            mobile.setError("Mobile number is required");
            mobile.requestFocus();
            return false;
        } else if (mobileNo.length() != 10 || !mobileNo.matches("[0-9]+")) {
            mobile.setError("Enter a valid 10-digit number");
            mobile.requestFocus();
            return false;
        }

        if (emailId.isEmpty()) {
            email.setError("Email is required");
            email.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailId).matches()) {
            email.setError("Enter a valid email address");
            email.requestFocus();
            return false;
        }

        if (pass.isEmpty()) {
            password.setError("Password is required");
            password.requestFocus();
            return false;
        } else if (pass.length() < 6) {
            password.setError("Password must be at least 6 characters");
            password.requestFocus();
            return false;
        }

        return true;
    }
}


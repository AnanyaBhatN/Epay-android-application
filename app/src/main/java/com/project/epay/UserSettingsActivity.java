package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserSettingsActivity extends AppCompatActivity {

    private TextView nameTextView, phoneTextView, emailTextView;
    private String emailKey;  // passed from DashboardActivity
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_setting);

        // Firebase reference
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize Views
        nameTextView = findViewById(R.id.tv_user_name);
        phoneTextView = findViewById(R.id.tv_phone_number);
        emailTextView = findViewById(R.id.tv_avatar); // we’ll use avatar text to show first letter

        // Toolbar
        ImageView backArrow = findViewById(R.id.btnBack);
        TextView titleText = findViewById(R.id.tv_toolbar_title);
        ImageView homeIcon = findViewById(R.id.iv_home_icon);

        // Cards
        LinearLayout aboutEpayLayout = findViewById(R.id.ll_about_epay);
        LinearLayout helpCenterLayout = findViewById(R.id.ll_help_center);
        Button logoutButton = findViewById(R.id.btn_logout);

        // Get email key passed from Dashboard
        emailKey = getIntent().getStringExtra("emailKey");

        if (emailKey == null || emailKey.isEmpty()) {
            Toast.makeText(this, "Error: No user email passed!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Replace '.' with '_' to match Firebase structure
        String sanitizedEmail = emailKey.replace(".", "_");

        // ✅ Fetch user data
        usersRef.child(sanitizedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String mobile = snapshot.child("mobile").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);

                    nameTextView.setText(name != null ? name : "Name not found");
                    phoneTextView.setText(mobile != null ? mobile : "Phone not found");

                    // Show first letter of name or default "U"
                    if (name != null && !name.isEmpty()) {
                        emailTextView.setText(String.valueOf(name.charAt(0)).toUpperCase());
                    } else {
                        emailTextView.setText("U");
                    }
                } else {
                    Toast.makeText(UserSettingsActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(UserSettingsActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Toolbar actions
        titleText.setText("User Settings");
        backArrow.setOnClickListener(v -> finish());

        homeIcon.setOnClickListener(v -> {
            Intent intent = new Intent(UserSettingsActivity.this, DashboardActivity.class);
            intent.putExtra("emailKey", emailKey);
            startActivity(intent);
            finish();
        });

        // About Epay
        aboutEpayLayout.setOnClickListener(v -> {
            Intent intent = new Intent(UserSettingsActivity.this, AboutEpayActivity.class);
            startActivity(intent);
        });

        // Help Center
        helpCenterLayout.setOnClickListener(v -> {
            Intent intent = new Intent(UserSettingsActivity.this, HelpCenterActivity.class);
            startActivity(intent);
        });

        // Logout
        logoutButton.setOnClickListener(v -> {
            Toast.makeText(UserSettingsActivity.this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UserSettingsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}

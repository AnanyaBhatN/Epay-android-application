package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback; // <-- **1. ADD THIS IMPORT**
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PlanActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PlanAdapter adapter;
    private Button nextButton;
    private ImageView btnHome;
    private ImageView btnBack; // <-- **2. ADD THIS DECLARATION**

    private String mobile, operator, email, emailKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        recyclerView = findViewById(R.id.recyclerPlans);
        nextButton = findViewById(R.id.nextButton);
        btnHome = findViewById(R.id.btn_home);
        btnBack = findViewById(R.id.btnBack); // <-- **3. FIND THE BACK BUTTON BY ID**

        // Receive data from RechargeActivity
        mobile = getIntent().getStringExtra("mobile");
        operator = getIntent().getStringExtra("operator");
        email = getIntent().getStringExtra("email");       // actual email
        emailKey = getIntent().getStringExtra("emailKey"); // sanitized key

        if (mobile == null || operator == null || email == null || emailKey == null) {
            // If any required data missing, go back to login
            Intent intent = new Intent(PlanActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Load sample plans (unchanged)
        List<Plan> planList = new ArrayList<>();
        planList.add(new Plan("₹19", "100MB • 1 day • 50 SMS", "1 day"));
        planList.add(new Plan("₹21", "150MB • 1 day • 50 SMS", "1 day"));
        planList.add(new Plan("₹35", "250MB • 2 days • 50 SMS", "2 days"));
        planList.add(new Plan("₹49", "500MB • 2 days • 50 SMS", "2 days"));
        planList.add(new Plan("₹199", "1GB/day • Unlimited Calls • 50 SMS/day", "28 days"));
        planList.add(new Plan("₹299", "2GB/day • Unlimited Calls • 100 SMS/day", "28 days"));
        planList.add(new Plan("₹399", "2.5GB/day • Unlimited Calls • 100 SMS/day", "28 days"));
        planList.add(new Plan("₹499", "3GB/day • Unlimited Calls • 100 SMS/day", "56 days"));
        planList.add(new Plan("₹599", "3.5GB/day • Unlimited Calls • 100 SMS/day", "56 days"));
        planList.add(new Plan("₹699", "4GB/day • Unlimited Calls • 100 SMS/day", "56 days"));
        planList.add(new Plan("₹799", "2.5GB/day • Unlimited Calls • 100 SMS/day", "84 days"));
        planList.add(new Plan("₹899", "3GB/day • Unlimited Calls • 100 SMS/day", "84 days"));
        planList.add(new Plan("₹999", "4GB/day • Unlimited Calls • 100 SMS/day", "84 days"));
        planList.add(new Plan("₹1099", "5GB/day • Unlimited Calls • 100 SMS/day", "84 days"));

        adapter = new PlanAdapter(this, planList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Next button → go to PIN activity (unchanged)
        nextButton.setOnClickListener(v -> {
            Plan selectedPlan = adapter.getSelectedPlan();
            if (selectedPlan != null) {
                Intent intent = new Intent(PlanActivity.this, Pinactivity_recharge.class);
                intent.putExtra("mobile", mobile);
                intent.putExtra("operator", operator);
                intent.putExtra("amount", selectedPlan.getPrice().replace("₹", ""));
                intent.putExtra("email", email);       // pass actual email
                intent.putExtra("emailKey", emailKey); // pass sanitized key
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please select a plan", Toast.LENGTH_SHORT).show();
            }
        });

        // --- **4. ADD THIS CLICK LISTENER FOR THE BACK ARROW** ---
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                // finish() closes this page and goes back to Recharge
                finish();
            });
        }

        // --- **5. UPDATED HOME BUTTON LISTENER** ---
        if (btnHome != null) {
            btnHome.setOnClickListener(v -> {
                Intent homeIntent = new Intent(PlanActivity.this, DashboardActivity.class);
                homeIntent.putExtra("email", email);       // pass actual email
                homeIntent.putExtra("emailKey", emailKey); // pass sanitized key
                // Add flags to prevent duplicate Dashboards
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(homeIntent);
                finish();
            });
        }

        // --- **6. ADD THIS FOR THE PHYSICAL BACK BUTTON** ---
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // This closes the activity and returns to the previous page
                finish();
            }
        });
    }
}
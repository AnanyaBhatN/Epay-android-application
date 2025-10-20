package com.project.epay;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PlanActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PlanAdapter adapter;
    private Button nextButton;
    ImageView btnHome;

    private String mobile, operator, emailKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        recyclerView = findViewById(R.id.recyclerPlans);
        nextButton = findViewById(R.id.nextButton);
        btnHome = findViewById(R.id.btn_home); // make sure you have this button in layout

        // Receive data from RechargeActivity
        mobile = getIntent().getStringExtra("mobile");
        operator = getIntent().getStringExtra("operator");
        emailKey = getIntent().getStringExtra("emailKey"); // logged-in user email

        if (mobile == null || operator == null || emailKey == null) {
            finish();
            return;
        }

        // Load sample plans
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

        nextButton.setOnClickListener(v -> {
            Plan selectedPlan = adapter.getSelectedPlan();
            if (selectedPlan != null) {
                Intent intent = new Intent(PlanActivity.this, Pinactivity_recharge.class);
                intent.putExtra("mobile", mobile);
                intent.putExtra("operator", operator);
                intent.putExtra("amount", selectedPlan.getPrice().replace("₹", ""));
                intent.putExtra("emailKey", emailKey); // pass logged-in email
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please select a plan", Toast.LENGTH_SHORT).show();
            }
        });

        // Home button click listener
        if (btnHome != null) {
            btnHome.setOnClickListener(v -> {
                Intent homeIntent = new Intent(PlanActivity.this, DashboardActivity.class);
                homeIntent.putExtra("emailKey", emailKey);
                startActivity(homeIntent);
                finish();
            });
        }
    }
}
